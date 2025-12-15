package com.leopaul29.bento.services.recommendation.unit;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.Ingredient;
import com.leopaul29.bento.entities.Tag;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.services.recommendation.PreferenceBasedRecommendationStrategy;
import com.leopaul29.bento.services.recommendation.RecommendationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PreferenceBasedRecommendationStrategyTest {

    private RecommendationStrategy strategy;

    @BeforeEach
    void setup() {
        strategy = new PreferenceBasedRecommendationStrategy();
    }

    @Test
    void shouldRecommendBentoWhenUserLikesTagAndNoDislikedIngredient() {
        // given
        Tag vegan = Tag.of("vegan");
        Ingredient rice = Ingredient.of("rice");
        Ingredient beef = Ingredient.of("beef");

        Bento bento = Bento.builder().name("Vegan Bento").tags(Set.of(vegan)).ingredients(Set.of(rice)).build();

        User user = User.builder().likedTags(Set.of(vegan)).dislikedIngredients(Set.of(beef)).build();

        // when
        List<Bento> result = strategy.recommend(user, List.of(bento));

        // then
        assertEquals(1, result.size());
        assertEquals("Vegan Bento", result.get(0).getName());
    }

    @Test
    void shouldExcludeBentoContainingDislikedIngredient() {
        Ingredient beef = Ingredient.of("beef");

        Bento bento = Bento.builder().name("Beef Bento").tags(Set.of()).ingredients(Set.of(beef)).build();

        User user = User.builder().likedTags(Set.of()).dislikedIngredients(Set.of(beef)).build();

        List<Bento> result = strategy.recommend(user, List.of(bento));

        assertTrue(result.isEmpty());
    }
}
