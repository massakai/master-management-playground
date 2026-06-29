package com.github.massakai.mastermanagement.application.category;

import com.github.massakai.mastermanagement.domain.category.CategoryCsvValidationResult;
import com.github.massakai.mastermanagement.domain.category.CategoryCsvValidationService;
import com.github.massakai.mastermanagement.domain.category.CsvValidationError;
import com.github.massakai.mastermanagement.domain.category.CsvValidationException;
import com.github.massakai.mastermanagement.domain.category.ProductCategoryRepository;
import com.github.massakai.mastermanagement.domain.category.RawCategoryCsvRow;
import com.github.massakai.mastermanagement.infrastructure.csv.category.CsvProductCategoryReader;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadCategoriesCsvUseCase {

    private static final Logger log = LoggerFactory.getLogger(UploadCategoriesCsvUseCase.class);

    private final CsvProductCategoryReader reader;
    private final CategoryCsvValidationService validationService;
    private final ProductCategoryRepository repository;

    public UploadCategoriesCsvUseCase(
            CsvProductCategoryReader reader,
            CategoryCsvValidationService validationService,
            ProductCategoryRepository repository
    ) {
        this.reader = reader;
        this.validationService = validationService;
        this.repository = repository;
    }

    @Transactional
    public UploadCategoriesCsvResult execute(UploadCategoriesCsvCommand command) {
        MultipartFile file = command.file();
        if (file == null || file.isEmpty()) {
            throw new FileFormatException(
                    "CSV ファイルを選択してください。",
                    new CsvValidationError(null, "file", "FILE_REQUIRED", "CSV ファイルを選択してください。")
            );
        }

        log.info("Category CSV upload started. originalFilename={}", file.getOriginalFilename());

        List<RawCategoryCsvRow> rows = readRows(file);
        CategoryCsvValidationResult validationResult = validationService.validate(rows);
        if (validationResult.hasErrors()) {
            log.info("Category CSV upload validation failed. errorCount={}", validationResult.errors().size());
            throw new CsvValidationException("CSV にエラーがあります。", validationResult.errors());
        }

        repository.replaceAll(validationResult.categories());
        log.info("Category CSV upload completed. updatedCount={}", validationResult.categories().size());
        return new UploadCategoriesCsvResult(validationResult.categories().size(), "商品カテゴリを更新しました。");
    }

    private List<RawCategoryCsvRow> readRows(MultipartFile file) {
        try {
            return reader.read(file.getInputStream());
        } catch (IOException e) {
            throw new FileFormatException(
                    "CSV を読み込めませんでした。",
                    new CsvValidationError(null, "file", "INVALID_ENCODING", "UTF-8 の CSV ファイルを指定してください。")
            );
        }
    }
}
