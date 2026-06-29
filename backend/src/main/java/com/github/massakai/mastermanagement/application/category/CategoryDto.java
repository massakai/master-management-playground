package com.github.massakai.mastermanagement.application.category;

import com.github.massakai.mastermanagement.domain.category.ProductCategory;

public record CategoryDto(
        String categoryCode,
        String categoryName,
        int displayOrder,
        boolean isActive,
        String description
) {

    public static CategoryDto from(ProductCategory category) {
        return new CategoryDto(
                category.categoryCode().value(),
                category.categoryName().value(),
                category.displayOrder().value(),
                category.activeFlag().value(),
                category.description().value()
        );
    }
}
