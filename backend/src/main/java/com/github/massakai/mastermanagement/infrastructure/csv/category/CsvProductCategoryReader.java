package com.github.massakai.mastermanagement.infrastructure.csv.category;

import com.github.massakai.mastermanagement.application.category.FileFormatException;
import com.github.massakai.mastermanagement.domain.category.CsvValidationError;
import com.github.massakai.mastermanagement.domain.category.RawCategoryCsvRow;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

@Component
public class CsvProductCategoryReader {

    private static final List<String> HEADERS = List.of(
            "category_code",
            "category_name",
            "display_order",
            "is_active",
            "description"
    );

    public List<RawCategoryCsvRow> read(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(
                inputStream,
                StandardCharsets.UTF_8.newDecoder()
                        .onMalformedInput(CodingErrorAction.REPORT)
                        .onUnmappableCharacter(CodingErrorAction.REPORT)
        );
        try (BufferedReader reader = new BufferedReader(inputStreamReader);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setAllowMissingColumnNames(false)
                     .setIgnoreEmptyLines(true)
                     .build()
                     .parse(reader)) {
            validateHeader(parser);

            List<RawCategoryCsvRow> rows = new ArrayList<>();
            for (CSVRecord record : parser) {
                if (record.size() != HEADERS.size()) {
                    throw invalidHeader();
                }
                rows.add(new RawCategoryCsvRow(
                        (int) record.getRecordNumber() + 1,
                        record.get("category_code"),
                        record.get("category_name"),
                        record.get("display_order"),
                        record.get("is_active"),
                        record.get("description")
                ));
            }
            return rows;
        } catch (IllegalArgumentException e) {
            throw invalidHeader();
        }
    }

    private void validateHeader(CSVParser parser) {
        List<String> actual = parser.getHeaderNames();
        if (!HEADERS.equals(actual)) {
            throw invalidHeader();
        }
    }

    private FileFormatException invalidHeader() {
        return new FileFormatException(
                "CSV のヘッダーが不正です。",
                new CsvValidationError(null, "header", "INVALID_HEADER", "ヘッダーは category_code,category_name,display_order,is_active,description の順にしてください。")
        );
    }
}
