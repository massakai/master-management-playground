package com.github.massakai.mastermanagement.presentation.category;

import com.github.massakai.mastermanagement.application.category.CategoryDto;

public record CategoryResponse(
        String categoryCode,
        String categoryName,
        int displayOrder,
        boolean isActive,
        String description
) {

    public static CategoryResponse from(CategoryDto dto) {
        return new CategoryResponse(
                dto.categoryCode(),
                dto.categoryName(),
                dto.displayOrder(),
                dto.isActive(),
                dto.description()
        );
    }
}
