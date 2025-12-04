package com.example.demo.otc;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 伪实现，实际应调用数据库和用户中心服务
 */
@Service
public class MemberRelationSupplementService {

    public MemberRelationSupplementResponse supplement(MemberRelationSupplementRequest request) {
        MemberRelationSupplementResponse response = new MemberRelationSupplementResponse();
        List<MemberRelationSupplementResponse.InstallLedgerDto> installLedgers = new ArrayList<>();
        List<MemberRelationSupplementResponse.PayRelationLedgerDto> payRelationLedgers = new ArrayList<>();

        // 0. 查询oc_order_line_item，判断是否需要处理装机地址
        boolean keepCurrentAddress = mockQueryKeepCurrentAddress(request.getOrderId(), request.getOrderLineId());
        if (!keepCurrentAddress) {
            // 1. 判断是否有装机地址台账
            boolean hasInstallLedger = mockQueryHasInstallLedger(request.getOrderId(), request.getOrderLineId());
            if (!hasInstallLedger) {
                // 1.1 获取群组用户user_id
                String groupUserId = mockQueryGroupUserId(request.getParentSerialNumber());
                // 1.2 获取群组用户装机地址
                InstallInfo groupInstallInfo = mockQueryInstallInfo(groupUserId);
                // 1.3 生成当前订单的装机地址台账
                if (groupInstallInfo != null) {
                    MemberRelationSupplementResponse.InstallLedgerDto addLedger = new MemberRelationSupplementResponse.InstallLedgerDto();
                    addLedger.setOrderId(request.getOrderId());
                    addLedger.setOrderLineId(request.getOrderLineId());
                    addLedger.setInstallId("I" + System.currentTimeMillis());
                    addLedger.setInstallItemId("II" + System.currentTimeMillis());
                    addLedger.setUserId(groupUserId);
                    addLedger.setAddress(groupInstallInfo.getAddress());
                    addLedger.setModifyTag("0");
                    addLedger.setStartDate(request.getSrd());
                    addLedger.setEndDate("2099-12-31 23:59:59");
                    installLedgers.add(addLedger);
                }
                // 1.4 查询当前号码是否有装机地址信息，生成删除台账
                InstallInfo currentInstallInfo = mockQueryInstallInfo(request.getSnUserId());
                if (currentInstallInfo != null) {
                    MemberRelationSupplementResponse.InstallLedgerDto delLedger = new MemberRelationSupplementResponse.InstallLedgerDto();
                    delLedger.setOrderId(request.getOrderId());
                    delLedger.setOrderLineId(request.getOrderLineId());
                    delLedger.setInstallId("I" + (System.currentTimeMillis() + 1));
                    delLedger.setInstallItemId("II" + (System.currentTimeMillis() + 1));
                    delLedger.setUserId(request.getSnUserId());
                    delLedger.setAddress(currentInstallInfo.getAddress());
                    delLedger.setModifyTag("1");
                    delLedger.setStartDate(currentInstallInfo.getStartDate());
                    delLedger.setEndDate(calcEndDate(request.getSrd()));
                    installLedgers.add(delLedger);
                }
            }
        }

        // 2. 判断是否有付费关系台账
        boolean hasPayRelationLedger = mockQueryHasPayRelationLedger(request.getOrderId(), request.getOrderLineId());
        String groupUserIdCache = null;
        if (!hasPayRelationLedger) {
            // 1.2.1 获取群组用户user_id（如已查过可复用）
            String groupUserId = (installLedgers.size() > 0) ? installLedgers.get(0).getUserId() : mockQueryGroupUserId(request.getParentSerialNumber());
            groupUserIdCache = groupUserId;
            // 1.2.2 获取群组用户的付费关系
            PayRelationInfo groupPayRelation = mockQueryPayRelation(groupUserId);
            // 1.2.3 获取当前号码的付费关系
            PayRelationInfo currentPayRelation = mockQueryPayRelation(request.getSnUserId());
            if (currentPayRelation != null) {
                // 1.2.3.1 比较account_id和charge_category
                if (!groupPayRelation.getAccountId().equals(currentPayRelation.getAccountId())
                        || !groupPayRelation.getChargeCategory().equals(currentPayRelation.getChargeCategory())) {
                    // 1.2.3.1.1 终止当前号码付费关系
                    MemberRelationSupplementResponse.PayRelationLedgerDto delLedger = new MemberRelationSupplementResponse.PayRelationLedgerDto();
                    delLedger.setOrderId(request.getOrderId());
                    delLedger.setOrderLineId(request.getOrderLineId());
                    delLedger.setPayRelationId(currentPayRelation.getPayRelationId());
                    delLedger.setUserId(request.getSnUserId());
                    delLedger.setAccountId(currentPayRelation.getAccountId());
                    delLedger.setChargeCategory(currentPayRelation.getChargeCategory());
                    delLedger.setModifyTag("1");
                    delLedger.setStartDate(currentPayRelation.getStartDate());
                    delLedger.setEndDate(calcEndDate(request.getSrd()));
                    payRelationLedgers.add(delLedger);

                    // 1.2.3.1.2 新增当前号码付费关系
                    MemberRelationSupplementResponse.PayRelationLedgerDto addLedger = new MemberRelationSupplementResponse.PayRelationLedgerDto();
                    addLedger.setOrderId(request.getOrderId());
                    addLedger.setOrderLineId(request.getOrderLineId());
                    addLedger.setPayRelationId("P" + System.currentTimeMillis());
                    addLedger.setUserId(request.getSnUserId());
                    addLedger.setAccountId(groupPayRelation.getAccountId());
                    addLedger.setChargeCategory(groupPayRelation.getChargeCategory());
                    addLedger.setModifyTag("0");
                    addLedger.setStartDate(request.getSrd());
                    addLedger.setEndDate("2099-12-31 23:59:59");
                    payRelationLedgers.add(addLedger);
                }
            } else {
                // 1.2.4 新增当前订单的付费关系台账
                MemberRelationSupplementResponse.PayRelationLedgerDto addLedger = new MemberRelationSupplementResponse.PayRelationLedgerDto();
                addLedger.setOrderId(request.getOrderId());
                addLedger.setOrderLineId(request.getOrderLineId());
                addLedger.setPayRelationId("P" + System.currentTimeMillis());
                addLedger.setUserId(request.getSnUserId());
                addLedger.setAccountId(groupPayRelation.getAccountId());
                addLedger.setChargeCategory(groupPayRelation.getChargeCategory());
                addLedger.setModifyTag("0");
                addLedger.setStartDate(request.getSrd());
                addLedger.setEndDate("2099-12-31 23:59:59");
                payRelationLedgers.add(addLedger);
            }
        }

        response.setSuccess(true);
        response.setMessage("台账生成成功");
        response.setInstallLedgers(installLedgers);
        response.setPayRelationLedgers(payRelationLedgers);
        return response;
    }

    // 伪方法：判断是否需要保留当前装机地址
    private boolean mockQueryKeepCurrentAddress(Long orderId, Long orderLineId) {
        // 实际应通过order_id/order_line_id/attr_code/attr_value查询
        return false;
    }

    // 伪方法：判断是否已有装机地址台账
    private boolean mockQueryHasInstallLedger(Long orderId, Long orderLineId) {
        // 实际应通过order_id/order_line_id/modify_tag=0查询
        return false;
    }

    // 伪方法：获取群组用户user_id
    private String mockQueryGroupUserId(String parentSerialNumber) {
        // 实际应通过parent_serial_number/net_type_code=CP/remove_tag=0查询
        return "U30001";
    }

    // 伪方法：获取装机地址信息
    private InstallInfo mockQueryInstallInfo(String userId) {
        // 实际应通过user_id/attr_sub_type=10/end_date>sysdate查询
        if (userId == null) return null;
        InstallInfo info = new InstallInfo();
        info.setAddress("上海市静安区XX路88号");
        info.setStartDate("2025-01-01 00:00:00");
        return info;
    }

    // 伪方法：判断是否已有付费关系台账
    private boolean mockQueryHasPayRelationLedger(Long orderId, Long orderLineId) {
        // 实际应通过order_id/order_line_id/modify_tag=0查询
        return false;
    }

    // 伪方法：获取付费关系信息
    private PayRelationInfo mockQueryPayRelation(String userId) {
        // 实际应通过user_id/end_date>sysdate查询
        if (userId == null) return null;
        PayRelationInfo info = new PayRelationInfo();
        info.setPayRelationId("PR" + userId);
        info.setAccountId("A" + userId);
        info.setChargeCategory("C" + userId);
        info.setStartDate("2025-01-01 00:00:00");
        return info;
    }

    // 伪方法：计算end_date=SRD-1秒
    private String calcEndDate(String srd) {
        if (srd == null || srd.length() < 19) return srd;
        return srd.substring(0, 10) + " 23:59:58";
    }

    // 内部类，模拟装机地址信息
    static class InstallInfo {
        private String address;
        private String startDate;
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
    }

    // 内部类，模拟付费关系信息
    static class PayRelationInfo {
        private String payRelationId;
        private String accountId;
        private String chargeCategory;
        private String startDate;
        public String getPayRelationId() { return payRelationId; }
        public void setPayRelationId(String payRelationId) { this.payRelationId = payRelationId; }
        public String getAccountId() { return accountId; }
        public void setAccountId(String accountId) { this.accountId = accountId; }
        public String getChargeCategory() { return chargeCategory; }
        public void setChargeCategory(String chargeCategory) { this.chargeCategory = chargeCategory; }
        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
    }
}