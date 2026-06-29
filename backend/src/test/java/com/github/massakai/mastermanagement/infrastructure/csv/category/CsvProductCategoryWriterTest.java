package com.github.massakai.mastermanagement.infrastructure.csv.category;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.massakai.mastermanagement.domain.category.ProductCategory;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class CsvProductCategoryWriterTest {

    private final CsvProductCategoryWriter writer = new CsvProductCategoryWriter();

    @Test
    void writesCsvWithFixedHeaderOrder() {
        byte[] bytes = writer.write(List.of(
                ProductCategory.of("FOOD", "食品", 10, true, "食品カテゴリ")
        ));

        String csv = new String(bytes, StandardCharsets.UTF_8);
        assertThat(csv).startsWith("category_code,category_name,display_order,is_active,description\n");
        assertThat(csv).contains("FOOD,食品,10,true,食品カテゴリ\n");
    }
}
