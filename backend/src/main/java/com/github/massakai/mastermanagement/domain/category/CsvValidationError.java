package com.github.massakai.mastermanagement.domain.category;

public record CsvValidationError(
        Integer rowNumber,
        String field,
        String code,
        String message
) {
}
