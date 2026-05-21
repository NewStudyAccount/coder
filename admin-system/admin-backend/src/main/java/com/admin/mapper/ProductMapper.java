package com.admin.mapper;

import com.admin.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    List<Product> selectList(@Param("name") String name, @Param("categoryId") Long categoryId,
                             @Param("status") Integer status);

    Product selectById(Long id);

    int insert(Product product);

    int update(Product product);

    int deleteById(Long id);

    int deleteBatch(List<Long> ids);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
