package com.github.massakai.mastermanagement.domain.category;

import java.util.List;

public record CategoryCsvValidationResult(
        List<ProductCategory> categories,
        List<CsvValidationError> errors
) {

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
