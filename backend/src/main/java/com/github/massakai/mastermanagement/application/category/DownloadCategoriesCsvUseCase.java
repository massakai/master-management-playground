package com.github.massakai.mastermanagement.application.category;

import com.github.massakai.mastermanagement.domain.category.ProductCategoryRepository;
import com.github.massakai.mastermanagement.infrastructure.csv.category.CsvProductCategoryWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DownloadCategoriesCsvUseCase {

    private final ProductCategoryRepository repository;
    private final CsvProductCategoryWriter writer;
    private final String filename;

    public DownloadCategoriesCsvUseCase(
            ProductCategoryRepository repository,
            CsvProductCategoryWriter writer,
            @Value("${app.csv.filename:product-categories.csv}") String filename
    ) {
        this.repository = repository;
        this.writer = writer;
        this.filename = filename;
    }

    @Transactional(readOnly = true)
    public CategoryCsvFile execute() {
        byte[] content = writer.write(repository.findAllOrdered());
        return new CategoryCsvFile(filename, "text/csv; charset=UTF-8", content);
    }
}
