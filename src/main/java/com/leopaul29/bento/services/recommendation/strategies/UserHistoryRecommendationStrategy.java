package com.leopaul29.bento.services.recommendation.strategies;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.services.recommendation.RecommendationContext;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class UserHistoryRecommendationStrategy implements RecommendationStrategy {

    private Map<Long, Long> orderCount = Map.of();
    private Map<Long, Date> lastOrder = Map.of();

    public void init(
            Map<Long, Long> orderCount,
            Map<Long, Date> lastOrder
    ) {
        this.orderCount = orderCount;
        this.lastOrder = lastOrder;
    }

    @Override
    public List<Bento> recommend(User user, List<Bento> bentos, RecommendationContext context) {
        return bentos.stream()
                .sorted(Comparator
                        .comparingLong((Bento b) -> orderCount.getOrDefault(b.getId(), 0L))
                        .reversed()
                        .thenComparing(
                                b -> lastOrder.getOrDefault(b.getId(), new Date(0)),
                                Comparator.reverseOrder()
                        )
                )
                .toList();
    }
}

