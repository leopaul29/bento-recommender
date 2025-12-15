package com.leopaul29.bento.services.recommendation;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.Ingredient;
import com.leopaul29.bento.entities.Tag;
import com.leopaul29.bento.entities.User;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class WeightedRecommendationStrategy implements RecommendationStrategy {

    @Override
    public List<Bento> recommend(User user, List<Bento> allBentos) {
        return allBentos.stream()
                .map(bento -> new ScoredBento(bento, score(bento, user)))
                .filter(scored -> scored.score() > 0)
                .sorted(Comparator.comparingInt(ScoredBento::score).reversed())
                .map(ScoredBento::bento)
                .toList();
    }

    private int score(Bento bento, User user) {
        int score = 0;

        for (Tag tag : user.getLikedTags()) {
            if (bento.getTags().contains(tag)) {
                score += 10;
            }
        }

        for (Ingredient disliked : user.getDislikedIngredients()) {
            if (bento.getIngredients().contains(disliked)) {
                score -= 50;
            }
        }

        return score;
    }

    private record ScoredBento(Bento bento, int score) {}
}
