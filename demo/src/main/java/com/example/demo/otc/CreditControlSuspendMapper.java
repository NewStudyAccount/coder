package com.example.demo.otc;

import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CreditControlSuspendMapper {

    // 1.0 Get User List
    @Select("SELECT * FROM oc_order_item WHERE order_id = #{orderId} AND attr_code = 'sr_user_list' AND attr_value = '1'")
    List<OcOrderItem> selectOcOrderItemByUserList(@Param("orderId") Long orderId);

    @Select("SELECT DISTINCT user_id FROM tf_F_payrelation WHERE account_id = #{accountId} AND end_date > SYSDATE")
    List<Long> selectUsersFromPayRelation(@Param("accountId") Long accountId);

    // 1.1 Check In-flight Disconnection Orders
    @Select("<script>" +
            "SELECT * FROM oc_order_line WHERE sn_user_id = #{userId} " +
            "AND trade_type_code IN ('7230') " +
            "AND cancel_tag = '0' " +
            "AND line_level IN (0, 1) " +
            "AND (produce_order_state IS NULL OR produce_order_state < 1)" +
            "</script>")
    List<OcOrderLine> selectInFlightOrders(@Param("userId") Long userId);

    @Update("UPDATE oc_order_line SET cancel_tag = 'Z' WHERE order_id = #{orderId} AND order_line_id = #{orderLineId}")
    void updateOrderLineCancelTag(@Param("orderId") Long orderId, @Param("orderLineId") Long orderLineId);

    // 2.1 Get Lines for Cancellation/Amendment
    @Select("SELECT * FROM oc_order_line WHERE order_id = #{orderId} AND line_level IN (0, 2)")
    List<OcOrderLine> selectOrderLinesForAmendment(@Param("orderId") Long orderId);
}
