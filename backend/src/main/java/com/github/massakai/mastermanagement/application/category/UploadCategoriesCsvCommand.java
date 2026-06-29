package com.github.massakai.mastermanagement.application.category;

import org.springframework.web.multipart.MultipartFile;

public record UploadCategoriesCsvCommand(MultipartFile file) {
}
