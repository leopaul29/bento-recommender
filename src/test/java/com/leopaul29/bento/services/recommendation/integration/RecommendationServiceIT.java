package com.leopaul29.bento.services.recommendation.integration;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.Tag;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.repositories.BentoRepository;
import com.leopaul29.bento.repositories.TagRepository;
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

    @Autowired
    TagRepository tagRepository;

    @Test
    void shouldReturnRecommendationsUsingDefaultStrategy() {
        // DataInitializer is an ungated @Component, so every test context is already seeded
        // with a "vegan" tag. Own tag name rather than collide with it.
        Tag vegan = tagRepository.save(Tag.builder().name("it-vegan").build());
        User user = userRepository.save(
                User.builder()
                        .username("reco-it-user")
                        .password("irrelevant")
                        .likedTags(Set.of(vegan))
                        .dislikedIngredients(Set.of())
                        .build()
        );

        bentoRepository.save(
                Bento.builder()
                        .name("Vegan bento")
                        .tags(Set.of(vegan))
                        .ingredients(Set.of())
                        .build()
        );

        List<Bento> result = recommendationService
                .recommend(null, user, bentoRepository.findAll());

        assertFalse(result.isEmpty());
    }
}
