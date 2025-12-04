package com.example.demo.otc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserRelationService {

    private final JdbcTemplate jdbcTemplate;
    private final UuRelationService.UserCenterClient userCenterClient;

    public UserRelationService(JdbcTemplate jdbcTemplate, UuRelationService.UserCenterClient userCenterClient) {
        this.jdbcTemplate = jdbcTemplate;
        this.userCenterClient = userCenterClient;
    }

    public UserRelationResponse generateUserLedger(UserRelationRequest req) {
        UserRelationResponse resp = new UserRelationResponse();

        // 步骤0：查 newSerialNumberB
        String newSerialNumberB = queryOrderLineItemAttr(req.order_id, req.order_line_id, "New Serial Number");
        if (newSerialNumberB == null || newSerialNumberB.isEmpty()) {
            return failResp("未找到新号码（New Serial Number），无法生成台账");
        }

        // 步骤1：查是否有Call_Sequence修改订单
        String callSequence = getOrderItemAttrIfOrderExists(
                "select order_id, order_line_id from oc_order_line where order_id = ? and serial_number = ? and parent_serial_number = ? and trade_type_code = '340' and scene_type = '34005' and cancel_tag = '0'",
                req.order_id, req.serial_number, req.parent_serial_number, "Call sequence"
        );
        if (callSequence != null) {
            return buildLedgerResp(req, newSerialNumberB, callSequence, null, null, getUserRelation(req, null), "台账生成成功");
        }

        // 步骤2：查是否有is_main_number修改订单
        String isMainNumber = getOrderItemAttrIfOrderExists(
                "select order_id, order_line_id from oc_order_line where order_id = ? and serial_number = ? and parent_serial_number = ? and trade_type_code = '340' and scene_type in ('34006','34007') and cancel_tag = '0'",
                req.order_id, req.serial_number, req.parent_serial_number, "Is Primary Number"
        );
        if (isMainNumber != null) {
            return buildLedgerResp(req, newSerialNumberB, null, isMainNumber, null, getUserRelation(req, null), "台账生成成功");
        }

        // 步骤3.1：查群组改号订单
        String serialNumberA = getOrderItemAttrIfOrderExists(
                "select order_id, order_line_id from oc_order_line where order_id = ? and serial_number = ? and trade_type_code = '279' and cancel_tag = '0' and line_level = '2'",
                req.order_id, req.parent_serial_number, null, "New Serial Number"
        );

        // 步骤4：判断是否为citinet/one/ec成员改号
        String userDiffCode = queryUserDiffCode(req.parent_serial_number);
        boolean isCitinetOneEc = userDiffCode != null && (userDiffCode.equals("C001") || userDiffCode.equals("O002") || userDiffCode.equals("E002"));

        // 步骤4.1：查用户关系
        UuRelationService.UserRelationInfo userRel = getUserRelation(req, isCitinetOneEc ? null : req.parent_serial_number);
        if (userRel == null) {
            return failResp("未找到用户关系信息");
        }

        // serialNumberA赋值逻辑
        if (Objects.equals(userRel.primary_serial_number, req.parent_serial_number) && serialNumberA != null) {
            // serialNumberA 保持群组改号订单值
        } else {
            serialNumberA = userRel.primary_serial_number;
        }

        // 组装台账
        UserLedger ledger = new UserLedger();
        ledger.order_id = req.order_id;
        ledger.order_line_id = req.order_line_id;
        ledger.relation_type_code = userRel.relation_type_code;
        ledger.user_id_a = userRel.user_id_a;
        ledger.user_id_b = userRel.user_id_b;
        ledger.serial_number_b = newSerialNumberB;
        ledger.serial_number_a = serialNumberA;
        ledger.call_sequence = callSequence != null ? callSequence : userRel.call_sequence;
        ledger.is_primary_number = isMainNumber != null ? isMainNumber : userRel.is_main_number;
        ledger.modify_tag = 2;
        ledger.start_date = userRel.start_date;
        ledger.end_date = LocalDateTime.of(2099, 12, 31, 23, 59, 59);

        insertUserLedger(ledger);

        resp.success = true;
        resp.message = "台账生成成功";
        resp.ledger = ledger;
        return resp;
    }

    // 工具方法：失败响应
    private UserRelationResponse failResp(String msg) {
        UserRelationResponse resp = new UserRelationResponse();
        resp.success = false;
        resp.message = msg;
        resp.ledger = null;
        return resp;
    }

    // 工具方法：如有订单则查属性
    private String getOrderItemAttrIfOrderExists(String sql, Object p1, Object p2, Object p3, String attrCode) {
        List<Map<String, Object>> orders = p3 != null
                ? jdbcTemplate.queryForList(sql, p1, p2, p3)
                : jdbcTemplate.queryForList(sql, p1, p2);
        if (!orders.isEmpty()) {
            Map<String, Object> order = orders.get(0);
            return queryOrderLineItemAttr((Long) order.get("order_id"), (Long) order.get("order_line_id"), attrCode);
        }
        return null;
    }

    // 工具方法：查用户关系
    private UuRelationService.UserRelationInfo getUserRelation(UserRelationRequest req, String parentSerialNumber) {
        return userCenterClient.queryUserRelation(req.sn_user_id, parentSerialNumber);
    }

    // 工具方法：台账直接生成分支
    private UserRelationResponse buildLedgerResp(UserRelationRequest req, String serialNumberB, String callSequence, String isMainNumber, String serialNumberA, UuRelationService.UserRelationInfo userRel, String msg) {
        if (userRel == null) return failResp("未找到用户关系信息");
        UserLedger ledger = new UserLedger();
        ledger.order_id = req.order_id;
        ledger.order_line_id = req.order_line_id;
        ledger.relation_type_code = userRel.relation_type_code;
        ledger.user_id_a = userRel.user_id_a;
        ledger.user_id_b = userRel.user_id_b;
        ledger.serial_number_b = serialNumberB;
        ledger.serial_number_a = serialNumberA != null ? serialNumberA : userRel.primary_serial_number;
        ledger.call_sequence = callSequence != null ? callSequence : userRel.call_sequence;
        ledger.is_primary_number = isMainNumber != null ? isMainNumber : userRel.is_main_number;
        ledger.modify_tag = 2;
        ledger.start_date = userRel.start_date;
        ledger.end_date = LocalDateTime.of(2099, 12, 31, 23, 59, 59);

        insertUserLedger(ledger);

        UserRelationResponse resp = new UserRelationResponse();
        resp.success = true;
        resp.message = msg;
        resp.ledger = ledger;
        return resp;
    }

    private String queryOrderLineItemAttr(Long orderId, Long orderLineId, String attrCode) {
        String sql = "select attr_value from oc_order_line_item where order_id = ? and order_line_id = ? and attr_code = ? and modify_tag in ('0','2') limit 1";
        List<String> list = jdbcTemplate.query(sql, new Object[]{orderId, orderLineId, attrCode}, (rs, rn) -> rs.getString("attr_value"));
        return list.isEmpty() ? null : list.get(0);
    }

    private String queryUserDiffCode(String parentSerialNumber) {
        String sql = "select user_diff_code from tf_f_user where parent_serial_number = ? and net_type_code = 'CP' and remove_tag = '0' limit 1";
        List<String> list = jdbcTemplate.query(sql, new Object[]{parentSerialNumber}, (rs, rn) -> rs.getString("user_diff_code"));
        return list.isEmpty() ? null : list.get(0);
    }

    private void insertUserLedger(UserLedger ledger) {
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

    // 台账直接生成分支
    private UserRelationResponse buildLedgerResp(UserRelationRequest req, String serialNumberB, String callSequence, String isMainNumber, String serialNumberA, UuRelationService.UserRelationInfo userRel, UserRelationResponse resp, UserLedger ledger) {
        if (userRel == null) {
            resp.success = false;
            resp.message = "未找到用户关系信息";
            return resp;
        }
        ledger.order_id = req.order_id;
        ledger.order_line_id = req.order_line_id;
        ledger.relation_type_code = userRel.relation_type_code;
        ledger.user_id_a = userRel.user_id_a;
        ledger.user_id_b = userRel.user_id_b;
        ledger.serial_number_b = serialNumberB;
        ledger.serial_number_a = serialNumberA != null ? serialNumberA : userRel.primary_serial_number;
        ledger.call_sequence = callSequence != null ? callSequence : userRel.call_sequence;
        ledger.is_primary_number = isMainNumber != null ? isMainNumber : userRel.is_main_number;
        ledger.modify_tag = 2;
        ledger.start_date = userRel.start_date;
        ledger.end_date = LocalDateTime.of(2099, 12, 31, 23, 59, 59);

        insertUserLedger(ledger);

        resp.success = true;
        resp.message = "台账生成成功";
        resp.ledger = ledger;
        return resp;
    }
}