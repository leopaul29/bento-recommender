package com.leopaul29.bento.dtos;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class BentoFilterDto {
    @Size(max = 10, message = "Maximum 10 ingredients allowed")
    private List<Long> ingredientIds;
    @Size(max = 5, message = "Maximum 5 tags allowed")
    private List<Long> tagIds;
    @Size(max = 10, message = "Maximum 10 ingredients allowed")
    private List<Long> excludeIngredientIds;
    @Size(max = 5, message = "Maximum 5 tags allowed")
    private List<Long> excludeTagIds;

    // Utility methods
    public boolean hasIngredientFilter() {
        return ingredientIds != null && !ingredientIds.isEmpty();
    }

    public boolean hasTagFilter() {
        return tagIds != null && !tagIds.isEmpty();
    }

    public boolean hasExcludeIngredientFilter() {
        return excludeIngredientIds != null && !excludeIngredientIds.isEmpty();
    }

    public boolean hasExcludeTagFilter() {
        return excludeTagIds != null && !excludeTagIds.isEmpty();
    }

    public boolean hasAnyFilter() {
        return hasIngredientFilter() || hasTagFilter() ||
                hasExcludeIngredientFilter() || hasExcludeTagFilter();
    }
}
