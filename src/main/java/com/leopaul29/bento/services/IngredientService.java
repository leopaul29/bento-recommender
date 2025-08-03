package com.leopaul29.bento.services;

import com.leopaul29.bento.dtos.IngredientDto;
import com.leopaul29.bento.entities.Ingredient;

import java.util.List;
import java.util.Set;

public interface IngredientService {
    Set<Ingredient> saveAndGetIngredientSet(List<IngredientDto> ingredientDtoList);
}
