package com.admin.mapper;

import com.admin.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {

    List<Order> selectList(@Param("orderNo") String orderNo, @Param("memberId") Long memberId,
                           @Param("status") Integer status);

    Order selectById(Long id);

    Order selectByOrderNo(String orderNo);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
