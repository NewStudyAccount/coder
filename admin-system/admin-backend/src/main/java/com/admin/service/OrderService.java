package com.admin.service;

import com.admin.entity.Order;
import com.admin.entity.OrderItem;
import com.admin.mapper.OrderItemMapper;
import com.admin.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    public List<Order> getList(String orderNo, Long memberId, Integer status) {
        return orderMapper.selectList(orderNo, memberId, status);
    }

    public Order getById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order != null) {
            order.setItems(orderItemMapper.selectByOrderId(id));
        }
        return order;
    }

    @Transactional
    public void ship(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new RuntimeException("只有待发货订单才能发货");
        }
        orderMapper.updateStatus(id, 2);
    }

    @Transactional
    public void cancel(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("只有待付款订单才能取消");
        }
        orderMapper.updateStatus(id, 4);
    }

    @Transactional
    public void refund(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 1 && order.getStatus() != 2 && order.getStatus() != 3) {
            throw new RuntimeException("当前订单状态不允许退款");
        }
        orderMapper.updateStatus(id, 5);
    }
}
