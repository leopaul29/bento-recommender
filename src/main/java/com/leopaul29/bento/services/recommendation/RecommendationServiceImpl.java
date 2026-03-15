package com.leopaul29.bento.services.recommendation;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.repositories.UserBentoHistoryRepository;
import com.leopaul29.bento.services.recommendation.strategies.RecommendationStrategy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    private final Map<String, RecommendationStrategy> strategies;
    private final UserBentoHistoryRepository historyRepo;

    public RecommendationServiceImpl(List<RecommendationStrategy> strategyList, UserBentoHistoryRepository historyRepo) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        s -> s.getClass().getSimpleName(),
                        s -> s
                ));
        this.historyRepo = historyRepo;
    }

    @Override
    public List<Bento> recommend(
            String strategyName,
            User user,
            Supplier<List<Bento>> bentosSupplier) {
        RecommendationStrategy strategy =
                strategies.getOrDefault(
                        strategyName,
                        strategies.get("PreferenceBasedRecommendationStrategy")
                );

        Map<Long, Long> orderCount = Collections.emptyMap();
        Map<Long, Date> lastOrder = Collections.emptyMap();

        if (strategy.requiresHistory()) {
            orderCount = historyRepo.countByBentoIdForUser(user.getId());
            lastOrder = historyRepo.lastOrderedByBento(user.getId());
        }

        List<Bento> bentos = bentosSupplier.get();
        if (bentos == null || bentos.isEmpty()) {
            return Collections.emptyList();
        }

        RecommendationContext context = RecommendationContext.builder()
                .orderCountByBento(orderCount)
                .lastOrderByBento(lastOrder)
                .build();

        return strategy.recommend(user, bentos, context);
    }
}
