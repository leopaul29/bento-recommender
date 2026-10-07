package com.leopaul29.bento.ordering.domain;

/** A status change the order's lifecycle does not allow. */
public class IllegalOrderTransition extends DomainRuleViolation {

    public IllegalOrderTransition(OrderStatus from, OrderStatus to) {
        super("an order cannot go from " + from + " to " + to);
    }
}
