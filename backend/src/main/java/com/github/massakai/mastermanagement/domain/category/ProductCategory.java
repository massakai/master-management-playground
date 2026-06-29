package com.github.massakai.mastermanagement.domain.category;

public record ProductCategory(
        CategoryCode categoryCode,
        CategoryName categoryName,
        DisplayOrder displayOrder,
        ActiveFlag activeFlag,
        Description description
) {

    public static ProductCategory of(
            String categoryCode,
            String categoryName,
            int displayOrder,
            boolean isActive,
            String description
    ) {
        return new ProductCategory(
                new CategoryCode(categoryCode),
                new CategoryName(categoryName),
                new DisplayOrder(displayOrder),
                new ActiveFlag(isActive),
                new Description(description == null || description.isBlank() ? null : description)
        );
    }
}
