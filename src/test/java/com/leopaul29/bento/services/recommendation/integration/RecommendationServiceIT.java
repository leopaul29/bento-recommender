package com.leopaul29.bento.services.recommendation.integration;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.Tag;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.repositories.BentoRepository;
import com.leopaul29.bento.repositories.UserRepository;
import com.leopaul29.bento.services.RecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("test")
class RecommendationServiceIT {

    @Autowired
    RecommendationService recommendationService;

    @Autowired
    BentoRepository bentoRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void shouldReturnRecommendationsUsingDefaultStrategy() {
        Tag vegan = Tag.of("vegan");
        User user = userRepository.save(
                User.builder().likedTags(Set.of(vegan)).build()
        );

        bentoRepository.save(
                Bento.builder().tags(Set.of(vegan)).build()
        );

        List<Bento> result = recommendationService
                .recommend(null, user, bentoRepository.findAll());

        assertFalse(result.isEmpty());
    }
}
