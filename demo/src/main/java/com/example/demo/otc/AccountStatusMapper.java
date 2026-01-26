package com.example.demo.otc;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AccountStatusMapper {

    @Select("SELECT * FROM tf_f_account WHERE account_status = #{status}")
    List<TfFAccount> queryAccountsByStatus(@Param("status") Integer status);

    @Select("SELECT COUNT(1) FROM tf_f_payrelation WHERE account_id = #{accountId} AND end_date > NOW()")
    int countActiveUsersByAccount(@Param("accountId") Long accountId);

    @Select("SELECT * FROM oc_order_payrelation WHERE account_id = #{accountId} AND modify_tag = '0'")
    List<OcOrderPayRelation> queryOrderPayRelations(@Param("accountId") Long accountId);

    @Select("SELECT COUNT(1) FROM oc_order_line WHERE order_id = #{orderId} AND order_line_id = #{orderLineId} AND cancel_tag = '0'")
    int countActiveOrderLines(@Param("orderId") Long orderId, @Param("orderLineId") Long orderLineId);

    @Update("UPDATE tf_f_account SET account_status = #{newStatus}, status_date = NOW() WHERE account_id = #{accountId}")
    void updateAccountStatus(@Param("accountId") Long accountId, @Param("newStatus") Integer newStatus);
}
