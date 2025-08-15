package com.leopaul29.bento.repositories;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.entities.Ingredient;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class BentoSpecification {

    private BentoSpecification() {}

    public static Specification<Bento> hasIngredients(List<Long> ingredientIds) {
        return (root, query, criteriaBuilder) -> {
            Join<Bento, Ingredient> ingredientJoin = root.join("ingredients");
            return ingredientJoin.get("id").in(ingredientIds);
        };
    }
}
