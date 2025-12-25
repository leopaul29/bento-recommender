package com.leopaul29.bento.services.recommendation;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.repositories.UserBentoHistoryRepository;
import com.leopaul29.bento.services.recommendation.strategies.RecommendationStrategy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
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

    public List<Bento> recommend(
            String strategyName,
            User user,
            List<Bento> bentos) {
        RecommendationStrategy strategy =
                strategies.getOrDefault(
                        strategyName,
                        strategies.get("PreferenceBasedRecommendationStrategy")
                );

        Map<Long, Long> orderCount = historyRepo.countByBentoIdForUser(user.getId());
        Map<Long, Date> lastOrder = historyRepo.lastOrderedByBento(user.getId());

        RecommendationContext context = RecommendationContext.builder()
                .orderCountByBento(orderCount)
                .lastOrderByBento(lastOrder)
                .build();

        return strategy.recommend(user, bentos, context);
    }
}
