package com.github.massakai.mastermanagement.domain.category;

import java.util.List;

public interface ProductCategoryRepository {

    List<ProductCategory> findAllOrdered();

    void replaceAll(List<ProductCategory> categories);
}
