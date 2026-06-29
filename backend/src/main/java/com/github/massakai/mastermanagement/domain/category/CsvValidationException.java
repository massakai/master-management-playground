package com.github.massakai.mastermanagement.domain.category;

import java.util.List;

public class CsvValidationException extends RuntimeException {

    private final List<CsvValidationError> errors;

    public CsvValidationException(String message, List<CsvValidationError> errors) {
        super(message);
        this.errors = List.copyOf(errors);
    }

    public List<CsvValidationError> errors() {
        return errors;
    }
}
