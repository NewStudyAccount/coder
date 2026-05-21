package com.admin.mapper;

import com.admin.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    List<OrderItem> selectByOrderId(Long orderId);

    int batchInsert(List<OrderItem> items);
}
