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
    
    // 存放需撤单的详细信息
    private List<CancelOrderDetail> cancelOrderDetailList = new ArrayList<>();
    // 存放不处理订单明细 (这里使用 String 存放 userId 或 serial_number，根据上下文)
    private List<Long> notProcessSNList = new ArrayList<>();
    
    // 存放需处理的在途订单 order_id 归类后列表
    private List<Long> sortOrderList = new ArrayList<>();
    // 全局变量 sortOrder
    private AtomicLong sortOrder = new AtomicLong(0);

    private AtomicLong sequenceGenerator = new AtomicLong(5000);
    
    // 模拟功能服务调用 (步骤 1, 2, 3)
    private void callFunctionService17(Long accountId) {
        cancelOrderDetailList.clear();
        notProcessSNList.clear();
        sortOrderList.clear();
        sortOrder.set(0);

        // 1.0 获取此账户下的所有用户
        List<Long> userIds = creditTerminateMapper.getUserIdsByAccountId(accountId);
        if (userIds != null && !userIds.isEmpty()) {
            for (Long userId : userIds) {
                processInFlightOrderForUser(userId, accountId);
            }
        }
        
        // 对 sortOrderList 中待撤的 order_id 的 oc_order_item 增加属性
        for (Long orderId : sortOrderList) {
             OcOrderItem attrItem = new OcOrderItem();
             attrItem.setOrderId(orderId);
             attrItem.setAttrCode("cancel_for_credit_termination");
             attrItem.setAttrValue(String.valueOf(accountId));
             attrItem.setModifyTag("0"); // 假设
             attrItem.setStartDate(LocalDateTime.now());
             attrItem.setEndDate(LocalDateTime.of(2099, 12, 31, 23, 59, 59));
             creditTerminateMapper.insertOrderItem(attrItem);
        }

        // 2. 按 account_id 判断是否有新开在途单，并处理在途单
        processNewInFlightOrderForAccount(accountId);

        // 3. 在途单撤单
        processCancelOrders();
    }
    
    // 步骤 1：按号码判断是否有在途单,处理在途单
    private void processInFlightOrderForUser(Long userId, Long currentAccountId) {
        // 1.1 在途单判断
        List<OcOrderLine> inFlightLines = creditTerminateMapper.queryOcOrderLineForInFlight(userId);
        
        for (OcOrderLine line : inFlightLines) {
            Long orderId = line.getOrderId();
            Long orderLineId = line.getOrderLineId();
            String tradeTypeCode = line.getTradeTypeCode();
            
            boolean isValid = false;
            boolean shouldSkip = false;

            // 1.1.1 判断此号码对应的 account_id 与 本次信控的 account_id 是否一个 (modify_tag='0')
            int count0 = creditTerminateMapper.countOcOrderPayRelation(orderId, orderLineId, currentAccountId, "0");
            if (count0 > 0) {
                isValid = true;
            } else {
                 // 1.1.2 modify_tag='1'
                 List<String> tradeTypes = creditTerminateMapper.queryOcOrderPayRelationTradeTypes(orderId, orderLineId, currentAccountId, "1");
                 if (tradeTypes != null && !tradeTypes.isEmpty()) {
                     boolean hasNon192 = false;
                     for(String tt : tradeTypes) {
                         if(!"192".equals(tt)) {
                             hasNon192 = true;
                             break;
                         }
                     }
                     if (hasNon192) {
                         // 跳过本次循环，继续 下个用户 -> 这里的逻辑是针对 orderLine 的循环，还是 user 的循环？
                         // 原文： "如果有数据且trade_type_code=！192 则跳过本次循环，继续 下个用户"
                         // 这里的实现是 return 退出当前 user 的处理
                         return; 
                     }
                 } else {
                     // 如果上一步骤无数据 (即没有 modify_tag=1 的记录)
                     // 通过 user_id + end_ date > sysdate 调用用户中心服务（tf_F_payrelation）获取对应的账户
                     List<Long> accountIds = creditTerminateMapper.getAccountIdsByUserId(userId);
                     if (accountIds != null && accountIds.contains(currentAccountId)) {
                         isValid = true;
                     }
                 }
                 
                 // 1.1.2 如果 trade_type_code= ’192‘
                 if ("192".equals(tradeTypeCode)) {
                     // 1.1.2.1 当前在途单的 SRD 是否小于系统时间： SRD < sysdate( 取整到天)
                     LocalDate srdDate = line.getSrd() != null ? line.getSrd().toLocalDate() : null;
                     boolean isSrdBeforeToday = srdDate != null && srdDate.isBefore(LocalDate.now());

                     // 1.1.2.2 判断当前 Noss 状态是否在 Temp Comp，comp
                     int fulfillCount = creditTerminateMapper.countTradeFulfillAction(orderId, orderLineId);
                     boolean isFulfillStateMatch = fulfillCount > 0;
                     
                     if (isSrdBeforeToday || isFulfillStateMatch) {
                         // 压入对应的 user_id 到 notProcessSNList
                         if (!notProcessSNList.contains(userId)) {
                             notProcessSNList.add(userId);
                         }
                         // 跳过本次循环，继续下个用户
                         return;
                     }
                 }
            }
            
            if (isValid) {
                // 1.1.3：更新 1.1 步骤中对应订单行的 cancel_tag =’Z‘
                creditTerminateMapper.updateOcOrderLineCancelTag(orderId, orderLineId, "Z");
                
                // 同时比较 sortOrderId 与 在途单 order_id 是否相同
                if (sortOrder.get() != orderId) {
                    if (!sortOrderList.contains(orderId)) {
                        sortOrderList.add(orderId);
                    }
                    sortOrder.set(orderId);
                }
                
                // 1.1.4: 压入此订单行的相关数据到 cancelOrderDetailList
                CancelOrderDetail detail = new CancelOrderDetail();
                detail.setSerialNumber(line.getSerialNumber());
                detail.setOrderId(orderId);
                detail.setOrderLineId(orderLineId);
                detail.setTradeTypeCode(tradeTypeCode);
                detail.setSrd(line.getSrd());
                cancelOrderDetailList.add(detail);
            }
        }
    }

    // 步骤 2：按 account_id 判断是否有新开在途单，并处理在途单
    private void processNewInFlightOrderForAccount(Long accountId) {
        // 2.1: 获取 account_id 下是否有新开在途单
        List<OcOrderPayRelation> relations = creditTerminateMapper.queryNewInFlightOrders(accountId);
        if (relations != null) {
            for (OcOrderPayRelation rel : relations) {
                // 2.1.1
                List<OcOrderLine> lines = creditTerminateMapper.queryOcOrderLineForNewInFlight(rel.getOrderId(), rel.getOrderLineId());
                for (OcOrderLine line : lines) {
                    // 2.1.2：通过 order_id+order_line_id 更新对应订单行的 cancel_tag =’Z‘
                    creditTerminateMapper.updateOcOrderLineCancelTag(line.getOrderId(), line.getOrderLineId(), "Z");
                    
                    // 2.1.3：比较 sortOrderId 与 在途单 order_id 是否相同
                     if (sortOrder.get() != line.getOrderId()) {
                        if (!sortOrderList.contains(line.getOrderId())) {
                            sortOrderList.add(line.getOrderId());
                        }
                        // 题目未明确是否要更新 sortOrder，但按逻辑应该更新
                        sortOrder.set(line.getOrderId());
                    }
                }
            }
        }
    }

    // 步骤 3：在途单撤单
    private void processCancelOrders() {
        // 循环上一步骤构建的 sortOrderList，按 order_id 的维度循环完成撤单订单下单
        for (Long orderId : sortOrderList) {
            // 3.1: 通过 order_id + line_level in（0，2）+ cancel_tag= ‘0’ 查询 oc_order_line
            List<OcOrderLine> lines = creditTerminateMapper.queryOcOrderLinesForCancel(orderId);
            
            if (lines != null && !lines.isEmpty()) {
                // 拼撤单下单报文完成该客户单的撤单 (模拟调用接口)
                createCancelOrder(orderId, lines);
            }
        }
    }

    private void createCancelOrder(Long originalOrderId, List<OcOrderLine> lines) {
        // 模拟创建撤单
        // ORDER 节点: TRADE_STAFF_ID=system, cancel_tag='3', remark='due to dunning'
        // ORDER_ATTR_INFO: attr_code='prior_order_id', attr_value=originalOrderId
        
        // 针对每个 line
        for (OcOrderLine line : lines) {
            // ORDER_LINE_INFO: trade_type_code='615', scene_type='61500', cancel_tag='0', SRD=sysdate
            // 保持 serial_number, net_type_code, cust_id, sn_user_id, line_level, parent_serial_number 不变
            
            // 实际开发中这里会构建 XML/JSON 并调用外部接口，或者插入数据库
            // 这里仅打印日志或简单插入模拟
            System.out.println("Creating cancel order for original order: " + originalOrderId + ", line: " + line.getOrderLineId());
        }
    }

    /**
     * 信控拆机处理逻辑
     * @param inputOrderLineId 输入的订单行ID，用于获取 account_id (serial_number)
     */
    @Transactional
    public CreditTerminateResult processCreditTerminate(Long inputOrderId, Long inputOrderLineId) {
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
            return new CreditTerminateResult(new ArrayList<>(cancelOrderDetailList), new ArrayList<>(notProcessSNList), "Account has no users.");
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
        
        return new CreditTerminateResult(new ArrayList<>(cancelOrderDetailList), new ArrayList<>(notProcessSNList), "Success");
    }
    
    // 辅助方法：获取待撤销的订单行
    private List<OcOrderLine> getPendingCancelLinesForUser(Long userId) {
        List<OcOrderLine> result = new ArrayList<>();
        
        for (CancelOrderDetail detail : cancelOrderDetailList) {
            // 根据 orderId 和 orderLineId 从数据库重新查询 OcOrderLine
            // 这是一个比较重的操作，但在 Demo 中可以接受
            OcOrderLine line = creditTerminateMapper.queryOcOrderLineById(detail.getOrderId(), detail.getOrderLineId());
            if (line != null && line.getUserId().equals(userId)) {
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
