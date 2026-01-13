package com.example.demo.otc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChangeGroupService {

    @Autowired
    private ChangeGroupMapper changeGroupMapper;

    @Transactional
    public void processChangeGroup(Long orderId) {
        // 1. 获取群组改号订单
        OcOrderLine groupOrderLine = changeGroupMapper.getChangeGroupOrderLine(orderId);
        if (groupOrderLine == null) {
            // 不满足条件：trade_type_code='279' + sence_type='27900' + net_type_code='CP' + cancel_tag='0'
            return;
        }

        Long groupOrderLineId = groupOrderLine.getOrderLineId();

        // 获取新号码
        String newSerialNumber = changeGroupMapper.getNewSerialNumber(orderId, groupOrderLineId);
        if (newSerialNumber != null && !newSerialNumber.isEmpty()) {
            LocalDateTime srd = changeGroupMapper.getSysDate(); // 步骤1的 SRD
            LocalDateTime endDate = LocalDateTime.of(2099, 12, 31, 23, 59, 59);

            // TODO: 这里假设 groupOrderLine 有 getParentSerialNumber 方法，实际上需要确认 OcOrderLine 定义
             String parentSerialNumber = "123456"; // 模拟，需从 groupOrderLine 获取

            // 1.1 获取关联的成员订单行
            List<OcOrderLine> memberOrderLines = changeGroupMapper.getMemberOrderLines(orderId, parentSerialNumber);

            if (memberOrderLines != null) {
                for (OcOrderLine memberLine : memberOrderLines) {
                    // 1.2 为对应的订单行增加属性
                    OrderLineItem item = new OrderLineItem();
                    item.setOrderId(orderId);
                    item.setOrderLineId(memberLine.getOrderLineId());
                    item.setAttrCode("related_new_grp_serial_number");
                    item.setAttrValue(newSerialNumber);
                    item.setStartDate(srd);
                    item.setEndDate(endDate);
                    item.setModifyTag("0");

                    changeGroupMapper.insertOrderLineItem(item);
                }
            }

            // 2. 增补步骤1的订单行属性
            OrderLineItem groupItem = new OrderLineItem();
            groupItem.setOrderId(orderId);
            groupItem.setOrderLineId(groupOrderLineId);
            groupItem.setAttrCode("related_new_grp_serial_number");
            groupItem.setAttrValue(newSerialNumber);
            groupItem.setStartDate(srd);
            groupItem.setEndDate(endDate);
            groupItem.setModifyTag("0");

            changeGroupMapper.insertOrderLineItem(groupItem);
        }
    }
}
