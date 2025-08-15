package com.leopaul29.bento.dtos;

import lombok.Data;

import java.util.List;

@Data
public class BentoFilterDto {
    private List<Long> ingredientIds;
    private List<Long> tagIds;
    private List<Long> excludeIngredientIds;
    private List<Long> excludeTagIds;
}
