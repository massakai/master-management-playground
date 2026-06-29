package com.github.massakai.mastermanagement.domain.category;

public record RawCategoryCsvRow(
        int rowNumber,
        String categoryCode,
        String categoryName,
        String displayOrder,
        String isActive,
        String description
) {
}
