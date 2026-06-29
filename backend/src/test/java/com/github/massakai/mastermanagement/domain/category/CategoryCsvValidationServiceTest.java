package com.github.massakai.mastermanagement.domain.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class CategoryCsvValidationServiceTest {

    private final CategoryCsvValidationService service = new CategoryCsvValidationService();

    @Test
    void validatesRowsAndBuildsCategories() {
        CategoryCsvValidationResult result = service.validate(List.of(
                new RawCategoryCsvRow(2, "FOOD", "食品", "10", "true", "食品カテゴリ")
        ));

        assertThat(result.errors()).isEmpty();
        assertThat(result.categories()).hasSize(1);
        ProductCategory category = result.categories().get(0);
        assertThat(category.categoryCode().value()).isEqualTo("FOOD");
        assertThat(category.activeFlag().value()).isTrue();
    }

    @Test
    void detectsDuplicateCodeAndInvalidValues() {
        CategoryCsvValidationResult result = service.validate(List.of(
                new RawCategoryCsvRow(2, "FOOD", "", "abc", "1", null),
                new RawCategoryCsvRow(3, "FOOD", "食品", "20", "true", null)
        ));

        assertThat(result.categories()).isEmpty();
        assertThat(result.errors())
                .extracting(CsvValidationError::code)
                .contains(
                        "DUPLICATE_CATEGORY_CODE",
                        "REQUIRED",
                        "INVALID_INTEGER",
                        "INVALID_BOOLEAN"
                );
    }

    @Test
    void detectsEmptyData() {
        CategoryCsvValidationResult result = service.validate(List.of());

        assertThat(result.errors()).singleElement()
                .satisfies(error -> {
                    assertThat(error.rowNumber()).isNull();
                    assertThat(error.code()).isEqualTo("EMPTY_DATA");
                });
    }
}
