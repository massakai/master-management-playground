package com.github.massakai.mastermanagement.presentation.category;

import com.github.massakai.mastermanagement.domain.category.CsvValidationError;

public record CsvValidationErrorResponse(
        Integer rowNumber,
        String field,
        String code,
        String message
) {

    public static CsvValidationErrorResponse from(CsvValidationError error) {
        return new CsvValidationErrorResponse(
                error.rowNumber(),
                error.field(),
                error.code(),
                error.message()
        );
    }
}
