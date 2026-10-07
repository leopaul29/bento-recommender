package com.leopaul29.bento.ordering.application;

import com.leopaul29.bento.ordering.domain.BentoId;
import com.leopaul29.bento.ordering.domain.CustomerId;
import com.leopaul29.bento.ordering.domain.DomainRuleViolation;
import com.leopaul29.bento.ordering.domain.Money;
import com.leopaul29.bento.ordering.domain.Order;
import com.leopaul29.bento.ordering.domain.OrderId;
import com.leopaul29.bento.ordering.domain.OrderStatus;
import com.leopaul29.bento.ordering.domain.Quantity;
import com.leopaul29.bento.ordering.domain.ShopDay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The invariants that need two aggregates or an outside read, so they cannot be shown on
 * {@link Order} alone: I3 (the price is captured, not looked up), I6 (the day's menu) and
 * I10 (the day's stock).
 */
class OrderingUseCasesTest {

    private static final LocalDate SERVICE_DATE = LocalDate.of(2026, 10, 9);
    private static final Instant CUTOFF = Instant.parse("2026-10-09T01:00:00Z");
    private static final Instant NOW = Instant.parse("2026-10-09T00:30:00Z");

    private static final BentoId KARAAGE = BentoId.of(1L);
    private static final BentoId SALMON = BentoId.of(2L);
    private static final BentoId OFF_MENU = BentoId.of(99L);

    private InMemoryOrders orders;
    private InMemoryShopDays shopDays;
    private MutableCatalogue catalogue;
    private PlaceOrder placeOrder;
    private AcceptOrder acceptOrder;
    private CancelOrder cancelOrder;

    @BeforeEach
    void setUp() {
        orders = new InMemoryOrders();
        shopDays = new InMemoryShopDays();
        catalogue = new MutableCatalogue();
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);

        shopDays.save(ShopDay.of(SERVICE_DATE, CUTOFF, Map.of(KARAAGE, 5, SALMON, 2)));
        catalogue.setPrice(KARAAGE, Money.yen(500));
        catalogue.setPrice(SALMON, Money.yen(780));

        placeOrder = new PlaceOrder(orders, shopDays, catalogue, clock);
        acceptOrder = new AcceptOrder(orders, shopDays, clock);
        cancelOrder = new CancelOrder(orders, clock);
    }

    private OrderId place(BentoId bentoId, int quantity) {
        return placeOrder.handle(new PlaceOrder.Command(
                CustomerId.of(1L), SERVICE_DATE,
                List.of(new PlaceOrder.Item(bentoId, Quantity.of(quantity)))));
    }

    @Test
    @DisplayName("I3 — repricing the catalogue afterwards does not touch a placed order")
    void aLaterCataloguePriceChangeDoesNotAlterAPlacedOrder() {
        OrderId id = place(KARAAGE, 2);
        Money totalWhenPlaced = orders.findById(id).orElseThrow().total();
        assertThat(totalWhenPlaced).isEqualTo(Money.yen(1000));

        // The shop raises the price the next morning.
        catalogue.setPrice(KARAAGE, Money.yen(900));

        Order reloaded = orders.findById(id).orElseThrow();
        assertThat(reloaded.total()).isEqualTo(totalWhenPlaced);
        assertThat(reloaded.lines()).singleElement()
                .satisfies(line -> assertThat(line.unitPrice()).isEqualTo(Money.yen(500)));
    }

    @Test
    @DisplayName("I6 — a bento that is not on the day's menu cannot be ordered")
    void orderingABentoNotOnTheDaysMenuIsRefused() {
        catalogue.setPrice(OFF_MENU, Money.yen(600)); // priced, but not served that day

        assertThatThrownBy(() -> place(OFF_MENU, 1))
                .isInstanceOf(DomainRuleViolation.class)
                .hasMessageContaining("not on the menu");

        assertThat(orders.size()).isZero();
    }

    @Test
    @DisplayName("I10 — accepting more than the remaining stock is refused, and stock never goes negative")
    void acceptingMoreThanTheRemainingStockIsRefused() {
        OrderId first = place(SALMON, 2);   // takes both
        OrderId second = place(SALMON, 1);  // placed while stock looked fine

        acceptOrder.handle(first);
        assertThat(shopDays.findByDate(SERVICE_DATE).orElseThrow().remaining(SALMON)).isZero();

        assertThatThrownBy(() -> acceptOrder.handle(second))
                .isInstanceOf(DomainRuleViolation.class)
                .hasMessageContaining("left");

        // The refused order stayed PLACED, and the count did not go below zero.
        assertThat(orders.findById(second).orElseThrow().status()).isEqualTo(OrderStatus.PLACED);
        assertThat(shopDays.findByDate(SERVICE_DATE).orElseThrow().remaining(SALMON)).isZero();
    }

    @Test
    @DisplayName("the happy path: place, accept, and the stock moves once")
    void placingThenAcceptingTakesStockOnce() {
        OrderId id = place(KARAAGE, 3);

        assertThat(orders.findById(id).orElseThrow().status()).isEqualTo(OrderStatus.PLACED);
        assertThat(shopDays.findByDate(SERVICE_DATE).orElseThrow().remaining(KARAAGE)).isEqualTo(5);

        acceptOrder.handle(id);

        assertThat(orders.findById(id).orElseThrow().status()).isEqualTo(OrderStatus.ACCEPTED);
        assertThat(shopDays.findByDate(SERVICE_DATE).orElseThrow().remaining(KARAAGE)).isEqualTo(2);
    }

    @Test
    @DisplayName("cancelling a placed order goes through the use case")
    void cancellingAPlacedOrder() {
        OrderId id = place(KARAAGE, 1);

        cancelOrder.handle(id);

        assertThat(orders.findById(id).orElseThrow().status()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("the use cases refuse what they cannot find")
    void theUseCasesRefuseWhatTheyCannotFind() {
        OrderId unknown = OrderId.newId();

        assertThatThrownBy(() -> acceptOrder.handle(unknown))
                .isInstanceOf(DomainRuleViolation.class).hasMessageContaining("no order");
        assertThatThrownBy(() -> cancelOrder.handle(unknown))
                .isInstanceOf(DomainRuleViolation.class).hasMessageContaining("no order");

        assertThatThrownBy(() -> placeOrder.handle(new PlaceOrder.Command(
                CustomerId.of(1L), SERVICE_DATE.plusDays(30),
                List.of(new PlaceOrder.Item(KARAAGE, Quantity.of(1))))))
                .isInstanceOf(DomainRuleViolation.class).hasMessageContaining("not serving");

        // On the menu for the day, but the catalogue has no price for it.
        shopDays.save(ShopDay.of(SERVICE_DATE, CUTOFF, Map.of(OFF_MENU, 1)));
        assertThatThrownBy(() -> place(OFF_MENU, 1))
                .isInstanceOf(DomainRuleViolation.class).hasMessageContaining("no price");
    }
}
