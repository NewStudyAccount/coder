package com.example.demo.otc;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface IddMapper {

    @Select("SELECT cust_id FROM oc_order_customer WHERE order_id = #{orderId}")
    Long getCustIdByOrderId(@Param("orderId") Long orderId);

    @Select("SELECT * FROM oc_order_line_item WHERE order_id = #{orderId} AND order_line_id = #{orderLineId} AND attr_code LIKE 'CustLevelIDDCustId%' AND modify_tag = '0'")
    List<OrderLineItem> getOrderLineItems(@Param("orderId") Long orderId, @Param("orderLineId") Long orderLineId);

    @Insert("INSERT INTO oc_order_relation_cc (cust_id_a, cust_id_b, start_date, end_date, modify_tag) VALUES (#{custIdA}, #{custIdB}, #{startDate}, #{endDate}, #{modifyTag})")
    void insertRelationCc(OcOrderRelationCc relationCc);

    @Update("UPDATE oc_order_line SET serial_number = #{serialNumber}, sn_cust_id = #{snCustId} WHERE order_id = #{orderId} AND order_line_id = #{orderLineId}")
    void updateOrderLine(OcOrderLine orderLine);
}
