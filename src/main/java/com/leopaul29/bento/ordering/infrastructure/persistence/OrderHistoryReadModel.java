package com.leopaul29.bento.ordering.infrastructure.persistence;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * How many of each bento a customer has actually ordered.
 *
 * <p>A read model, deliberately not a repository: it returns counts keyed by bento id, never an
 * {@code Order}. The recommender has no business loading an aggregate to rank a menu, and an
 * aggregate loaded for reading is an aggregate someone will eventually mutate by accident.
 *
 * <p>It counts only orders the shop accepted or beyond. A placed-then-cancelled order is not
 * evidence that someone likes a bento.
 */
@Component
public class OrderHistoryReadModel {

    private final OrderJpaRepository orders;

    public OrderHistoryReadModel(OrderJpaRepository orders) {
        this.orders = orders;
    }

    /** Bento id to quantity ordered, most ordered first. */
    public Map<Long, Long> orderedQuantitiesFor(long customerId) {
        Map<Long, Long> counts = new LinkedHashMap<>();
        for (OrderJpaRepository.BentoOrderCount row : orders.countOrderedBentosFor(customerId)) {
            counts.put(row.getBentoId(), row.getTotal());
        }
        return counts;
    }
}
