package com.github.massakai.mastermanagement.presentation.category;

import com.github.massakai.mastermanagement.domain.category.CsvValidationException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CsvValidationException.class)
    public ResponseEntity<CategoryCsvUploadErrorResponse> handleCsvValidation(CsvValidationException e) {
        List<CsvValidationErrorResponse> errors = e.errors().stream()
                .map(CsvValidationErrorResponse::from)
                .toList();
        return ResponseEntity.badRequest().body(new CategoryCsvUploadErrorResponse(
                false,
                e.getMessage(),
                errors.size(),
                errors
        ));
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MultipartException.class,
            MaxUploadSizeExceededException.class
    })
    public ResponseEntity<CategoryCsvUploadErrorResponse> handleFileRequestException(Exception e) {
        CsvValidationErrorResponse error = new CsvValidationErrorResponse(
                null,
                "file",
                "FILE_REQUIRED",
                "CSV ファイルを選択してください。"
        );
        return ResponseEntity.badRequest().body(new CategoryCsvUploadErrorResponse(
                false,
                error.message(),
                1,
                List.of(error)
        ));
    }

    @ExceptionHandler(UnexpectedApplicationException.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedApplication(UnexpectedApplicationException e) {
        log.error("Unexpected application error.", e);
        return internalServerError();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected system error.", e);
        return internalServerError();
    }

    private ResponseEntity<ErrorResponse> internalServerError() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "システムエラーが発生しました。"));
    }
}
