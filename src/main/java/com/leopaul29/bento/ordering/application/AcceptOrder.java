package com.leopaul29.bento.ordering.application;

import com.leopaul29.bento.ordering.domain.DomainRuleViolation;
import com.leopaul29.bento.ordering.domain.Order;
import com.leopaul29.bento.ordering.domain.OrderId;
import com.leopaul29.bento.ordering.domain.OrderLine;
import com.leopaul29.bento.ordering.domain.ShopDay;
import com.leopaul29.bento.ordering.domain.ports.OrderRepository;
import com.leopaul29.bento.ordering.domain.ports.ShopDayRepository;

import java.time.Clock;

/**
 * The shop takes the order on, which is the moment the stock is really committed.
 *
 * <p>Two aggregates change here, so the ordering matters: reserve first, accept second. If the
 * stock is short, {@link ShopDay#reserve} throws and the order stays PLACED rather than being
 * accepted against bentos that do not exist.
 */
public final class AcceptOrder {

    private final OrderRepository orders;
    private final ShopDayRepository shopDays;
    private final Clock clock;

    public AcceptOrder(OrderRepository orders, ShopDayRepository shopDays, Clock clock) {
        this.orders = orders;
        this.shopDays = shopDays;
        this.clock = clock;
    }

    public void handle(OrderId orderId) {
        Order order = orders.findById(orderId)
                .orElseThrow(() -> new DomainRuleViolation("no order " + orderId));

        ShopDay day = shopDays.findByDate(order.serviceDate())
                .orElseThrow(() -> new DomainRuleViolation(
                        "the shop is not serving on " + order.serviceDate()));

        for (OrderLine line : order.lines()) {
            day.reserve(line.bentoId(), line.quantity());
        }

        order.accept(clock.instant());

        shopDays.save(day);
        orders.save(order);
    }
}
