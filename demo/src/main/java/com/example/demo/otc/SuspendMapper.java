package com.example.demo.otc;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SuspendMapper {

    @Select("SELECT * FROM tf_f_payrelation WHERE account_id = #{accountId} AND end_date > SYSDATE")
    List<TfFPayRelation> getPayRelationsByAccountId(@Param("accountId") Long accountId);

    @Select("SELECT * FROM tf_f_user_svcstate WHERE user_id = #{userId} AND end_date > SYSDATE AND service_state_code = '0'")
    List<TfFUserSvcState> getUserNormalSvcStates(@Param("userId") Long userId);

    @Select("SELECT * FROM tf_f_payrelation WHERE user_id = #{userId} AND account_id = #{accountId} AND price_tag = #{priceTag} AND end_date > SYSDATE")
    List<TfFPayRelation> getPayRelationsByUserAndAccount(@Param("userId") Long userId, 
                                                         @Param("accountId") Long accountId, 
                                                         @Param("priceTag") String priceTag);

    @Select("SELECT * FROM tf_f_user_svcstate WHERE user_id = #{userId} AND service_id = #{serviceId} AND end_date > SYSDATE")
    TfFUserSvcState getUserSvcStateByServiceId(@Param("userId") Long userId, @Param("serviceId") String serviceId);

    @Select("SELECT * FROM tf_f_user_svcstate WHERE user_id = #{userId} AND end_date > SYSDATE")
    List<TfFUserSvcState> getAllUserSvcStates(@Param("userId") Long userId);

    @Select("SELECT * FROM tf_f_payrelation WHERE user_id = #{userId} AND end_date > SYSDATE")
    List<TfFPayRelation> getUserPayRelations(@Param("userId") Long userId);

    @Insert("INSERT INTO oc_order_num_svcstate (order_id, order_line_id, serial_number, user_id, service_id, service_state_code, main_tag, modify_tag, start_date, end_date) " +
            "VALUES (#{orderId}, #{orderLineId}, #{serialNumber}, #{userId}, #{serviceId}, #{serviceStateCode}, #{mainTag}, #{modifyTag}, #{startDate}, #{endDate})")
    void insertOrderNumSvcState(OcOrderNumSvcState svcState);
    
    // 模拟从 oc_order_line_item 获取 account_id (这里简化为直接查)
    @Select("SELECT attr_value FROM oc_order_line_item WHERE order_id = #{orderId} AND order_line_id = #{orderLineId} AND attr_code = 'ACCOUNT_ID'")
    String getAccountIdFromOrderLine(@Param("orderId") Long orderId, @Param("orderLineId") Long orderLineId);

    // 模拟归档操作 (更新 modify_tag 或状态)
    @Insert("UPDATE oc_order_line SET order_state = 'ARCHIVED' WHERE order_id = #{orderId}")
    void archiveOrder(@Param("orderId") Long orderId);
}
