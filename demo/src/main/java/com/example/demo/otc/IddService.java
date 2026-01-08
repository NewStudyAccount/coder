package com.example.demo.otc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class IddService {

    @Autowired
    private IddMapper iddMapper;

    // 模拟序列生成服务
    private AtomicLong sequenceGenerator = new AtomicLong(1000);

    @Transactional
    public void processIdd(Long orderId, Long orderLineId) {
        // 1. 通过 order_id 查询 oc_order_customer 获取对应的 cust_id
        Long custId = iddMapper.getCustIdByOrderId(orderId);
        if (custId == null) {
            throw new RuntimeException("CustId not found for orderId: " + orderId);
        }

        // 2. 查询 oc_order_line_item
        List<OrderLineItem> items = iddMapper.getOrderLineItems(orderId, orderLineId);
        
        if (items != null && !items.isEmpty()) {
            for (OrderLineItem item : items) {
                // 1.1 调用序列生成服务生成 cust_id_a
                Long custIdA = sequenceGenerator.incrementAndGet();

                // 1.2 生成 oc_order_relation_cc 台账
                OcOrderRelationCc relationCc = new OcOrderRelationCc();
                relationCc.setCustIdA(custIdA);
                // cust_id_b 为步骤2生成的（这里理解为 cust_id）
                relationCc.setCustIdB(custId); 
                relationCc.setStartDate(LocalDateTime.now());
                relationCc.setEndDate(LocalDateTime.of(2099, 12, 31, 23, 59, 59));
                relationCc.setModifyTag("0");

                iddMapper.insertRelationCc(relationCc);
            }
        }

        // IDD业务更新 oc_order_line 表的 serial_number、cust_id
        // 1. 调用序列生成服务生成 serial_number (IDD + 8位序列)
        long seq = sequenceGenerator.incrementAndGet();
        String serialNumber = String.format("IDD%08d", seq);

        // 2. 更新 oc_order_line 表
        OcOrderLine orderLine = new OcOrderLine();
        orderLine.setOrderId(orderId);
        orderLine.setOrderLineId(orderLineId);
        orderLine.setSerialNumber(serialNumber);
        orderLine.setSnCustId(custId);

        iddMapper.updateOrderLine(orderLine);
    }
}
