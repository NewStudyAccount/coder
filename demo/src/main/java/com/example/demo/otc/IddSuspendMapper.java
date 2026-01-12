package com.example.demo.otc;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface IddSuspendMapper {

    @Select("SELECT attr_value FROM oc_order_item WHERE order_id = #{orderId} AND attr_code = 'sr_user_list' AND attr_value IS NOT NULL AND attr_value != ''")
    String getSrUserList(@Param("orderId") Long orderId);

    @Select("SELECT user_id FROM tf_f_payrelation WHERE account_id = #{accountId} AND end_date > SYSDATE")
    List<Long> getUserIdsByAccountId(@Param("accountId") Long accountId);

    @Select("SELECT * FROM tf_f_user_svcstate WHERE user_id = #{userId} AND service_id = #{serviceId} AND end_date > SYSDATE")
    TfFUserSvcState getUserSvcState(@Param("userId") Long userId, @Param("serviceId") String serviceId);

    @Insert("INSERT INTO oc_order_num_svcstate (order_id, order_line_id, serial_number, user_id, service_id, service_state_code, main_tag, modify_tag, start_date, end_date) " +
            "VALUES (#{orderId}, #{orderLineId}, #{serialNumber}, #{userId}, #{serviceId}, #{serviceStateCode}, #{mainTag}, #{modifyTag}, #{startDate}, #{endDate})")
    void insertOrderNumSvcState(OcOrderNumSvcState svcState);

    @Insert("UPDATE oc_order_line SET order_state = 'ARCHIVED' WHERE order_id = #{orderId}")
    void archiveOrder(@Param("orderId") Long orderId);

    // 模拟从 oc_order_line 获取 serial_number (作为 account_id)
    @Select("SELECT serial_number FROM oc_order_line WHERE order_id = #{orderId} AND order_line_id = #{orderLineId}")
    String getSerialNumberAsAccountId(@Param("orderId") Long orderId, @Param("orderLineId") Long orderLineId);
}
