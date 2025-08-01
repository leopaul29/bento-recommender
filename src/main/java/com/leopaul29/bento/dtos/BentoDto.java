package com.leopaul29.bento.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class BentoDto {
    private String name;
    private String description;
    private int calorie;
    private Set<IngredientDto> ingredients;
    private Set<TagDto> tags;
}
