package com.example.demo.otc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AddHuntingService {

    @Autowired
    private AddHuntingMapper addHuntingMapper;

    @Transactional
    public void processAddHunting(Long orderId) {
        // 1. 检查 is_add_on_hunting
        int count = addHuntingMapper.checkAddOnHunting(orderId);
        if (count == 0) {
            return;
        }

        // 2. 检查并更新 cancel_tag
        String cancelTag = addHuntingMapper.getOrderCancelTag(orderId);
        if (!"4".equals(cancelTag)) {
            addHuntingMapper.updateOrderCancelTag(orderId);
        }

        // 3. 检查并增加 initial_normal_order_id 属性
        String attrValue = String.valueOf(orderId);
        int attrCount = addHuntingMapper.checkInitialNormalOrderId(orderId, attrValue);
        
        if (attrCount == 0) {
            LocalDateTime srd = addHuntingMapper.getSysDate();
            LocalDateTime endDate = LocalDateTime.of(2099, 12, 31, 23, 59, 59);

            OcOrderItem item = new OcOrderItem();
            item.setOrderId(orderId);
            item.setAttrCode("initial_normal_order_id");
            item.setAttrValue(attrValue);
            item.setModifyTag("0");
            item.setStartDate(srd);
            item.setEndDate(endDate);

            addHuntingMapper.insertOrderItem(item);
        }
    }
}
