package com.github.massakai.mastermanagement.infrastructure.persistence.category;

import com.github.massakai.mastermanagement.domain.category.ProductCategory;
import com.github.massakai.mastermanagement.domain.category.ProductCategoryRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisProductCategoryRepository implements ProductCategoryRepository {

    private final ProductCategoryMapper mapper;
    private final Clock clock;

    public MyBatisProductCategoryRepository(ProductCategoryMapper mapper, Clock clock) {
        this.mapper = mapper;
        this.clock = clock;
    }

    @Override
    public List<ProductCategory> findAllOrdered() {
        return mapper.selectAllOrdered().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void replaceAll(List<ProductCategory> categories) {
        LocalDateTime now = LocalDateTime.now(clock);
        mapper.deleteAll();
        for (ProductCategory category : categories) {
            mapper.insert(toRecord(category, now));
        }
    }

    private ProductCategory toDomain(ProductCategoryRecord record) {
        return ProductCategory.of(
                record.categoryCode(),
                record.categoryName(),
                record.displayOrder(),
                record.isActive(),
                record.description()
        );
    }

    private ProductCategoryRecord toRecord(ProductCategory category, LocalDateTime now) {
        return new ProductCategoryRecord(
                category.categoryCode().value(),
                category.categoryName().value(),
                category.displayOrder().value(),
                category.activeFlag().value(),
                category.description().value(),
                now,
                now
        );
    }
}
