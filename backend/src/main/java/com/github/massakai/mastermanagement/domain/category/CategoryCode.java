package com.github.massakai.mastermanagement.domain.category;

import java.util.regex.Pattern;

public record CategoryCode(String value) {

    private static final Pattern ALLOWED = Pattern.compile("^[A-Za-z0-9_-]+$");

    public CategoryCode {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException("category_code", "REQUIRED", "入力してください。");
        }
        if (value.length() > 50 || !ALLOWED.matcher(value).matches()) {
            throw new DomainValidationException("category_code", "INVALID_FORMAT", "半角英数字、_、- で入力してください。");
        }
    }
}
