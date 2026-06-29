package com.github.massakai.mastermanagement.infrastructure.persistence.category;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductCategoryMapper {

    List<ProductCategoryRecord> selectAllOrdered();

    int deleteAll();

    int insert(ProductCategoryRecord record);
}
