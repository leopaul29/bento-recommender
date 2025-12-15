package com.leopaul29.bento.services.recommendation;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.User;

import java.util.List;

public class UserHistoryRecommendationStrategy implements RecommendationStrategy {
    @Override
    public List<Bento> recommend(User user, List<Bento> allBentos) {
        return List.of();
    }
}
