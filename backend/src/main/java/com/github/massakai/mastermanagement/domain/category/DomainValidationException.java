package com.github.massakai.mastermanagement.domain.category;

public class DomainValidationException extends RuntimeException {

    private final String field;
    private final String code;

    public DomainValidationException(String field, String code, String message) {
        super(message);
        this.field = field;
        this.code = code;
    }

    public String field() {
        return field;
    }

    public String code() {
        return code;
    }
}
