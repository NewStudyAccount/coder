package com.example.demo.otc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class IddSuspendService {

    @Autowired
    private IddSuspendMapper iddSuspendMapper;

    @Autowired
    private ProductCenterClient productCenterClient;

    private AtomicLong sequenceGenerator = new AtomicLong(3000);

    @Transactional
    public void processIddSuspend(Long orderId, Long orderLineId, String tradeTypeCode) {
        List<Long> userIds = new ArrayList<>();
        
        // 1. 尝试从 oc_order_item 获取 sr_user_list
        String srUserList = iddSuspendMapper.getSrUserList(orderId);
        
        if (srUserList != null && !srUserList.trim().isEmpty()) {
            userIds = Arrays.stream(srUserList.split(","))
                            .map(String::trim)
                            .map(Long::valueOf)
                            .collect(Collectors.toList());
        } else {
            // 获取 account_id (这里逻辑是 serial_number 作为 account_id)
            String accountIdStr = iddSuspendMapper.getSerialNumberAsAccountId(orderId, orderLineId);
            if (accountIdStr == null) {
                throw new RuntimeException("Account ID not found for orderLineId: " + orderLineId);
            }
            Long accountId = Long.valueOf(accountIdStr);
            userIds = iddSuspendMapper.getUserIdsByAccountId(accountId);
        }

        if (userIds == null || userIds.isEmpty()) {
            iddSuspendMapper.archiveOrder(orderId);
            return;
        }

        boolean anyUserProcessed = false;
        String iddServiceId = "IDD_SERVICE_CODE"; // 待定

        for (Long userId : userIds) {
            // 1.a 检查产品 suspend_indicator
            String mainProductId = "PROD_001"; // 模拟获取
            String suspendIndicator = productCenterClient.getProductTag(mainProductId, "suspend_indicator");
            if ("N".equals(suspendIndicator)) {
                continue; // 跳过
            }

            // 1.0 检查 IDD 服务状态
            TfFUserSvcState iddSvcState = iddSuspendMapper.getUserSvcState(userId, iddServiceId);
            if (iddSvcState == null || !"0".equals(iddSvcState.getServiceStateCode())) {
                continue; // 不处理
            }

            anyUserProcessed = true;

            // 1.1 增补订单行 (模拟)
            Long newOrderLineId = sequenceGenerator.incrementAndGet();

            // 1.2 增补服务状态台账
            // 1.2.1 再次确认状态 (逻辑复用)
            // 1.2.1.1 生成终止当前服务状态台账
            LocalDateTime now = LocalDateTime.now();
            OcOrderNumSvcState stopState = new OcOrderNumSvcState();
            stopState.setOrderId(orderId);
            stopState.setOrderLineId(newOrderLineId);
            stopState.setUserId(userId);
            stopState.setServiceId(iddServiceId);
            stopState.setServiceStateCode(iddSvcState.getServiceStateCode());
            stopState.setMainTag(iddSvcState.getMainTag());
            stopState.setModifyTag("1");
            stopState.setStartDate(now);
            stopState.setEndDate(now.minusSeconds(1));
            
            iddSuspendMapper.insertOrderNumSvcState(stopState);

            // 1.2.1.2 生成开机服务状态台账
            String newServiceStateCode = "0";
            if ("126".equals(tradeTypeCode)) {
                newServiceStateCode = "4";
            } else if ("7220".equals(tradeTypeCode)) {
                newServiceStateCode = "5";
            }

            OcOrderNumSvcState startState = new OcOrderNumSvcState();
            startState.setOrderId(orderId);
            startState.setOrderLineId(newOrderLineId);
            startState.setUserId(userId);
            startState.setServiceId(iddServiceId);
            startState.setServiceStateCode(newServiceStateCode);
            startState.setMainTag(iddSvcState.getMainTag());
            startState.setModifyTag("0");
            startState.setStartDate(now);
            startState.setEndDate(LocalDateTime.of(2099, 12, 31, 23, 59, 59));

            iddSuspendMapper.insertOrderNumSvcState(startState);
        }

        if (!anyUserProcessed) {
            iddSuspendMapper.archiveOrder(orderId);
        }
    }
}
