package com.github.massakai.mastermanagement.application.category;

import com.github.massakai.mastermanagement.domain.category.ProductCategoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListCategoriesUseCase {

    private final ProductCategoryRepository repository;

    public ListCategoriesUseCase(ProductCategoryRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> execute() {
        return repository.findAllOrdered().stream()
                .map(CategoryDto::from)
                .toList();
    }
}
