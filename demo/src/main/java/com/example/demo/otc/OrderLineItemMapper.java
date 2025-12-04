package com.example.demo.otc;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderLineItemMapper {

    @Select("SELECT * FROM oc_order_line_item WHERE order_id = #{orderId} AND order_line_id = #{orderLineId} AND attr_code = #{attrCode} AND modify_tag = #{modifyTag}")
    OrderLineItem selectByOrderAndAttr(@Param("orderId") Long orderId,
                                       @Param("orderLineId") Long orderLineId,
                                       @Param("attrCode") String attrCode,
                                       @Param("modifyTag") String modifyTag);
}