package com.leopaul29.bento.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class BentoDto {
    private Long id;
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private int calorie;
    @NotEmpty
    private Set<IngredientDto> ingredients;
    @NotEmpty
    private Set<TagDto> tags;
}
