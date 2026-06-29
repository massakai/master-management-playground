package com.github.massakai.mastermanagement.presentation.category;

public record CategoryCsvUploadResponse(
        boolean success,
        String message,
        int updatedCount
) {
}
