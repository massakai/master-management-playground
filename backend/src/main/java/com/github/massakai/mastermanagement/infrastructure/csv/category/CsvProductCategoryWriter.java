package com.github.massakai.mastermanagement.infrastructure.csv.category;

import com.github.massakai.mastermanagement.domain.category.ProductCategory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

@Component
public class CsvProductCategoryWriter {

    private static final String[] HEADERS = {
            "category_code",
            "category_name",
            "display_order",
            "is_active",
            "description"
    };

    public byte[] write(List<ProductCategory> categories) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try (OutputStreamWriter writer = new OutputStreamWriter(output, StandardCharsets.UTF_8);
                 CSVPrinter printer = CSVFormat.DEFAULT.builder()
                         .setHeader(HEADERS)
                         .setRecordSeparator('\n')
                         .build()
                         .print(writer)) {
                for (ProductCategory category : categories) {
                    printer.printRecord(
                            category.categoryCode().value(),
                            category.categoryName().value(),
                            category.displayOrder().value(),
                            Boolean.toString(category.activeFlag().value()),
                            category.description().value()
                    );
                }
            }
            return output.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("CSV の生成に失敗しました。", e);
        }
    }
}
