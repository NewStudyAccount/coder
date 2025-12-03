package com.example.demo.otc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UuRelationService {

    private final JdbcTemplate jdbcTemplate;
    private final UserCenterClient userCenterClient;

    public UuRelationService(JdbcTemplate jdbcTemplate, UserCenterClient userCenterClient) {
        this.jdbcTemplate = jdbcTemplate;
        this.userCenterClient = userCenterClient;
    }

    public UuRelationResponse generateUuLedger(UuRelationRequest req) {
        UuRelationResponse resp = new UuRelationResponse();
        UuLedger ledger = new UuLedger();

        // 步骤1：查callSequence
        String callSequence = queryOrderLineItemAttr(req.order_id, req.order_line_id, "Call Sequence");
        // 步骤2：查isPrimaryNumber
        String isPrimaryNumber = queryOrderLineItemAttr(req.order_id, req.order_line_id, "Is Primary Number");

        // 步骤3：查本号码改号订单行
        SerialOrderInfo serialOrder1 = querySerialOrder(req.order_id, req.serial_number, req.parent_serial_number, "1");
        String serialNumberB = null;
        if (serialOrder1 != null) {
            serialNumberB = queryOrderLineItemAttr(serialOrder1.order_id, serialOrder1.order_line_id, "New Serial Number");
        }

        // 步骤4.1：查群组改号订单
        SerialOrderInfo serialOrder2 = querySerialOrder(req.order_id, req.parent_serial_number, null, "2");
        String serialNumberA_4 = null;
        if (serialOrder2 != null) {
            serialNumberA_4 = queryOrderLineItemAttr(serialOrder2.order_id, serialOrder2.order_line_id, "New Serial Number");
        }

        // 步骤5.1：查用户关系
        UserRelationInfo userRel;
        if (serialOrder1 != null) {
            userRel = userCenterClient.queryUserRelation(req.sn_user_id, null);
        } else {
            userRel = userCenterClient.queryUserRelation(req.sn_user_id, req.parent_serial_number);
        }

        if (userRel == null) {
            resp.success = false;
            resp.message = "未找到用户关系信息";
            return resp;
        }

        // 组装台账数据
        ledger.order_id = req.order_id;
        ledger.order_line_id = req.order_line_id;
        ledger.relation_type_code = userRel.relation_type_code;
        ledger.user_id_a = userRel.user_id_a;
        ledger.user_id_b = userRel.user_id_b;
        ledger.modify_tag = 2;
        ledger.start_date = userRel.start_date;
        ledger.end_date = LocalDateTime.of(2099, 12, 31, 23, 59, 59);

        // 赋值逻辑
        if (serialOrder1 != null) {
            // 步骤5.1.0
            if (Objects.equals(userRel.primary_serial_number, req.parent_serial_number)) {
                ledger.serial_number_a = serialNumberA_4 != null ? serialNumberA_4 : userRel.primary_serial_number;
                ledger.call_sequence = callSequence != null ? callSequence : userRel.call_sequence;
                ledger.is_primary_number = isPrimaryNumber != null ? isPrimaryNumber : userRel.is_main_number;
            } else {
                ledger.serial_number_a = userRel.primary_serial_number;
                ledger.call_sequence = userRel.call_sequence;
                ledger.is_primary_number = userRel.is_main_number;
            }
            ledger.serial_number_b = serialNumberB != null ? serialNumberB : userRel.serial_number_b;
        } else {
            ledger.serial_number_b = serialNumberB != null ? serialNumberB : userRel.serial_number_b;
            ledger.serial_number_a = serialNumberA_4 != null ? serialNumberA_4 : userRel.primary_serial_number;
            ledger.call_sequence = callSequence != null ? callSequence : userRel.call_sequence;
            ledger.is_primary_number = isPrimaryNumber != null ? isPrimaryNumber : userRel.is_main_number;
        }

        // 插入台账
        insertUuLedger(ledger);

        resp.success = true;
        resp.message = "台账生成成功";
        resp.ledger = ledger;
        return resp;
    }

    private String queryOrderLineItemAttr(Long orderId, Long orderLineId, String attrCode) {
        String sql = "select attr_value from oc_order_line_item where order_id = ? and order_line_id = ? and attr_code = ? and modify_tag in ('0','2') limit 1";
        List<String> list = jdbcTemplate.query(sql, new Object[]{orderId, orderLineId, attrCode}, (rs, rn) -> rs.getString("attr_value"));
        return list.isEmpty() ? null : list.get(0);
    }

    private SerialOrderInfo querySerialOrder(Long orderId, String serialNumber, String parentSerialNumber, String lineLevel) {
        String sql = "select order_id, order_line_id from oc_order_line where order_id = ? and serial_number = ?"
                + (parentSerialNumber != null ? " and parent_serial_number = ?" : "")
                + " and trade_type_code = '279' and cancel_tag = '0' and line_level = ?";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        params.add(serialNumber);
        if (parentSerialNumber != null) params.add(parentSerialNumber);
        params.add(lineLevel);
        List<SerialOrderInfo> list = jdbcTemplate.query(sql, params.toArray(), (rs, rn) -> {
            SerialOrderInfo info = new SerialOrderInfo();
            info.order_id = rs.getLong("order_id");
            info.order_line_id = rs.getLong("order_line_id");
            return info;
        });
        return list.isEmpty() ? null : list.get(0);
    }

    private void insertUuLedger(UuLedger ledger) {
        String sql = "insert into oc_order_relation_uu (order_id, order_line_id, relation_type_code, user_id_a, user_id_b, serial_number_a, serial_number_b, call_sequence, is_primary_number, modify_tag, start_date, end_date) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                ledger.order_id,
                ledger.order_line_id,
                ledger.relation_type_code,
                ledger.user_id_a,
                ledger.user_id_b,
                ledger.serial_number_a,
                ledger.serial_number_b,
                ledger.call_sequence,
                ledger.is_primary_number,
                ledger.modify_tag,
                Timestamp.valueOf(ledger.start_date),
                Timestamp.valueOf(ledger.end_date)
        );
    }

    // 用户中心服务、tf_F_user_relation 查询桩
    public static class UserRelationInfo {
        public String relation_type_code;
        public String user_id_a;
        public String user_id_b;
        public String primary_serial_number;
        public String serial_number_b;
        public String call_sequence;
        public String is_main_number;
        public LocalDateTime start_date;
    }

    public static class SerialOrderInfo {
        public Long order_id;
        public Long order_line_id;
    }

    // 用户中心服务接口
    public interface UserCenterClient {
        UserRelationInfo queryUserRelation(String snUserId, String parentSerialNumber);
    }
}