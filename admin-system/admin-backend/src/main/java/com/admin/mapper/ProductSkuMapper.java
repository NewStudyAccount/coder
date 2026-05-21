package com.admin.mapper;

import com.admin.entity.ProductSku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductSkuMapper {

    List<ProductSku> selectByProductId(Long productId);

    ProductSku selectById(Long id);

    int insert(ProductSku sku);

    int update(ProductSku sku);

    int deleteById(Long id);

    int deleteByProductId(Long productId);

    int batchInsert(@Param("list") List<ProductSku> skuList);
}
