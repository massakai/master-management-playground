package com.github.massakai.mastermanagement.application.category;

import com.github.massakai.mastermanagement.domain.category.CsvValidationError;
import com.github.massakai.mastermanagement.domain.category.CsvValidationException;
import java.util.List;

public class FileFormatException extends CsvValidationException {

    public FileFormatException(String message, CsvValidationError error) {
        super(message, List.of(error));
    }
}
