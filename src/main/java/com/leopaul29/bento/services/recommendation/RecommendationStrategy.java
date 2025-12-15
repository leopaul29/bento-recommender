package com.leopaul29.bento.services.recommendation;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.User;

import java.util.List;

public interface RecommendationStrategy {
    List<Bento> recommend(User user, List<Bento> allBentos);
}
