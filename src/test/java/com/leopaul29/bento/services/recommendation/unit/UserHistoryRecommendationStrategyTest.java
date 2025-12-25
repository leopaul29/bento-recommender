package com.leopaul29.bento.services.recommendation.unit;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.Tag;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.services.recommendation.RecommendationContext;
import com.leopaul29.bento.services.recommendation.strategies.UserHistoryRecommendationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
class UserHistoryRecommendationStrategyTest {

    private UserHistoryRecommendationStrategy strategy;

    @BeforeEach
    void setup() {
        strategy = new UserHistoryRecommendationStrategy();
    }

    @Test
    void shouldRankBentoWithMoreMatchingTagsHigher() {
        Tag spicy = Tag.builder().name("spicy").build();
        Tag vegan = Tag.builder().name("vegan").build();

        Bento spicyVegan = Bento.builder()
                .id(1L) // ✅ IMPORTANT
                .name("Spicy Vegan Bento")
                .tags(Set.of(spicy, vegan))
                .build();

        Bento plain = Bento.builder()
                .id(2L) // ✅ IMPORTANT
                .name("Plain Bento")
                .tags(Set.of())
                .build();

        User user = User.builder()
                .likedTags(Set.of(spicy, vegan))
                .build();

        List<Bento> allBentos = List.of(plain, spicyVegan);

        RecommendationContext context = RecommendationContext.builder()
                .orderCountByBento(Map.of(1L, 3L)) // user ordered spicyVegan 3 times
                .build();

        List<Bento> result = strategy.recommend(user, allBentos, context);

        assertEquals("Spicy Vegan Bento", result.get(0).getName());
    }
}
