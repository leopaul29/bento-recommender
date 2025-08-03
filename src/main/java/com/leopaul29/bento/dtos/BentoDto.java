package com.leopaul29.bento.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BentoDto {
    private Long id;
    @NotBlank
    private String name;
    private String description;
    private int calorie;
    @NotEmpty
    private List<IngredientDto> ingredients;
    @NotEmpty
    private List<TagDto> tags;
}
