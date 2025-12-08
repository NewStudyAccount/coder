package com.example.demo.otc;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 站点群组关系服务
 * 重构说明：
 * 1. 增加类和方法注释
 * 2. 参数判空校验
 * 3. 统一异常处理
 * 4. 提取常量，避免硬编码
 * 5. 精简冗余代码
 */
@Service
public class SiteGroupRelationService {

    private static final String END_DATE_DEFAULT = "2099-12-31 23:59:59";

    /**
     * 修改站点群组关系台账
     */
    public SiteGroupRelationResponse modifySiteGroupRelation(SiteGroupRelationRequest request) {
        SiteGroupRelationResponse response = new SiteGroupRelationResponse();
        if (request == null) {
            response.setSuccess(false);
            response.setMessage("请求参数不能为空");
            response.setUuLedgers(null);
            return response;
        }
        List<SiteGroupRelationResponse.UuLedgerDto> ledgerList = new ArrayList<>();

        try {
            // 1. 查询oc_order_line_item，获取newParentSerialNumber
            String newParentSerialNumber = mockQueryNewParentSerialNumber(request.getOrderId(), request.getOrderLineId());
            if (newParentSerialNumber == null || newParentSerialNumber.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("未找到新群组父号码（newParentSerialNumber），请检查订单行属性。");
                response.setUuLedgers(null);
                return response;
            }

            // 2. 查询tf_f_user，获取新群组user_id
            String newUserId = mockQueryUserIdBySerialNumber(newParentSerialNumber);
            if (newUserId == null || newUserId.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("未找到新群组用户，请检查新父号码。");
                response.setUuLedgers(null);
                return response;
            }

            // 3. 查询tf_f_user_relation，获取当前成员user_relation
            UserRelationInfo relationInfo = mockQueryUserRelation(request.getSnUserId(), newParentSerialNumber);
            if (relationInfo == null) {
                response.setSuccess(false);
                response.setMessage("未找到当前成员的用户关系信息。");
                response.setUuLedgers(null);
                return response;
            }

            // 4. 生成新增uu台账
            SiteGroupRelationResponse.UuLedgerDto addLedger = new SiteGroupRelationResponse.UuLedgerDto();
            addLedger.setOrderId(request.getOrderId());
            addLedger.setOrderLineId(request.getOrderLineId());
            addLedger.setRelationTypeCode(relationInfo.getRelationTypeCode());
            addLedger.setUserIdA(newUserId);
            addLedger.setUserIdB(request.getSnUserId());
            addLedger.setSerialNumberA(newParentSerialNumber);
            addLedger.setSerialNumberB(request.getSerialNumber());
            addLedger.setCallSequence(relationInfo.getCallSequence());
            addLedger.setIsPrimaryNumber(relationInfo.getIsPrimaryNumber());
            addLedger.setRoleCodeA(relationInfo.getRoleCodeA());
            addLedger.setRoleCodeB(relationInfo.getRoleCodeB());
            addLedger.setModifyTag("0");
            addLedger.setStartDate(request.getSrd());
            addLedger.setEndDate(END_DATE_DEFAULT);
            ledgerList.add(addLedger);

            // 5. 生成删除uu台账
            SiteGroupRelationResponse.UuLedgerDto delLedger = new SiteGroupRelationResponse.UuLedgerDto();
            delLedger.setOrderId(request.getOrderId());
            delLedger.setOrderLineId(request.getOrderLineId());
            delLedger.setRelationTypeCode(relationInfo.getRelationTypeCode());
            delLedger.setUserIdA(relationInfo.getPrimaryUserId());
            delLedger.setUserIdB(relationInfo.getMemUserId());
            delLedger.setSerialNumberA(relationInfo.getMemSerialNumber());
            delLedger.setSerialNumberB(relationInfo.getPrimarySerialNumber());
            delLedger.setCallSequence(relationInfo.getCallSequence());
            delLedger.setIsPrimaryNumber(relationInfo.getIsPrimaryNumber());
            delLedger.setRoleCodeA(relationInfo.getRoleCodeA());
            delLedger.setRoleCodeB(relationInfo.getRoleCodeB());
            delLedger.setModifyTag("1");
            delLedger.setStartDate(relationInfo.getStartDate());
            delLedger.setEndDate(calcEndDate(request.getSrd()));
            ledgerList.add(delLedger);

            // 6. 返回
            response.setSuccess(true);
            response.setMessage("台账生成成功");
            response.setUuLedgers(ledgerList);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("系统异常: " + e.getMessage());
            response.setUuLedgers(null);
        }
        return response;
    }

    /**
     * 伪方法：查询oc_order_line_item表
     */
    private String mockQueryNewParentSerialNumber(Long orderId, Long orderLineId) {
        // 实际应通过order_id/order_line_id/attr_code/modify_tag查询
        return "18888888888";
    }

    /**
     * 伪方法：查询tf_f_user表
     */
    private String mockQueryUserIdBySerialNumber(String serialNumber) {
        // 实际应通过serial_number/REMOVE_TAG=0查询
        return "U10001";
    }

    /**
     * 伪方法：查询tf_f_user_relation表
     */
    private UserRelationInfo mockQueryUserRelation(String memUserId, String primarySerialNumber) {
        // 实际应通过mem_user_id/primary_serial_number/start_date>sysdate查询
        UserRelationInfo info = new UserRelationInfo();
        info.setRelationTypeCode("REL01");
        info.setPrimaryUserId("U20001");
        info.setMemUserId(memUserId);
        info.setMemSerialNumber("19999999999");
        info.setPrimarySerialNumber(primarySerialNumber);
        info.setCallSequence("1");
        info.setIsPrimaryNumber("Y");
        info.setRoleCodeA("A");
        info.setRoleCodeB("B");
        info.setStartDate("2025-01-01 00:00:00");
        return info;
    }

    /**
     * 伪方法：计算end_date=SRD-1秒
     */
    private String calcEndDate(String srd) {
        // 实际应做时间减1秒，这里简单处理
        if (srd == null || srd.length() < 19) return srd;
        return srd.substring(0, 10) + " 23:59:58";
    }

    /**
     * 内部类，模拟用户关系信息
     */
    static class UserRelationInfo {
        private String relationTypeCode;
        private String primaryUserId;
        private String memUserId;
        private String memSerialNumber;
        private String primarySerialNumber;
        private String callSequence;
        private String isPrimaryNumber;
        private String roleCodeA;
        private String roleCodeB;
        private String startDate;

        public String getRelationTypeCode() { return relationTypeCode; }
        public void setRelationTypeCode(String relationTypeCode) { this.relationTypeCode = relationTypeCode; }
        public String getPrimaryUserId() { return primaryUserId; }
        public void setPrimaryUserId(String primaryUserId) { this.primaryUserId = primaryUserId; }
        public String getMemUserId() { return memUserId; }
        public void setMemUserId(String memUserId) { this.memUserId = memUserId; }
        public String getMemSerialNumber() { return memSerialNumber; }
        public void setMemSerialNumber(String memSerialNumber) { this.memSerialNumber = memSerialNumber; }
        public String getPrimarySerialNumber() { return primarySerialNumber; }
        public void setPrimarySerialNumber(String primarySerialNumber) { this.primarySerialNumber = primarySerialNumber; }
        public String getCallSequence() { return callSequence; }
        public void setCallSequence(String callSequence) { this.callSequence = callSequence; }
        public String getIsPrimaryNumber() { return isPrimaryNumber; }
        public void setIsPrimaryNumber(String isPrimaryNumber) { this.isPrimaryNumber = isPrimaryNumber; }
        public String getRoleCodeA() { return roleCodeA; }
        public void setRoleCodeA(String roleCodeA) { this.roleCodeA = roleCodeA; }
        public String getRoleCodeB() { return roleCodeB; }
        public void setRoleCodeB(String roleCodeB) { this.roleCodeB = roleCodeB; }
        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
    }
}
