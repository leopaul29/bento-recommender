package com.leopaul29.bento.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class UserPreferenceDto {
    private List<IngredientDto> dislikedIngredients;
    private List<TagDto> likedTags;
}
