package com.example.demo.otc;

import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CreditControlSuspendMapper {

    @Select("SELECT * FROM oc_order_item WHERE order_id = #{orderId} AND attr_code = #{attrCode} AND attr_value = #{attrValue}")
    List<OcOrderItem> selectOcOrderItemByOrderAndAttr(@Param("orderId") Long orderId, @Param("attrCode") String attrCode, @Param("attrValue") String attrValue);

    @Select("SELECT DISTINCT user_id FROM tf_F_payrelation WHERE account_id = #{accountId} AND end_date > SYSDATE")
    List<Long> selectUsersFromPayRelation(@Param("accountId") Long accountId);

    @Select("<script>" +
            "SELECT * FROM oc_order_line WHERE sn_user_id = #{userId} " +
            "AND trade_type_code NOT IN ('192', '7230', '126', '7220', '7301', '7203', '615') " +
            "AND cancel_tag = '0' " +
            "AND line_level IN (0, 1) " +
            "AND (produce_order_state IS NULL OR produce_order_state < 1)" +
            "</script>")
    List<OcOrderLine> selectInFlightOrders(@Param("userId") Long userId);

    @Select("SELECT * FROM tf_b_trade_fulfill_action WHERE order_id = #{orderId} AND order_line_id = #{orderLineId} AND action_id IN ('Temp Comp', 'comp')")
    List<TfBTradeFulfillAction> selectFulfillAction(@Param("orderId") Long orderId, @Param("orderLineId") Long orderLineId);

    @Select("SELECT * FROM oc_order_payrelation WHERE order_id = #{orderId} AND order_line_id = #{orderLineId} AND account_id = #{accountId} AND modify_tag = #{modifyTag}")
    List<OcOrderPayRelation> selectOrderPayRelation(@Param("orderId") Long orderId, @Param("orderLineId") Long orderLineId, @Param("accountId") Long accountId, @Param("modifyTag") String modifyTag);

    @Select("SELECT DISTINCT account_id FROM tf_F_payrelation WHERE user_id = #{userId} AND end_date > SYSDATE")
    List<Long> selectPayRelationAccount(@Param("userId") Long userId);

    @Update("UPDATE oc_order_line SET cancel_tag = 'Z' WHERE order_id = #{orderId} AND order_line_id = #{orderLineId}")
    void updateOrderLineCancelTag(@Param("orderId") Long orderId, @Param("orderLineId") Long orderLineId);

    @Select("SELECT DISTINCT order_id, order_line_id FROM oc_order_payrelation WHERE account_id = #{accountId} AND modify_tag = '0'")
    List<OcOrderPayRelation> selectOrderPayRelationByAccount(@Param("accountId") Long accountId);

    @Select("<script>" +
            "SELECT * FROM oc_order_line WHERE order_id = #{orderId} AND order_line_id = #{orderLineId} " +
            "AND trade_type_code != '615' " +
            "AND cancel_tag = '0' " +
            "AND (produce_order_state IS NULL OR produce_order_state < 1)" +
            "</script>")
    List<OcOrderLine> selectInFlightOrdersByLineId(@Param("orderId") Long orderId, @Param("orderLineId") Long orderLineId);

    @Select("SELECT * FROM oc_order_line WHERE order_id = #{orderId} AND cancel_tag = 'Z'")
    List<OcOrderLine> selectOrderLinesForAmendment(@Param("orderId") Long orderId);
}
