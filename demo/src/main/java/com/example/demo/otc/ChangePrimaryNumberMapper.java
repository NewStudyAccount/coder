package com.example.demo.otc;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChangePrimaryNumberMapper {

    // 0. Check trigger condition
    @Select("SELECT * FROM oc_order_line WHERE order_id = #{orderId} AND trade_type_code = '279' AND scene_type = '27900' AND cancel_tag IN ('0', '5') AND net_type_code IN ('CP', 'CE')")
    List<OcOrderLine> queryTriggerOrderLine(@Param("orderId") Long orderId);

    // 1. Get new serial number
    @Select("SELECT attr_value FROM oc_order_line_item WHERE order_id = #{orderId} AND order_line_id = #{orderLineId} AND attr_code = 'New Serial Number' AND modify_tag = '0'")
    String getNewSerialNumber(@Param("orderId") Long orderId, @Param("orderLineId") Long orderLineId);

    // 2. Get member relations
    @Select("SELECT * FROM tf_f_user_relation WHERE user_id_a = #{userIdA} AND end_date > SYSDATE")
    List<TfFUserRelation> getMemberRelations(@Param("userIdA") Long userIdA);

    // 2.1 Check existing relation in oc_order_relation_uu
    @Select("SELECT COUNT(1) FROM oc_order_relation_uu WHERE order_id = #{orderId} AND user_id_a = #{userIdA} AND user_id_b = #{userIdB} AND relation_type_code = #{relationTypeCode}")
    int countExistingRelationChange(@Param("orderId") Long orderId, @Param("userIdA") Long userIdA, @Param("userIdB") Long userIdB, @Param("relationTypeCode") String relationTypeCode);

    // Helper: Get User Info
    @Select("SELECT * FROM tf_f_user WHERE user_id = #{userId}")
    TfFUser getUserInfo(@Param("userId") Long userId);

    @Insert("INSERT INTO oc_order_line (order_id, order_line_id, serial_number, sn_user_id, parent_serial_number, trade_type_code, scene_type, sn_cust_id, main_product_id, net_type_code, line_level, srd, depart_id, channel_id, channel_type, city_code, eparchy_code, province_code, accept_date, cancel_tag, update_time, in_mode_code) " +
            "VALUES (#{orderId}, #{orderLineId}, #{serialNumber}, #{snUserId}, #{parentSerialNumber}, #{tradeTypeCode}, #{sceneType}, #{snCustId}, #{mainProductId}, #{netTypeCode}, #{lineLevel}, #{srd}, #{departId}, #{channelId}, #{channelType}, #{cityCode}, #{eparchyCode}, #{provinceCode}, #{acceptDate}, #{cancelTag}, #{updateTime}, #{inModeCode})")
    void insertOcOrderLine(OcOrderLine line);

    @Insert("INSERT INTO oc_order_relation_uu (order_id, order_line_id, user_id_a, serial_number_a, role_id_a, relation_type_code, user_id_b, serial_number_b, role_id_b, call_sequence, is_primary_number, modify_tag, start_date, end_date) " +
            "VALUES (#{orderId}, #{orderLineId}, #{userIdA}, #{serialNumberA}, #{roleIdA}, #{relationTypeCode}, #{userIdB}, #{serialNumberB}, #{roleIdB}, #{callSequence}, #{isPrimaryNumber}, #{modifyTag}, #{startDate}, #{endDate})")
    void insertOcOrderRelationUu(OcOrderRelationUu relation);
}
