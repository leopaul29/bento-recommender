package com.leopaul29.bento.services.recommendation.strategies;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.services.recommendation.RecommendationContext;

import java.util.List;

public interface RecommendationStrategy {
    List<Bento> recommend(User user,
                          List<Bento> allBentos,
                          RecommendationContext context);
}
