package com.leopaul29.bento.ordering.domain;

import java.util.Map;
import java.util.Set;

/**
 * The order lifecycle, with the legal moves written down once.
 *
 * <p>Encoding the transitions here is what makes I7 ("nothing skips a step") and I8
 * ("cancelling once preparation started is refused") a single table instead of a condition
 * scattered across every method that changes state.
 */
public enum OrderStatus {

    DRAFT,
    PLACED,
    ACCEPTED,
    PREPARING,
    READY,
    COLLECTED,
    CANCELLED;

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = Map.of(
            DRAFT, Set.of(PLACED),
            PLACED, Set.of(ACCEPTED, CANCELLED),
            ACCEPTED, Set.of(PREPARING, CANCELLED),
            // No CANCELLED from here on: the food is being made, so the cost is already sunk.
            PREPARING, Set.of(READY),
            READY, Set.of(COLLECTED),
            COLLECTED, Set.of(),
            CANCELLED, Set.of());

    public boolean canTransitionTo(OrderStatus next) {
        return ALLOWED.get(this).contains(next);
    }

    /** No move out of here exists, so an order in this state can never change again (I9). */
    public boolean isTerminal() {
        return ALLOWED.get(this).isEmpty();
    }
}
