package com.github.massakai.mastermanagement.domain.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CategoryCsvValidationService {

    public CategoryCsvValidationResult validate(List<RawCategoryCsvRow> rows) {
        List<CsvValidationError> errors = new ArrayList<>();
        List<ProductCategory> categories = new ArrayList<>();

        if (rows.isEmpty()) {
            errors.add(new CsvValidationError(null, "file", "EMPTY_DATA", "データ行を 1 件以上入力してください。"));
            return new CategoryCsvValidationResult(List.of(), errors);
        }

        Map<String, List<Integer>> codeRows = new HashMap<>();
        for (RawCategoryCsvRow row : rows) {
            if (hasText(row.categoryCode())) {
                codeRows.computeIfAbsent(row.categoryCode(), ignored -> new ArrayList<>()).add(row.rowNumber());
            }
        }

        for (RawCategoryCsvRow row : rows) {
            boolean rowHasError = false;
            if (!hasText(row.categoryCode())) {
                errors.add(new CsvValidationError(row.rowNumber(), "category_code", "REQUIRED", "入力してください。"));
                rowHasError = true;
            } else if (codeRows.getOrDefault(row.categoryCode(), List.of()).size() > 1) {
                errors.add(new CsvValidationError(row.rowNumber(), "category_code", "DUPLICATE_CATEGORY_CODE", "カテゴリコードが重複しています。"));
                rowHasError = true;
            }

            if (!hasText(row.categoryName())) {
                errors.add(new CsvValidationError(row.rowNumber(), "category_name", "REQUIRED", "入力してください。"));
                rowHasError = true;
            }

            Integer displayOrder = parseDisplayOrder(row, errors);
            if (displayOrder == null) {
                rowHasError = true;
            }

            Boolean active = parseActive(row, errors);
            if (active == null) {
                rowHasError = true;
            }

            if (!rowHasError) {
                try {
                    categories.add(ProductCategory.of(
                            row.categoryCode(),
                            row.categoryName(),
                            displayOrder,
                            active,
                            row.description()
                    ));
                } catch (DomainValidationException e) {
                    errors.add(new CsvValidationError(row.rowNumber(), e.field(), e.code(), e.getMessage()));
                }
            }
        }

        return new CategoryCsvValidationResult(categories, errors);
    }

    private Integer parseDisplayOrder(RawCategoryCsvRow row, List<CsvValidationError> errors) {
        if (!hasText(row.displayOrder())) {
            errors.add(new CsvValidationError(row.rowNumber(), "display_order", "REQUIRED", "入力してください。"));
            return null;
        }
        try {
            return Integer.valueOf(row.displayOrder());
        } catch (NumberFormatException e) {
            errors.add(new CsvValidationError(row.rowNumber(), "display_order", "INVALID_INTEGER", "整数で入力してください。"));
            return null;
        }
    }

    private Boolean parseActive(RawCategoryCsvRow row, List<CsvValidationError> errors) {
        if (!hasText(row.isActive())) {
            errors.add(new CsvValidationError(row.rowNumber(), "is_active", "REQUIRED", "入力してください。"));
            return null;
        }
        if ("true".equals(row.isActive())) {
            return true;
        }
        if ("false".equals(row.isActive())) {
            return false;
        }
        errors.add(new CsvValidationError(row.rowNumber(), "is_active", "INVALID_BOOLEAN", "true または false を入力してください。"));
        return null;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
