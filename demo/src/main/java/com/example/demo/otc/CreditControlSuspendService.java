package com.example.demo.otc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CreditControlSuspendService {

    @Autowired
    private CreditControlSuspendMapper mapper;

    // Placeholder for "Amendment Order" service
    public void callAmendmentService(Map<String, Object> orderPayload) {
        System.out.println("Calling Amendment Service with payload: " + orderPayload);
    }

    @Transactional
    public void processCreditControlSuspend(Long creditControlAccountId, Long triggerOrderId) {
        Long sortOrderId = 0L;
        Set<Long> sortOrderList = new HashSet<>();

        // Logic 1: Process by Number (User ID)
        List<Long> userIds = getUserIds(creditControlAccountId, triggerOrderId);

        for (Long userId : userIds) {
            // 1.1 In-flight order check
            List<OcOrderLine> inFlightOrders = mapper.selectInFlightOrders(userId);
            
            for (OcOrderLine orderLine : inFlightOrders) {
                if (shouldSkipOrder(orderLine, creditControlAccountId)) {
                    continue;
                }

                // 1.1.4: Update cancel_tag = 'Z'
                mapper.updateOrderLineCancelTag(orderLine.getOrderId(), orderLine.getOrderLineId());
                
                // Compare sortOrderId
                if (!orderLine.getOrderId().equals(sortOrderId)) {
                    sortOrderList.add(orderLine.getOrderId());
                    sortOrderId = orderLine.getOrderId();
                }
            }
        }

        // Logic 2: Process by Account ID (New in-flight orders)
        // 2.1 Get new in-flight orders by account_id
        List<OcOrderPayRelation> accountOrders = mapper.selectOrderPayRelationByAccount(creditControlAccountId);
        for (OcOrderPayRelation rel : accountOrders) {
             // 2.1.1 Check oc_order_line criteria
             List<OcOrderLine> lines = mapper.selectInFlightOrdersByLineId(rel.getOrderId(), rel.getOrderLineId());
             for(OcOrderLine line : lines) {
                 // 2.1.2 Update cancel_tag = 'Z'
                 mapper.updateOrderLineCancelTag(line.getOrderId(), line.getOrderLineId());
                 
                 // 2.1.3 Compare sortOrderId
                 if (!line.getOrderId().equals(sortOrderId)) {
                    sortOrderList.add(line.getOrderId());
                    sortOrderId = line.getOrderId();
                }
             }
        }

        // Logic 3: Amend Orders
        for (Long orderId : sortOrderList) {
            amendOrder(orderId);
        }
    }

    private List<Long> getUserIds(Long accountId, Long orderId) {
        // 1.0 Try to get from OcOrderItem
        List<OcOrderItem> items = mapper.selectOcOrderItemByOrderAndAttr(orderId, "sr_user_list", "1");
        if (!items.isEmpty()) {
             String attrValue = items.get(0).getAttrValue();
             if (attrValue != null && !attrValue.isEmpty()) {
                 try {
                     return Arrays.stream(attrValue.split(","))
                             .map(String::trim)
                             .map(Long::valueOf)
                             .collect(Collectors.toList());
                 } catch (NumberFormatException e) {
                     // Log error or ignore, fallback to pay relation
                 }
             }
        }
        // Fallback to User Center Service (TfFPayRelation)
        return mapper.selectUsersFromPayRelation(accountId);
    }

    private boolean shouldSkipOrder(OcOrderLine orderLine, Long creditControlAccountId) {
        // 1.1.1 SRD check: SRD < sysdate (truncated to day)
        if (orderLine.getSrd() != null && orderLine.getSrd().toLocalDate().isBefore(LocalDate.now())) {
            return true;
        }

        // 1.1.2 Noss Status check: Temp Comp or comp
        List<TfBTradeFulfillAction> actions = mapper.selectFulfillAction(orderLine.getOrderId(), orderLine.getOrderLineId());
        if (!actions.isEmpty()) {
            return true;
        }

        // Account ID Check
        // 1.1.1 (Reused numbering) - Check modify_tag='0'
        List<OcOrderPayRelation> rel0 = mapper.selectOrderPayRelation(orderLine.getOrderId(), orderLine.getOrderLineId(), creditControlAccountId, "0");
        if (!rel0.isEmpty()) {
            return false; // Fits logic, do not skip
        }

        // 1.1.3 - Check modify_tag='1'
        List<OcOrderPayRelation> rel1 = mapper.selectOrderPayRelation(orderLine.getOrderId(), orderLine.getOrderLineId(), creditControlAccountId, "1");
        if (!rel1.isEmpty()) {
            return true; // Skip
        }

        // 1.1.2 (Reused numbering) - Check User Center PayRelation
        List<Long> accounts = mapper.selectPayRelationAccount(orderLine.getSnUserId());
        if (accounts.contains(creditControlAccountId)) {
            return false; // Fits logic, do not skip
        }

        // Default skip if no match found
        return true; 
    }

    private void amendOrder(Long orderId) {
        // 3.1 Get lines with cancel_tag='Z'
        List<OcOrderLine> lines = mapper.selectOrderLinesForAmendment(orderId);
        if (lines.isEmpty()) return;

        Map<String, Object> orderPayload = new HashMap<>();
        Map<String, Object> orderNode = new HashMap<>();
        orderNode.put("TRADE_STAFF_ID", "system");
        orderNode.put("cancel_tag", "4");
        orderNode.put("remark", "due to dunning");
        orderPayload.put("ORDER", orderNode);

        Map<String, Object> orderAttrInfo = new HashMap<>();
        orderAttrInfo.put("attr_code", "origin_order_id");
        orderAttrInfo.put("attr_value", orderId);
        orderPayload.put("ORDER_ATTR_INFO", Collections.singletonList(orderAttrInfo));

        List<Map<String, Object>> lineInfos = new ArrayList<>();
        for (OcOrderLine line : lines) {
            Map<String, Object> lineInfo = new HashMap<>();
            lineInfo.put("serial_number", line.getSerialNumber());
            lineInfo.put("net_type_code", line.getNetTypeCode());
            lineInfo.put("cust_id", line.getCustId());
            lineInfo.put("sn_user_id", line.getSnUserId());
            lineInfo.put("trade_type_code", line.getTradeTypeCode());
            lineInfo.put("scene_type", line.getSceneType());
            lineInfo.put("line_level", line.getLineLevel());
            lineInfo.put("parent_serial_number", line.getParentSerialNumber());
            lineInfo.put("cancel_tag", "5");
            lineInfo.put("SRD", "2099-12-31 23:59:59");
            
            List<Map<String, Object>> lineAttrs = new ArrayList<>();
            Map<String, Object> attr1 = new HashMap<>();
            attr1.put("attr_type", "00");
            attr1.put("attr_code", "credit_hold_remark");
            attr1.put("attr_value", "due to dunning");
            
            Map<String, Object> attr2 = new HashMap<>();
            attr2.put("attr_type", "00");
            attr2.put("attr_code", "prior_order_line_id");
            attr2.put("attr_value", line.getOrderLineId());

            Map<String, Object> attr3 = new HashMap<>();
            attr3.put("attr_type", "00");
            attr3.put("attr_code", "is_allow_amend");
            attr3.put("attr_value", "N");
            
            lineAttrs.add(attr1);
            lineAttrs.add(attr2);
            lineAttrs.add(attr3);
            
            lineInfo.put("ORDER_LINE_ATTR_INFO", lineAttrs);
            lineInfos.add(lineInfo);
        }
        orderPayload.put("ORDER_LINE_INFO", lineInfos);

        callAmendmentService(orderPayload);
    }
}
