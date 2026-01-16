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
            // 1.1 In-flight disconnection order check
            List<OcOrderLine> inFlightOrders = mapper.selectInFlightOrders(userId);

            for (OcOrderLine orderLine : inFlightOrders) {
                // Update cancel_tag = 'Z'
                mapper.updateOrderLineCancelTag(orderLine.getOrderId(), orderLine.getOrderLineId());

                // Compare sortOrderId
                if (!orderLine.getOrderId().equals(sortOrderId)) {
                    sortOrderList.add(orderLine.getOrderId());
                    sortOrderId = orderLine.getOrderId();
                }
            }
        }

        // Logic 2: Cancel In-flight Orders
        for (Long orderId : sortOrderList) {
            cancelOrder(orderId);
        }
    }

    private List<Long> getUserIds(Long accountId, Long orderId) {
        // 1.0 Try to get from OcOrderItem
        List<OcOrderItem> items = mapper.selectOcOrderItemByUserList(orderId);
        if (!items.isEmpty()) {
            String attrValue = items.get(0).getAttrValue();
            if (attrValue != null && !attrValue.isEmpty()) {
                try {
                    return Arrays.stream(attrValue.split(","))
                            .map(String::trim)
                            .map(Long::valueOf)
                            .collect(Collectors.toList());
                } catch (NumberFormatException e) {
                    // Fallback to PayRelation
                }
            }
        }
        // Fallback to User Center Service (TfFPayRelation)
        return mapper.selectUsersFromPayRelation(accountId);
    }

    private void cancelOrder(Long orderId) {
        // 2.1 Get lines
        List<OcOrderLine> lines = mapper.selectOrderLinesForAmendment(orderId);
        if (lines.isEmpty()) return;

        Map<String, Object> orderPayload = new HashMap<>();
        Map<String, Object> orderNode = new HashMap<>();
        orderNode.put("TRADE_STAFF_ID", "system");
        orderNode.put("cancel_tag", "3");
        orderPayload.put("ORDER", orderNode);

        Map<String, Object> orderAttrInfo = new HashMap<>();
        orderAttrInfo.put("attr_code", "prior_order_id");
        orderAttrInfo.put("attr_value", orderId);
        orderPayload.put("ORDER_ATTR_INFO", Collections.singletonList(orderAttrInfo));

        List<Map<String, Object>> lineInfos = new ArrayList<>();
        for (OcOrderLine line : lines) {
            Map<String, Object> lineInfo = new HashMap<>();
            lineInfo.put("serial_number", line.getSerialNumber());
            lineInfo.put("net_type_code", line.getNetTypeCode());
            lineInfo.put("cust_id", line.getCustId());
            lineInfo.put("sn_user_id", line.getSnUserId());
            lineInfo.put("parent_serial_number", line.getParentSerialNumber());
            lineInfo.put("line_level", line.getLineLevel());
            lineInfo.put("trade_type_code", "615");
            lineInfo.put("scene_type", "61500");
            lineInfo.put("cancel_tag", "0");
            lineInfo.put("SRD", LocalDate.now().toString());

            Map<String, Object> attr1 = new HashMap<>();
            attr1.put("attr_type", "00");
            attr1.put("attr_code", "credit_open_remark");
            attr1.put("attr_value", "Cancel the disconnection order for the same number due to credit-controlled activation");

            lineInfo.put("ORDER_LINE_ATTR_INFO", Collections.singletonList(attr1));
            lineInfos.add(lineInfo);
        }
        orderPayload.put("ORDER_LINE_INFO", lineInfos);

        callAmendmentService(orderPayload);
    }
}
