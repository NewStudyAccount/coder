package com.example.demo.otc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SuspendService {

    @Autowired
    private SuspendMapper suspendMapper;

    // 模拟产品中心Client
    @Autowired
    private ProductCenterClient productCenterClient;

    private AtomicLong sequenceGenerator = new AtomicLong(2000);

    @Transactional
    public void processSuspend(Long orderId, Long orderLineId, String tradeTypeCode) {
        // 1. 获取 Account ID (模拟从订单行获取)
        String accountIdStr = suspendMapper.getAccountIdFromOrderLine(orderId, orderLineId);
        if (accountIdStr == null) {
            throw new RuntimeException("Account ID not found for orderLineId: " + orderLineId);
        }
        Long accountId = Long.valueOf(accountIdStr);

        // 2. 获取该账户下的所有用户
        List<TfFPayRelation> payRelations = suspendMapper.getPayRelationsByAccountId(accountId);
        
        if (payRelations == null || payRelations.isEmpty()) {
            // 如果没有用户，直接归档
            suspendMapper.archiveOrder(orderId);
            return;
        }

        boolean anyUserProcessed = false;

        for (TfFPayRelation relation : payRelations) {
            Long userId = relation.getUserId();

            // 1.a 获取用户产品信息并检查 suspend_indicator 标签
            // 模拟调用用户中心获取 main_product_id (此处简化逻辑，假设已有)
            String mainProductId = "PROD_001"; // 示例
            
            // 检查 suspend_indicator
            String suspendIndicator = productCenterClient.getProductTag(mainProductId, "suspend_indicator");
            if ("Y".equals(suspendIndicator)) {
                continue; // 跳过此号码
            }

            // 1.0 检查用户服务状态 (SERVICE_STATE_CODE = 0)
            List<TfFUserSvcState> normalSvcStates = suspendMapper.getUserNormalSvcStates(userId);
            if (normalSvcStates == null || normalSvcStates.isEmpty()) {
                continue; // 本用户不处理
            }

            anyUserProcessed = true;

            // 对应当前订单行的 senceType
            String senceType = "DefaultSence"; // 需根据业务逻辑获取
            
            // 1.a 针对信控停 (trade_type_code = '7220')，判断是否仅有 IDD 账户
            if ("7220".equals(tradeTypeCode)) {
                List<TfFPayRelation> iddRelations = suspendMapper.getPayRelationsByUserAndAccount(userId, accountId, "I");
                if (iddRelations != null && iddRelations.size() == 1) {
                    senceType = "72201";
                }
            }

            // 1.1 增补订单行 (逻辑省略，通常涉及插入 oc_order_line 表)
            Long newOrderLineId = sequenceGenerator.incrementAndGet();
            // TODO: insert into oc_order_line ...

            // 1.2 增补服务状态台账
            processUserSvcState(orderId, newOrderLineId, userId, tradeTypeCode);
        }

        if (!anyUserProcessed) {
            suspendMapper.archiveOrder(orderId);
        }
    }

    private void processUserSvcState(Long orderId, Long orderLineId, Long userId, String tradeTypeCode) {
        // 1.2.1 获取用户当前服务状态
        List<TfFPayRelation> userPayRelations = suspendMapper.getUserPayRelations(userId);
        
        // 检查是否仅有 I (IDD)
        boolean onlyIdd = userPayRelations.stream().allMatch(r -> "I".equals(r.getPriceTag()));
        
        List<TfFUserSvcState> svcStatesToProcess;

        if (onlyIdd) {
            TfFUserSvcState iddSvcState = suspendMapper.getUserSvcStateByServiceId(userId, "IDD_SERVICE_CODE"); // IDD服务编码待定
            svcStatesToProcess = (iddSvcState != null) ? Collections.singletonList(iddSvcState) : Collections.emptyList();
        } else {
            svcStatesToProcess = suspendMapper.getAllUserSvcStates(userId);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.of(2099, 12, 31, 23, 59, 59);

        for (TfFUserSvcState svcState : svcStatesToProcess) {
            if ("0".equals(svcState.getServiceStateCode())) {
                // 1.2.1.1 生成终止当前服务状态台账
                OcOrderNumSvcState stopState = new OcOrderNumSvcState();
                stopState.setOrderId(orderId);
                stopState.setOrderLineId(orderLineId);
                stopState.setUserId(userId);
                stopState.setServiceId(svcState.getServiceId());
                stopState.setServiceStateCode(svcState.getServiceStateCode()); // 原状态
                stopState.setMainTag(svcState.getMainTag());
                stopState.setModifyTag("1");
                stopState.setStartDate(now); // 实际应取原开始时间，这里简化
                stopState.setEndDate(now.minusSeconds(1));
                
                suspendMapper.insertOrderNumSvcState(stopState);

                // 1.2.1.2 生成开机服务状态台账 (新状态)
                String newServiceStateCode = "0";
                if ("126".equals(tradeTypeCode)) {
                    newServiceStateCode = "4";
                } else if ("7220".equals(tradeTypeCode)) {
                    newServiceStateCode = "5";
                }

                OcOrderNumSvcState startState = new OcOrderNumSvcState();
                startState.setOrderId(orderId);
                startState.setOrderLineId(orderLineId);
                startState.setUserId(userId);
                startState.setServiceId(svcState.getServiceId());
                startState.setServiceStateCode(newServiceStateCode);
                startState.setMainTag(svcState.getMainTag());
                startState.setModifyTag("0");
                startState.setStartDate(now);
                startState.setEndDate(endDate);

                suspendMapper.insertOrderNumSvcState(startState);
            }
        }
    }
}
