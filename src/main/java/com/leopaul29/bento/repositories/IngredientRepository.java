package com.leopaul29.bento.repositories;

import com.leopaul29.bento.entities.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findByNameIn(List<String> names);
}
