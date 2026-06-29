package com.github.massakai.mastermanagement.domain.category;

public record CategoryName(String value) {

    public CategoryName {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException("category_name", "REQUIRED", "入力してください。");
        }
        if (value.length() > 100) {
            throw new DomainValidationException("category_name", "INVALID_FORMAT", "100 文字以内で入力してください。");
        }
    }
}
