package com.leopaul29.bento.mappers;

import com.leopaul29.bento.dtos.IngredientDto;
import com.leopaul29.bento.entities.Ingredient;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IngredientMapper {
    IngredientDto toDto(Ingredient ingredient);
    Ingredient toEntity(IngredientDto IngredientDto);
    List<IngredientDto> toDtoList(List<Ingredient> ingredientList);
}