package com.github.massakai.mastermanagement.presentation.category;

import com.github.massakai.mastermanagement.application.category.CategoryCsvFile;
import com.github.massakai.mastermanagement.application.category.DownloadCategoriesCsvUseCase;
import com.github.massakai.mastermanagement.application.category.ListCategoriesUseCase;
import com.github.massakai.mastermanagement.application.category.UploadCategoriesCsvCommand;
import com.github.massakai.mastermanagement.application.category.UploadCategoriesCsvResult;
import com.github.massakai.mastermanagement.application.category.UploadCategoriesCsvUseCase;
import java.nio.charset.StandardCharsets;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final ListCategoriesUseCase listCategoriesUseCase;
    private final DownloadCategoriesCsvUseCase downloadCategoriesCsvUseCase;
    private final UploadCategoriesCsvUseCase uploadCategoriesCsvUseCase;

    public CategoryController(
            ListCategoriesUseCase listCategoriesUseCase,
            DownloadCategoriesCsvUseCase downloadCategoriesCsvUseCase,
            UploadCategoriesCsvUseCase uploadCategoriesCsvUseCase
    ) {
        this.listCategoriesUseCase = listCategoriesUseCase;
        this.downloadCategoriesCsvUseCase = downloadCategoriesCsvUseCase;
        this.uploadCategoriesCsvUseCase = uploadCategoriesCsvUseCase;
    }

    @GetMapping
    public CategoryListResponse listCategories() {
        return new CategoryListResponse(
                listCategoriesUseCase.execute().stream()
                        .map(CategoryResponse::from)
                        .toList()
        );
    }

    @GetMapping(value = "/csv", produces = "text/csv")
    public ResponseEntity<byte[]> downloadCategoriesCsv() {
        CategoryCsvFile file = downloadCategoriesCsvUseCase.execute();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(file.filename(), StandardCharsets.UTF_8)
                        .build()
                        .toString())
                .contentType(MediaType.parseMediaType(file.contentType()))
                .body(file.content());
    }

    @PostMapping(value = "/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CategoryCsvUploadResponse uploadCategoriesCsv(@RequestParam("file") MultipartFile file) {
        UploadCategoriesCsvResult result = uploadCategoriesCsvUseCase.execute(new UploadCategoriesCsvCommand(file));
        return new CategoryCsvUploadResponse(true, result.message(), result.updatedCount());
    }
}
