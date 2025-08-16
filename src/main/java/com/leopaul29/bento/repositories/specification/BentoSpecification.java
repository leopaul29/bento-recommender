package com.leopaul29.bento.repositories.specification;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.Ingredient;
import com.leopaul29.bento.entities.Tag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class BentoSpecification {

    private BentoSpecification() {}
// test: https://www.baeldung.com/rest-api-search-language-spring-data-specifications
    public static Specification<Bento> hasIngredients(List<Long> ingredientIds) {
        return (root, query, criteriaBuilder) -> {
            Join<Bento, Ingredient> ingredientJoin = root.join("ingredients");
            return ingredientJoin.get("id").in(ingredientIds);
        };
    }

    public static Specification<Bento> hasTags(List<Long> tagIds) {
        return (root, query, criteriaBuilder) -> {
            Join<Bento, Tag> tagJoin = root.join("tags");
            return tagJoin.get("id").in(tagIds);
        };
    }

    public static Specification<Bento> excludeIngredients(List<Long> excludeIngredientIds) {
        return (root, query, criteriaBuilder) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Bento> subRoot = subquery.from(Bento.class);
            Join<Bento, Ingredient> subIngredientJoin = subRoot.join("ingredients");

            subquery.select(subRoot.get("id"))
                    .where(subIngredientJoin.get("id").in(excludeIngredientIds));

            return criteriaBuilder.not(root.get("id").in(subquery));
        };
    }

    public static Specification<Bento> excludeTags(List<Long> excludeTagIds) {
        return (root, query, criteriaBuilder) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Bento> subRoot = subquery.from(Bento.class);
            Join<Bento, Tag> subTagJoin = subRoot.join("tags");

            subquery.select(subRoot.get("id"))
                    .where(subTagJoin.get("id").in(excludeTagIds));

            return criteriaBuilder.not(root.get("id").in(subquery));
        };
    }
}
