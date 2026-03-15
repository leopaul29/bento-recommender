package com.leopaul29.bento.services.recommendation.strategies;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.services.recommendation.RecommendationContext;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Component
public class UserHistoryRecommendationStrategy implements RecommendationStrategy {

    private static final Date EPOCH = new Date(0);

    @Override
    public boolean requiresHistory() {
        return true;
    }

    @Override
    public List<Bento> recommend(User user, List<Bento> bentos, RecommendationContext context) {
        return bentos.stream()
                .sorted(Comparator
                        .comparingLong((Bento b) -> context.getOrderCountByBento().getOrDefault(b.getId(), 0L))
                        .reversed()
                        .thenComparing(
                                b -> context.getLastOrderByBento().getOrDefault(b.getId(), EPOCH),
                                Comparator.reverseOrder()
                        )
                )
                .toList();
    }
}

