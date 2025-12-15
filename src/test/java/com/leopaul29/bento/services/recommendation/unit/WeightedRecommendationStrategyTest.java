package com.leopaul29.bento.services.recommendation.unit;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.Tag;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.services.recommendation.WeightedRecommendationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeightedRecommendationStrategyTest {

    private WeightedRecommendationStrategy strategy;

    @BeforeEach
    void setup() {
        strategy = new WeightedRecommendationStrategy();
    }

    @Test
    void shouldRankBentoWithMoreMatchingTagsHigher() {
        Tag spicy = Tag.builder().name("spicy").build();
        Tag vegan = Tag.builder().name("vegan").build();

        Bento highScore = Bento.builder().name("Spicy Vegan Bento").tags(Set.of(spicy, vegan)).build();

        Bento lowScore = Bento.builder().name("Vegan Bento").tags(Set.of(vegan)).build();

        User user = User.builder().likedTags(Set.of(spicy, vegan)).dislikedIngredients(Set.of()).build();

        List<Bento> result = strategy.recommend(
                user,
                List.of(lowScore, highScore)
        );

        assertEquals("Spicy Vegan Bento", result.get(0).getName());
    }
}
