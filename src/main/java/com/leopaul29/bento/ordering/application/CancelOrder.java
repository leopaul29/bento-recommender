package com.leopaul29.bento.ordering.application;

import com.leopaul29.bento.ordering.domain.DomainRuleViolation;
import com.leopaul29.bento.ordering.domain.Order;
import com.leopaul29.bento.ordering.domain.OrderId;
import com.leopaul29.bento.ordering.domain.ports.OrderRepository;

import java.time.Clock;

/**
 * Cancels an order, or refuses to. The rule about <em>when</em> that is allowed lives in the
 * aggregate, not here — this class would be the wrong place to be able to make an exception.
 */
public final class CancelOrder {

    private final OrderRepository orders;
    private final Clock clock;

    public CancelOrder(OrderRepository orders, Clock clock) {
        this.orders = orders;
        this.clock = clock;
    }

    public void handle(OrderId orderId) {
        Order order = orders.findById(orderId)
                .orElseThrow(() -> new DomainRuleViolation("no order " + orderId));
        order.cancel(clock.instant());
        orders.save(order);
    }
}
