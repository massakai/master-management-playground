package com.github.massakai.mastermanagement.application.category;

public record CategoryCsvFile(
        String filename,
        String contentType,
        byte[] content
) {
}
