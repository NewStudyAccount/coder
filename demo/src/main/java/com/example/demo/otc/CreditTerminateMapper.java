package com.example.demo.otc;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CreditTerminateMapper {

    @Select("SELECT user_id FROM tf_f_payrelation WHERE account_id = #{accountId} AND end_date > SYSDATE")
    List<Long> getUserIdsByAccountId(@Param("accountId") Long accountId);

    @Select("SELECT * FROM tf_f_user_svcstate WHERE user_id = #{userId} AND main_tag = '1' AND service_state_code IN ('4', '5') AND end_date > SYSDATE")
    TfFUserSvcState getUserCeaseSvcState(@Param("userId") Long userId);

    @Select("SELECT * FROM oc_order_line WHERE user_id = #{userId} AND trade_type_code IN ('136', '7220')")
    List<OcOrderLine> getInFlightCeaseOrderLines(@Param("userId") Long userId);

    @Select("SELECT start_date FROM tf_f_user_svcstate WHERE user_id = #{userId} AND main_tag = '1' AND service_state_code IN ('4', '5') AND end_date > SYSDATE")
    LocalDate getUserCeaseStartDate(@Param("userId") Long userId);
    
    // 假设 oc_order_line 有 srd 字段
    @Select("SELECT srd FROM oc_order_line WHERE order_id = #{orderId} AND order_line_id = #{orderLineId}")
    LocalDate getOrderLineSrd(@Param("orderId") Long orderId, @Param("orderLineId") Long orderLineId);

    @Insert("INSERT INTO oc_order_line_item (order_id, order_line_id, attr_code, attr_value, modify_tag, start_date, end_date) " +
            "VALUES (#{orderId}, #{orderLineId}, #{attrCode}, #{attrValue}, #{modifyTag}, #{startDate}, #{endDate})")
    void insertOrderLineItem(OrderLineItem item);

    @Insert("INSERT INTO oc_order_line (order_id, order_line_id, serial_number, user_id, net_type_code, main_product_id, main_product_name, main_product_type, product_family, trade_type_code, scene_type) " +
            "VALUES (#{orderId}, #{orderLineId}, #{serialNumber}, #{userId}, #{netTypeCode}, #{mainProductId}, #{mainProductName}, #{mainProductType}, #{productFamily}, #{tradeTypeCode}, #{sceneType})")
    void insertOrderLine(OcOrderLine line);

    @Insert("INSERT INTO oc_order_item (order_id, attr_code, attr_value, modify_tag, start_date, end_date) " +
            "VALUES (#{orderId}, #{attrCode}, #{attrValue}, #{modifyTag}, #{startDate}, #{endDate})")
    void insertOrderItem(OcOrderItem item);

    @Update("UPDATE oc_order_line SET order_node_state = '04', cancel_tag = 'Z', remark = #{remark} WHERE order_id = #{orderId} AND order_line_id = #{orderLineId}")
    void terminateOrderLine(@Param("orderId") Long orderId, @Param("orderLineId") Long orderLineId, @Param("remark") String remark);

    @Select("SELECT * FROM tf_f_user WHERE user_id = #{userId}")
    TfFUser getUserInfo(@Param("userId") Long userId);

    // 检查用户是否在 IDAP 关系中
    @Select("SELECT relation_type_code FROM tf_f_user_relation WHERE user_id = #{userId} AND end_date > SYSDATE")
    String getUserRelationTypeCode(@Param("userId") Long userId);

    // 获取序列号 (模拟)
    @Select("SELECT serial_number FROM oc_order_line WHERE order_id = #{orderId} AND order_line_id = #{orderLineId}")
    String getSerialNumber(@Param("orderId") Long orderId, @Param("orderLineId") Long orderLineId);
    
    @Select("SELECT SYSDATE FROM DUAL")
    LocalDateTime getSysDate();
    
    // 模拟获取香港公共假期 (需要建表维护)
    @Select("SELECT holiday_date FROM hk_public_holidays")
    List<LocalDate> getHkPublicHolidays();

    // 1.1 在途单判断
    @Select("SELECT * FROM oc_order_line WHERE user_id = #{userId} AND trade_type_code NOT IN ('7230', '615') AND cancel_tag = '0' AND line_level IN (0, 1) AND (produce_order_state IS NULL OR produce_order_state < 1)")
    List<OcOrderLine> queryOcOrderLineForInFlight(@Param("userId") Long userId);

    // 1.1.1 查询 oc_order_payrelation
    @Select("SELECT COUNT(1) FROM oc_order_payrelation WHERE order_id = #{orderId} AND order_line_id = #{orderLineId} AND account_id = #{accountId} AND modify_tag = #{modifyTag}")
    int countOcOrderPayRelation(@Param("orderId") Long orderId, @Param("orderLineId") Long orderLineId, @Param("accountId") Long accountId, @Param("modifyTag") String modifyTag);

    // 1.1.2 查询 oc_order_payrelation
    @Select("SELECT trade_type_code FROM oc_order_payrelation WHERE order_id = #{orderId} AND order_line_id = #{orderLineId} AND account_id = #{accountId} AND modify_tag = #{modifyTag}")
    List<String> queryOcOrderPayRelationTradeTypes(@Param("orderId") Long orderId, @Param("orderLineId") Long orderLineId, @Param("accountId") Long accountId, @Param("modifyTag") String modifyTag);

    // 1.1.2 根据 user_id 和 date 查询 tf_f_payrelation
    @Select("SELECT account_id FROM tf_f_payrelation WHERE user_id = #{userId} AND end_date > SYSDATE")
    List<Long> getAccountIdsByUserId(@Param("userId") Long userId);

    // 1.1.2.2 查询 tf_b_trade_fulfill_action
    @Select("SELECT COUNT(1) FROM tf_b_trade_fulfill_action WHERE order_id = #{orderId} AND order_line_id = #{orderLineId} AND action_id IN ('Temp Comp', 'comp')")
    int countTradeFulfillAction(@Param("orderId") Long orderId, @Param("orderLineId") Long orderLineId);

    // 1.1.3 更新 cancel_tag
    @Update("UPDATE oc_order_line SET cancel_tag = #{cancelTag} WHERE order_id = #{orderId} AND order_line_id = #{orderLineId}")
    void updateOcOrderLineCancelTag(@Param("orderId") Long orderId, @Param("orderLineId") Long orderLineId, @Param("cancelTag") String cancelTag);

    // 2.1 获取 account_id 下的新开在途单
    @Select("SELECT DISTINCT order_id, order_line_id FROM oc_order_payrelation WHERE account_id = #{accountId} AND modify_tag = '0'")
    List<OcOrderPayRelation> queryNewInFlightOrders(@Param("accountId") Long accountId);

    // 2.1.1 查询 oc_order_line for new in flight
    @Select("SELECT * FROM oc_order_line WHERE order_id = #{orderId} AND order_line_id = #{orderLineId} AND trade_type_code != '615' AND cancel_tag = '0' AND (produce_order_state IS NULL OR produce_order_state < 1)")
    List<OcOrderLine> queryOcOrderLineForNewInFlight(@Param("orderId") Long orderId, @Param("orderLineId") Long orderLineId);

    // 3.1 查询 oc_order_line 获取对应的订单行列表
    @Select("SELECT * FROM oc_order_line WHERE order_id = #{orderId} AND line_level IN (0, 2) AND cancel_tag = '0'")
    List<OcOrderLine> queryOcOrderLinesForCancel(@Param("orderId") Long orderId);

    // 辅助查询：根据 ID 获取订单行
    @Select("SELECT * FROM oc_order_line WHERE order_id = #{orderId} AND order_line_id = #{orderLineId}")
    OcOrderLine queryOcOrderLineById(@Param("orderId") Long orderId, @Param("orderLineId") Long orderLineId);
}
