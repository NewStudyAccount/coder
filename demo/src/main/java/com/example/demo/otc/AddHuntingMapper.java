package com.example.demo.otc;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface AddHuntingMapper {

    @Select("SELECT COUNT(1) FROM oc_order_item WHERE order_id = #{orderId} AND attr_code = 'is_add_on_hunting' AND attr_value = '1'")
    int checkAddOnHunting(@Param("orderId") Long orderId);

    @Select("SELECT cancel_tag FROM oc_order WHERE order_id = #{orderId}")
    String getOrderCancelTag(@Param("orderId") Long orderId);

    @Update("UPDATE oc_order SET cancel_tag = '4' WHERE order_id = #{orderId}")
    void updateOrderCancelTag(@Param("orderId") Long orderId);

    @Select("SELECT COUNT(1) FROM oc_order_item WHERE order_id = #{orderId} AND attr_code = 'initial_normal_order_id' AND attr_value = #{attrValue} AND modify_tag = '0'")
    int checkInitialNormalOrderId(@Param("orderId") Long orderId, @Param("attrValue") String attrValue);

    @Insert("INSERT INTO oc_order_item (order_id, attr_code, attr_value, modify_tag, start_date, end_date) " +
            "VALUES (#{orderId}, #{attrCode}, #{attrValue}, #{modifyTag}, #{startDate}, #{endDate})")
    void insertOrderItem(OcOrderItem item);

    @Select("SELECT SYSDATE FROM DUAL")
    LocalDateTime getSysDate();
}
