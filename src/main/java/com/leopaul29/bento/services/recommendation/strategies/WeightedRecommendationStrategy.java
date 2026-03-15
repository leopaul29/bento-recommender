package com.leopaul29.bento.services.recommendation.strategies;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.Ingredient;
import com.leopaul29.bento.entities.Tag;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.services.recommendation.RecommendationContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Component
public class WeightedRecommendationStrategy implements RecommendationStrategy {

    @Override
    public List<Bento> recommend(User user, List<Bento> allBentos, RecommendationContext context) {
        if (allBentos.isEmpty()) {
            return Collections.emptyList();
        }

        var likedTags = user.getLikedTags();
        var dislikedIngredients = user.getDislikedIngredients();

        return allBentos.stream()
                .map(bento -> new ScoredBento(bento, score(bento, likedTags, dislikedIngredients)))
                .filter(scored -> scored.score() > 0)
                .sorted(Comparator.comparingInt(ScoredBento::score).reversed())
                .map(ScoredBento::bento)
                .toList();
    }

    private int score(Bento bento, Set<Tag> likedTags, Set<Ingredient> dislikedIngredients) {
        int score = 0;

        Set<Tag> bentoTags = bento.getTags();
        for (Tag tag : likedTags) {
            if (bentoTags.contains(tag)) {
                score += 10;
            }
        }

        Set<Ingredient> bentoIngredients = bento.getIngredients();
        for (Ingredient disliked : dislikedIngredients) {
            if (bentoIngredients.contains(disliked)) {
                score -= 50;
            }
        }

        return score;
    }

    private record ScoredBento(Bento bento, int score) {}
}
