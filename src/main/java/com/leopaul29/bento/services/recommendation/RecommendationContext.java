package com.leopaul29.bento.services.recommendation;

import lombok.Builder;

import java.util.Date;
import java.util.Map;

@Builder
public class RecommendationContext {

    private final Map<Long, Long> orderCountByBento;
    private final Map<Long, Date> lastOrderByBento;

    public RecommendationContext(
            Map<Long, Long> orderCountByBento,
            Map<Long, Date> lastOrderByBento
    ) {
        this.orderCountByBento = orderCountByBento;
        this.lastOrderByBento = lastOrderByBento;
    }

    public Map<Long, Long> getOrderCountByBento() {
        return orderCountByBento;
    }

    public Map<Long, Date> getLastOrderByBento() {
        return lastOrderByBento;
    }
}

