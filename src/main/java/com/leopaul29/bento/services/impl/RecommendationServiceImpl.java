package com.leopaul29.bento.services.impl;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.services.RecommendationService;
import com.leopaul29.bento.services.recommendation.RecommendationStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    private final Map<String, RecommendationStrategy> strategies;

    public RecommendationServiceImpl(List<RecommendationStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        s -> s.getClass().getSimpleName(),
                        s -> s
                ));
    }

    public List<Bento> recommend(
            String strategyName,
            User user,
            List<Bento> bentos
    ) {
        RecommendationStrategy strategy =
                strategies.getOrDefault(
                        strategyName,
                        strategies.get("PreferenceBasedRecommendationStrategy")
                );

        return strategy.recommend(user, bentos);
    }
}
