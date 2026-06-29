package com.github.massakai.mastermanagement.presentation.category;

import java.util.List;

public record CategoryCsvUploadErrorResponse(
        boolean success,
        String message,
        int errorCount,
        List<CsvValidationErrorResponse> errors
) {
}
