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

    // 全局 order_id 变量
    private AtomicLong sortOrderId = new AtomicLong(0);
    // 存放在途 order 列表
    private List<Long> sortOrderList = new ArrayList<>();
    
    // 模拟的辅助列表
    private List<Long> notProcessSNList = new ArrayList<>(); // 这里存放 userId 以简化匹配
    private List<OcOrderLine> cancelOrderDetailList = new ArrayList<>(); // 需要撤销的订单列表

    private AtomicLong sequenceGenerator = new AtomicLong(5000);

    /**
     * 信控拆机处理逻辑
     * @param inputOrderLineId 输入的订单行ID，用于获取 account_id (serial_number)
     */
    @Transactional
    public void processCreditTerminate(Long inputOrderId, Long inputOrderLineId) {
        // 信控拆机，account_id 为对应订单行的 serial_number
        String accountIdStr = creditTerminateMapper.getSerialNumber(inputOrderId, inputOrderLineId);
        if (accountIdStr == null) {
            throw new RuntimeException("Account ID not found for OrderLineId: " + inputOrderLineId);
        }
        Long accountId = Long.valueOf(accountIdStr);

        // 1. 增加在途单处理逻辑：如果有在途的MACD单要先撤销对应的在途单
        callFunctionService17(accountId);

        // 2. 获取此账户下的所有用户
        List<Long> userIds = creditTerminateMapper.getUserIdsByAccountId(accountId);
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        // 循环补充对应的台账
        for (Long userId : userIds) {
            // 2.a 获取用户基本信息中增加 product_id 返回
            // 模拟调用用户中心服务获取 user info
            TfFUser userInfo = creditTerminateMapper.getUserInfo(userId); 
            String productId = userInfo != null ? userInfo.getProductId() : "DEFAULT_PROD";

            // 通过 product_id 查询产品中心服务查询产品标签 credit_termination_indicato
            String indicator = productCenterClient.getProductTag(productId, "credit_termination_indicato");
            // 如果有此标签，且值为N，则跳过此号码
            if ("N".equals(indicator)) {
                continue;
            }

            // 2.1：通过循环的 user_id 到 notProcessSNList 中过滤是否有对应的记录
            if (notProcessSNList.contains(userId)) {
                continue;
            }

            // 2.2：否则通过 循环中的 user_id 到 cancelOrderDetailList 过滤是否有对应的记录
            List<OcOrderLine> pendingCancelLines = getPendingCancelLinesForUser(userId);
            if (!pendingCancelLines.isEmpty()) {
                for (OcOrderLine pendingLine : pendingCancelLines) {
                    // 2.2.1: 增加 oc_order_line_item 属性
                    OrderLineItem item = new OrderLineItem();
                    item.setOrderId(pendingLine.getOrderId());
                    item.setOrderLineId(pendingLine.getOrderLineId());
                    item.setAttrCode("cancel_for_credit_termination");
                    item.setAttrValue(accountIdStr); // 信控的 account_id
                    item.setStartDate(LocalDateTime.now());
                    item.setEndDate(LocalDateTime.of(2099, 12, 31, 23, 59, 59));
                    
                    creditTerminateMapper.insertOrderLineItem(item);
                }
                // 2.2.2：跳过本次循环，继续下一个用户
                continue;
            }

            // -- 以下为对信控账号下无在途单的用户增加拆机订单信息 --

            // 2.3 增补订单行
            Long newOrderLineId = sequenceGenerator.incrementAndGet();
            OcOrderLine newOrderLine = new OcOrderLine();
            newOrderLine.setOrderId(inputOrderId); // order_id 不变
            newOrderLine.setOrderLineId(newOrderLineId);
            
            // 调用用户中心服务获取对应的 serial_number, net_type_code, cust_id, main_product_id
            if (userInfo == null) {
                 userInfo = creditTerminateMapper.getUserInfo(userId);
            }
            if (userInfo != null) {
                newOrderLine.setSerialNumber(userInfo.getSerialNumber());
                newOrderLine.setUserId(userId);
                newOrderLine.setNetTypeCode(userInfo.getNetTypeCode());
                newOrderLine.setMainProductId(userInfo.getMainProductId());
                // 模拟调产商品中心获取相关的产品信息
                newOrderLine.setMainProductName("Product Name " + userInfo.getMainProductId());
                newOrderLine.setMainProductType("Type A");
                newOrderLine.setProductFamily("Family B");
            }
            
            newOrderLine.setTradeTypeCode("7230");
            newOrderLine.setSceneType("72300");
            // line_level=1 (assuming field exists or mapped appropriately)
            
            // 插入新的 OrderLine (模拟)
            creditTerminateMapper.insertOrderLine(newOrderLine);

            // SRD 取值逻辑
            LocalDate srd = calculateSRD(userId, userInfo != null ? userInfo.getBrandCode() : null);

            // 2.4 增补 Cease Rental Date 属性
            LocalDate ceaseDate = null;

            // 2.4.1：判断此号码用户状态
            // 通过 user_id + main_tag= '1' and SERVICE_STATE_CODE in（4，5） and end_date > sysdate 查询用户中心服务
            TfFUserSvcState svcState = creditTerminateMapper.getUserCeaseSvcState(userId);
            if (svcState != null) {
                ceaseDate = svcState.getStartDate().toLocalDate(); // 取整到天
            } else {
                // 2.4.2：否则查询是否有在途拆机单
                List<OcOrderLine> inFlightLines = creditTerminateMapper.getInFlightCeaseOrderLines(userId);
                if (inFlightLines != null && !inFlightLines.isEmpty()) {
                    OcOrderLine line = inFlightLines.get(0);
                    // 获取对应的 SRD（规整到天）
                    ceaseDate = creditTerminateMapper.getOrderLineSrd(line.getOrderId(), line.getOrderLineId());
                }
            }

            // 2.4.3
            if (ceaseDate == null) {
                // 如果以上两个步骤都不满足
                // 修改 oc_order_line 表的 order_node_state = 04, cancel_tag = 'Z'
                creditTerminateMapper.terminateOrderLine(inputOrderId, newOrderLineId, "当前号码状态不满足信控拆机，此号码拆机异常");
                // 终止本次循环，继续下个用户循环
                continue;
            } else {
                // 否则 在 oc_order_item 表插入属性 Cease Rental Date (注意：题目说是 oc_order_item，但通常属性是挂在 line 上的，这里严格遵照题目)
                // 题目原话: "否则 在oc_order_item表插入属性Cease Rental Date ，attr_value= 步骤2.4.1 或2.4.2 的值"
                // 通常 order_item 是 line 维度的，这里假设 oc_order_item 关联的是 newOrderLineId
                
                OcOrderItem dateItem = new OcOrderItem();
                // 注意：这里可能需要区分是 Global Order Item 还是 Line Item。
                // 题目上下文是在 "针对用户循环" 中，且 2.3 增补了订单行。
                // 2.4.3 说的 "oc_order_item" 可能是指针对该 line 的 item。
                // 若 oc_order_item 表结构包含 order_line_id，则应设置。如果不包含，则是订单级属性?
                // 通常设计中，OcOrderItem 往往指 OrderLineItem。
                // 但 2.2.1 明确说了 "oc_order_line_item"，而 2.4.3 说是 "oc_order_item"。
                // 假设这里是指关联到新生成的 line 上的属性。
                
                // 假设 OcOrderItem 也有 orderLineId 字段，或者使用 OrderLineItem 替代
                OrderLineItem ceaseDateItem = new OrderLineItem();
                ceaseDateItem.setOrderId(inputOrderId);
                ceaseDateItem.setOrderLineId(newOrderLineId);
                ceaseDateItem.setAttrCode("Cease Rental Date");
                ceaseDateItem.setAttrValue(ceaseDate.toString());
                ceaseDateItem.setStartDate(LocalDateTime.now()); // 使用 sysdate
                ceaseDateItem.setEndDate(LocalDateTime.of(2099, 12, 31, 23, 59, 59));
                
                creditTerminateMapper.insertOrderLineItem(ceaseDateItem);
            }

            // 2.5：编排公共构建服务
            buildCommonServices(inputOrderId, newOrderLineId);
        }
    }
    
    // 模拟功能服务 17
    private void callFunctionService17(Long accountId) {
        // 调用功能服务17，撤销对应的在途单
    }
    
    // 辅助方法：获取待撤销的订单行
    private List<OcOrderLine> getPendingCancelLinesForUser(Long userId) {
        List<OcOrderLine> result = new ArrayList<>();
        for (OcOrderLine line : cancelOrderDetailList) {
            if (line.getUserId().equals(userId)) {
                result.add(line);
            }
        }
        return result;
    }
    
    private LocalDate calculateSRD(Long userId, String brandCode) {
        LocalDate srd;
        int daysToAdd = 1; // 默认 T+1
        
        boolean isBBI = "BBI".equals(brandCode);
        boolean isIdap = false;
        
        if (isBBI) {
            daysToAdd = 5;
        } else {
            // 判断是否为 idap 成员
            String relationCode = creditTerminateMapper.getUserRelationTypeCode(userId);
            if ("IDAP".equals(relationCode)) {
                isIdap = true;
                daysToAdd = 5;
            }
        }
        
        // 开始计算 SRD
        srd = LocalDate.now();
        List<LocalDate> holidays = creditTerminateMapper.getHkPublicHolidays();
        
        int addedDays = 0;
        while (addedDays < daysToAdd) {
            srd = srd.plusDays(1);
            if (isWorkingDay(srd, holidays)) {
                addedDays++;
            }
        }
        return srd;
    }
    
    private void buildCommonServices(Long orderId, Long orderLineId) {
        // 2.5：编排公共构建服务 九、十，十一、十二、十三、四
        // 拆机终止产品、付费关系、地址等相关信息
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
