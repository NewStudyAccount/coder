package com.example.demo.otc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CreditTerminateService {

    @Autowired
    private CreditTerminateMapper creditTerminateMapper;

    @Autowired
    private ProductCenterClient productCenterClient;
    
    // 模拟功能服务调用
    private void callFunctionService17() {
        // 调用撤销在途 MACD 单逻辑
    }

    private AtomicLong sortOrderId = new AtomicLong(0);
    private List<Long> sortOrderList = new ArrayList<>(); // 存放在途 orderId
    private List<String> notProcessSNList = new ArrayList<>(); // 不需要处理的序列号列表
    private List<Long> cancelOrderDetailList = new ArrayList<>(); // 需要撤销的订单列表

    private AtomicLong sequenceGenerator = new AtomicLong(4000);

    @Transactional
    public void processCreditTerminate(Long orderId, Long orderLineId) {
        // 1. 增加在途单处理逻辑
        callFunctionService17();

        // 获取 account_id (这里假设为 order_line 对应的 serial_number)
        String accountIdStr = creditTerminateMapper.getSerialNumber(orderId, orderLineId);
        Long accountId = Long.valueOf(accountIdStr);

        // 2. 获取账户下所有用户
        List<Long> userIds = creditTerminateMapper.getUserIdsByAccountId(accountId);
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        for (Long userId : userIds) {
            // 2.a 检查产品标签 credit_termination_indicato
            String mainProductId = "PROD_XXX"; // 模拟获取
            String indicator = productCenterClient.getProductTag(mainProductId, "credit_termination_indicato");
            if ("N".equals(indicator)) {
                continue;
            }

            // 2.1 检查 notProcessSNList
            if (notProcessSNList.contains(userId)) { // 假设 userId 对应 SN
                continue;
            }

            // 2.2 检查 cancelOrderDetailList
            if (cancelOrderDetailList.contains(userId)) { // 简化逻辑：假设 userId 关联到要撤销的订单
                // 2.2.1 增加 oc_order_line_item 属性
                // 模拟获取关联的 orderId 和 orderLineId
                Long cancelOrderId = 123L; 
                Long cancelOrderLineId = 456L;

                OrderLineItem item = new OrderLineItem();
                item.setOrderId(cancelOrderId);
                item.setOrderLineId(cancelOrderLineId);
                item.setAttrCode("cancel_for_credit_termination");
                item.setAttrValue(accountIdStr);
                item.setStartDate(LocalDateTime.now());
                item.setEndDate(LocalDateTime.of(2099, 12, 31, 23, 59, 59));
                
                creditTerminateMapper.insertOrderLineItem(item);
                continue;
            }

            // 2.3 增补订单行 (模拟)
            Long newOrderLineId = sequenceGenerator.incrementAndGet();
            
            // 计算 SRD
            LocalDate srd = calculateSRD(userId);
            LocalDateTime srdDateTime = srd.atStartOfDay();

            // 2.4 增补 Cease Rental Date 属性
            LocalDate ceaseDate = null;
            
            // 2.4.1 判断此号码用户状态
            TfFUserSvcState ceaseSvcState = creditTerminateMapper.getUserCeaseSvcState(userId);
            if (ceaseSvcState != null) {
                // 这里逻辑稍微调整，取 start_date
                ceaseDate = creditTerminateMapper.getUserCeaseStartDate(userId);
            } else {
                // 2.4.2 查询是否有在途拆机单
                List<OcOrderLine> inFlightLines = creditTerminateMapper.getInFlightCeaseOrderLines(userId);
                if (inFlightLines != null && !inFlightLines.isEmpty()) {
                    OcOrderLine line = inFlightLines.get(0);
                    ceaseDate = creditTerminateMapper.getOrderLineSrd(line.getOrderId(), line.getOrderLineId());
                }
            }

            // 2.4.3
            if (ceaseDate == null) {
                // 不满足信控拆机，终止订单行
                creditTerminateMapper.terminateOrderLine(orderId, newOrderLineId, "当前号码状态不满足信控拆机，此号码拆机异常");
                continue;
            } else {
                // 插入 Cease Rental Date 属性
                OcOrderItem dateItem = new OcOrderItem();
                dateItem.setOrderId(orderId);
                dateItem.setAttrCode("Cease Rental Date");
                dateItem.setAttrValue(ceaseDate.toString());
                dateItem.setModifyTag("0");
                dateItem.setStartDate(srdDateTime);
                dateItem.setEndDate(LocalDateTime.of(2099, 12, 31, 23, 59, 59));
                
                creditTerminateMapper.insertOrderItem(dateItem);
            }

            // 2.5 编排公共构建服务 (模拟)
            // buildCommonServices();
        }
    }

    private LocalDate calculateSRD(Long userId) {
        // 先判断是否为 BBI (模拟)
        String brandCode = "BBI"; // 模拟获取
        int daysToAdd = 1;

        if ("BBI".equals(brandCode)) {
            daysToAdd = 5;
        } else {
            String relationCode = creditTerminateMapper.getUserRelationTypeCode(userId);
            if ("IDAP".equals(relationCode)) {
                daysToAdd = 5;
            }
        }

        LocalDate srd = LocalDate.now();
        List<LocalDate> holidays = creditTerminateMapper.getHkPublicHolidays();
        
        // 简单模拟工作日计算 + 跳过节假日
        for (int i = 0; i < daysToAdd; ) {
            srd = srd.plusDays(1);
            if (isWorkingDay(srd, holidays)) {
                i++;
            }
        }
        return srd;
    }

    private boolean isWorkingDay(LocalDate date, List<LocalDate> holidays) {
        java.time.DayOfWeek day = date.getDayOfWeek();
        if (day == java.time.DayOfWeek.SATURDAY || day == java.time.DayOfWeek.SUNDAY) {
            return false;
        }
        if (holidays != null && holidays.contains(date)) {
            return false;
        }
        return true;
    }
}
