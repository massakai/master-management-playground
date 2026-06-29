package com.github.massakai.mastermanagement.infrastructure.csv.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.massakai.mastermanagement.application.category.FileFormatException;
import com.github.massakai.mastermanagement.domain.category.RawCategoryCsvRow;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class CsvProductCategoryReaderTest {

    private final CsvProductCategoryReader reader = new CsvProductCategoryReader();

    @Test
    void readsCsvRowsWithCsvLineNumber() throws Exception {
        String csv = """
                category_code,category_name,display_order,is_active,description
                FOOD,食品,10,true,食品カテゴリ
                """;

        List<RawCategoryCsvRow> rows = reader.read(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));

        assertThat(rows).containsExactly(new RawCategoryCsvRow(2, "FOOD", "食品", "10", "true", "食品カテゴリ"));
    }

    @Test
    void rejectsInvalidHeader() {
        String csv = """
                category_code,category_name
                FOOD,食品
                """;

        assertThatThrownBy(() -> reader.read(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8))))
                .isInstanceOf(FileFormatException.class);
    }
}
