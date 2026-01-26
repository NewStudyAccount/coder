package com.example.demo.otc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountStatusService {

    private static final Logger logger = LoggerFactory.getLogger(AccountStatusService.class);
    private final AccountStatusMapper accountStatusMapper;

    public AccountStatusService(AccountStatusMapper accountStatusMapper) {
        this.accountStatusMapper = accountStatusMapper;
    }

    /**
     * 每天凌晨执行一次
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduleTask() {
        logger.info("Starting scheduled account status check...");
        processAccountStatus();
        logger.info("Scheduled account status check completed.");
    }

    /**
     * 处理逻辑
     * @return 执行结果摘要
     */
    @Transactional(rollbackFor = Exception.class)
    public String processAccountStatus() {
        logger.info("Fetching active accounts...");
        // 1. 获取全量状态正常的账户
        List<TfFAccount> activeAccounts = accountStatusMapper.queryAccountsByStatus(1); // 1 = Active
        int processedCount = 0;
        int updatedCount = 0;

        for (TfFAccount account : activeAccounts) {
            processedCount++;
            Long accountId = account.getAccountId();

            // 1.1.1 查询此账户下是否有正常用户
            int activeUserCount = accountStatusMapper.countActiveUsersByAccount(accountId);
            if (activeUserCount > 0) {
                // 有效用户，跳过
                continue;
            }

            // 1.2.1 查询此账户下是否有在途订单
            boolean hasInflightOrders = false;
            List<OcOrderPayRelation> relations = accountStatusMapper.queryOrderPayRelations(accountId);
            if (relations != null && !relations.isEmpty()) {
                for (OcOrderPayRelation relation : relations) {
                    int validOrderLineCount = accountStatusMapper.countActiveOrderLines(relation.getOrderId(), relation.getOrderLineId());
                    if (validOrderLineCount > 0) {
                        hasInflightOrders = true;
                        break; // 发现有效订单，停止检查
                    }
                }
            }

            // 如果既无有效用户，也无在途订单
            if (!hasInflightOrders) {
                logger.info("Freezing account: {}", accountId);
                // 调用BRM接口修改账户状态为 Final (Mock)
                callBrmInterface(accountId);
                
                // 修改本地记录
                accountStatusMapper.updateAccountStatus(accountId, 2); // 2 = Final/Frozen
                updatedCount++;
            }
        }

        String result = String.format("Processed %d accounts, froze %d accounts.", processedCount, updatedCount);
        logger.info(result);
        return result;
    }

    private void callBrmInterface(Long accountId) {
        // Mock BRM call
        logger.info("Mock BRM call: Account {} status set to Final", accountId);
    }
}
