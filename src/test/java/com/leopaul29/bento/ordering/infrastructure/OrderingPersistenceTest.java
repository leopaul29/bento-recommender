package com.leopaul29.bento.ordering.infrastructure;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.ordering.domain.BentoId;
import com.leopaul29.bento.ordering.domain.CustomerId;
import com.leopaul29.bento.ordering.domain.Money;
import com.leopaul29.bento.ordering.domain.Order;
import com.leopaul29.bento.ordering.domain.OrderId;
import com.leopaul29.bento.ordering.domain.OrderStatus;
import com.leopaul29.bento.ordering.domain.Quantity;
import com.leopaul29.bento.ordering.domain.ShopDay;
import com.leopaul29.bento.ordering.domain.ports.BentoCatalogue;
import com.leopaul29.bento.ordering.domain.ports.OrderRepository;
import com.leopaul29.bento.ordering.domain.ports.ShopDayRepository;
import com.leopaul29.bento.repositories.BentoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The adapters, against a real JPA stack.
 *
 * <p>This is where Phase 2 earns its keep: the aggregate has no setters and a package-private line
 * constructor, the entity needs a no-arg constructor and mutable fields, and the two still
 * round-trip. If the mapping is wrong, nothing else in the context works and no unit test with an
 * in-memory fake would have noticed.
 */
@SpringBootTest
@ActiveProfiles("test")
class OrderingPersistenceTest {

    private static final LocalDate SERVICE_DATE = LocalDate.of(2026, 10, 9);
    private static final Instant CUTOFF = Instant.parse("2026-10-09T01:00:00Z");
    private static final Instant NOW = Instant.parse("2026-10-09T00:30:00Z");

    @Autowired
    private OrderRepository orders;

    @Autowired
    private ShopDayRepository shopDays;

    @Autowired
    private BentoCatalogue catalogue;

    @Autowired
    private BentoRepository bentos;

    @Test
    @DisplayName("an order survives the round trip through JPA with its captured prices")
    void anOrderSurvivesTheRoundTripThroughJpa() {
        Order order = Order.draft(OrderId.newId(), CustomerId.of(42L), SERVICE_DATE);
        order.addLine(BentoId.of(1L), Quantity.of(2), Money.yen(500));
        order.addLine(BentoId.of(2L), Quantity.of(1), Money.yen(780));
        order.place(NOW, CUTOFF);

        orders.save(order);

        Order loaded = orders.findById(order.id()).orElseThrow();

        assertThat(loaded.id()).isEqualTo(order.id());
        assertThat(loaded.customerId()).isEqualTo(CustomerId.of(42L));
        assertThat(loaded.serviceDate()).isEqualTo(SERVICE_DATE);
        assertThat(loaded.status()).isEqualTo(OrderStatus.PLACED);
        assertThat(loaded.total()).isEqualTo(Money.yen(2 * 500 + 780));
        assertThat(loaded.lines()).containsExactlyElementsOf(order.lines());
    }

    @Test
    @DisplayName("saving an order twice updates the row rather than inserting a second one")
    void savingAnOrderTwiceUpdatesTheRow() {
        Order order = Order.draft(OrderId.newId(), CustomerId.of(7L), SERVICE_DATE);
        order.addLine(BentoId.of(1L), Quantity.of(1), Money.yen(500));
        order.place(NOW, CUTOFF);
        orders.save(order);

        order.accept(NOW);
        orders.save(order);

        Order loaded = orders.findById(order.id()).orElseThrow();
        assertThat(loaded.status()).isEqualTo(OrderStatus.ACCEPTED);
        assertThat(loaded.lines()).hasSize(1);
    }

    @Test
    @DisplayName("a shop day round-trips, and a reservation is persisted")
    void aShopDayRoundTripsAndAReservationIsPersisted() {
        LocalDate date = SERVICE_DATE.plusDays(1);
        shopDays.save(ShopDay.of(date, CUTOFF, Map.of(BentoId.of(1L), 5, BentoId.of(2L), 2)));

        ShopDay loaded = shopDays.findByDate(date).orElseThrow();
        assertThat(loaded.date()).isEqualTo(date);
        assertThat(loaded.orderingClosesAt()).isEqualTo(CUTOFF);
        assertThat(loaded.remaining(BentoId.of(1L))).isEqualTo(5);
        assertThat(loaded.offers(BentoId.of(99L))).isFalse();

        loaded.reserve(BentoId.of(1L), Quantity.of(3));
        shopDays.save(loaded);

        assertThat(shopDays.findByDate(date).orElseThrow().remaining(BentoId.of(1L))).isEqualTo(2);
    }

    @Test
    @DisplayName("the catalogue adapter reads a price, and says nothing for an unpriced bento")
    void theCatalogueAdapterReadsAPrice() {
        Bento priced = bentos.save(Bento.builder()
                .name("Karaage bento").description("fried chicken").calorie(700).priceYen(620)
                .ingredients(Set.of()).tags(Set.of())
                .build());
        Bento unpriced = bentos.save(Bento.builder()
                .name("Not for sale").description("staff lunch").calorie(500)
                .ingredients(Set.of()).tags(Set.of())
                .build());

        assertThat(catalogue.priceOf(BentoId.of(priced.getId()))).contains(Money.yen(620));
        assertThat(catalogue.priceOf(BentoId.of(unpriced.getId()))).isEmpty();
        assertThat(catalogue.priceOf(BentoId.of(999_999L))).isEmpty();
    }

    @Test
    @DisplayName("the ports are wired to the adapters, not to the fakes")
    void thePortsAreWiredToTheAdapters() {
        assertThat(orders.getClass().getName()).contains("JpaOrderRepository");
        assertThat(shopDays.getClass().getName()).contains("JpaShopDayRepository");
        assertThat(catalogue.getClass().getName()).contains("BentoCataloguePriceAdapter");
    }
}
