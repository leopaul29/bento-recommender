package com.leopaul29.bento.repositories;

import com.leopaul29.bento.entities.Ingredient;
import com.leopaul29.bento.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
}
