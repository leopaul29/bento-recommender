package com.leopaul29.bento.ordering.application;

import com.leopaul29.bento.ordering.domain.BentoId;
import com.leopaul29.bento.ordering.domain.CustomerId;
import com.leopaul29.bento.ordering.domain.DomainRuleViolation;
import com.leopaul29.bento.ordering.domain.Money;
import com.leopaul29.bento.ordering.domain.Order;
import com.leopaul29.bento.ordering.domain.OrderId;
import com.leopaul29.bento.ordering.domain.Quantity;
import com.leopaul29.bento.ordering.domain.ShopDay;
import com.leopaul29.bento.ordering.domain.ports.BentoCatalogue;
import com.leopaul29.bento.ordering.domain.ports.OrderRepository;
import com.leopaul29.bento.ordering.domain.ports.ShopDayRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Turns a request into a placed order.
 *
 * <p>The use case does the reading — the day's menu, today's price — and the aggregate does the
 * deciding. Note where the price comes from: the catalogue, once, here, and then it belongs to
 * the line (I3).
 */
public final class PlaceOrder {

    private final OrderRepository orders;
    private final ShopDayRepository shopDays;
    private final BentoCatalogue catalogue;
    private final Clock clock;

    public PlaceOrder(OrderRepository orders, ShopDayRepository shopDays,
                      BentoCatalogue catalogue, Clock clock) {
        this.orders = orders;
        this.shopDays = shopDays;
        this.catalogue = catalogue;
        this.clock = clock;
    }

    public record Item(BentoId bentoId, Quantity quantity) {}

    public record Command(CustomerId customerId, LocalDate serviceDate, List<Item> items) {}

    public OrderId handle(Command command) {
        ShopDay day = shopDays.findByDate(command.serviceDate())
                .orElseThrow(() -> new DomainRuleViolation(
                        "the shop is not serving on " + command.serviceDate()));

        Order order = Order.draft(OrderId.newId(), command.customerId(), command.serviceDate());

        for (Item item : command.items()) {
            if (!day.offers(item.bentoId())) {
                throw new DomainRuleViolation(
                        item.bentoId() + " is not on the menu for " + command.serviceDate());
            }
            Money price = catalogue.priceOf(item.bentoId())
                    .orElseThrow(() -> new DomainRuleViolation(
                            "no price for " + item.bentoId()));
            order.addLine(item.bentoId(), item.quantity(), price);
        }

        Instant now = clock.instant();
        order.place(now, day.orderingClosesAt());
        orders.save(order);
        return order.id();
    }
}
