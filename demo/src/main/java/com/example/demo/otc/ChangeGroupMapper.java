package com.example.demo.otc;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ChangeGroupMapper {

    @Select("SELECT * FROM oc_order_line WHERE order_id = #{orderId} AND trade_type_code = '279' AND sence_type = '27900' AND net_type_code = 'CP' AND cancel_tag = '0'")
    OcOrderLine getChangeGroupOrderLine(@Param("orderId") Long orderId);

    @Select("SELECT attr_value FROM oc_order_line_item WHERE order_id = #{orderId} AND order_line_id = #{orderLineId} AND attr_code = 'New Sserial Number'")
    String getNewSerialNumber(@Param("orderId") Long orderId, @Param("orderLineId") Long orderLineId);
    
    // 假设 oc_order_line 表里有 parent_serial_number 字段
    @Select("SELECT * FROM oc_order_line WHERE order_id = #{orderId} AND order_line_id = #{orderLineId}")
    OcOrderLine getOrderLineById(@Param("orderId") Long orderId, @Param("orderLineId") Long orderLineId);

    @Select("SELECT * FROM oc_order_line WHERE order_id = #{orderId} AND trade_type_code IN ('340', '279') AND parent_serial_number = #{parentSerialNumber}")
    List<OcOrderLine> getMemberOrderLines(@Param("orderId") Long orderId, @Param("parentSerialNumber") String parentSerialNumber);

    @Insert("INSERT INTO oc_order_line_item (order_id, order_line_id, attr_code, attr_value, modify_tag, start_date, end_date) " +
            "VALUES (#{orderId}, #{orderLineId}, #{attrCode}, #{attrValue}, #{modifyTag}, #{startDate}, #{endDate})")
    void insertOrderLineItem(OrderLineItem item);
    
    // 增加获取 SRD 的方法 (这里模拟，实际上可能从 oc_order_line 获取)
    @Select("SELECT SYSDATE FROM DUAL")
    LocalDateTime getSysDate();
}
