package com.github.massakai.mastermanagement.infrastructure.persistence.category;

import java.time.LocalDateTime;

public record ProductCategoryRecord(
        String categoryCode,
        String categoryName,
        Integer displayOrder,
        Boolean isActive,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
