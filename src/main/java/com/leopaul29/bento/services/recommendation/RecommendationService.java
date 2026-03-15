package com.leopaul29.bento.services.recommendation;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.User;

import java.util.List;

import java.util.function.Supplier;

public interface RecommendationService {
    List<Bento> recommend(
            String strategyName,
            User user,
            Supplier<List<Bento>> bentosSupplier
    );
}
