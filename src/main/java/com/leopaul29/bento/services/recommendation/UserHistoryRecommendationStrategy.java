package com.leopaul29.bento.services.recommendation;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.ordering.infrastructure.persistence.OrderHistoryReadModel;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Ranks bentos by how many the user has actually ordered.
 *
 * <p>This class returned {@code List.of()} from 2025-12 until Phase 2, and was not even a
 * {@code @Component}, so asking for it silently fell back to the preference-based strategy. It
 * could not be implemented before now for a reason that was never written down: there was no order
 * history in the system to read. The ordering context supplies one.
 *
 * <p>It reads a read model, not the {@code Order} aggregate — ranking a menu is a query, and the
 * aggregate exists to enforce rules on writes.
 */
@Component
public class UserHistoryRecommendationStrategy implements RecommendationStrategy {

    private final OrderHistoryReadModel history;

    public UserHistoryRecommendationStrategy(OrderHistoryReadModel history) {
        this.history = history;
    }

    @Override
    public List<Bento> recommend(User user, List<Bento> allBentos) {
        Map<Long, Long> ordered = history.orderedQuantitiesFor(user.getId());

        // Never ordered anything: this strategy has nothing to say, and saying it with an
        // arbitrary order would be worse than admitting it.
        if (ordered.isEmpty()) {
            return List.of();
        }

        return allBentos.stream()
                .filter(bento -> ordered.containsKey(bento.getId()))
                .sorted(Comparator
                        .comparingLong((Bento bento) -> ordered.get(bento.getId()))
                        .reversed()
                        .thenComparing(Bento::getId))
                .toList();
    }
}
