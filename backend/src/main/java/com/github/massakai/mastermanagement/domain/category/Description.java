package com.github.massakai.mastermanagement.domain.category;

public record Description(String value) {

    public Description {
        if (value != null && value.length() > 255) {
            throw new DomainValidationException("description", "INVALID_FORMAT", "255 文字以内で入力してください。");
        }
    }
}
