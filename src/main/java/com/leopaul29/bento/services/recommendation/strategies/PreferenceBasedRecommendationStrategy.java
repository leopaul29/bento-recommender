package com.leopaul29.bento.services.recommendation.strategies;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.services.recommendation.RecommendationContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PreferenceBasedRecommendationStrategy implements RecommendationStrategy {

    @Override
    public List<Bento> recommend(User user, List<Bento> allBentos, RecommendationContext context) {
        return allBentos.stream()
                .filter(bento ->
                        bento.getTags().containsAll(user.getLikedTags()) &&
                                Collections.disjoint(
                                        bento.getIngredients(),
                                        user.getDislikedIngredients()
                                )
                )
                .toList();
    }
}
