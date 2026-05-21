package com.admin.controller;

import com.admin.common.Result;
import com.admin.entity.Order;
import com.admin.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/list")
    public Result<List<Order>> list(@RequestParam(required = false) String orderNo,
                                    @RequestParam(required = false) Long memberId,
                                    @RequestParam(required = false) Integer status) {
        List<Order> list = orderService.getList(orderNo, memberId, status);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Order> getById(@PathVariable Long id) {
        Order order = orderService.getById(id);
        return Result.success(order);
    }

    @PutMapping("/{id}/ship")
    public Result<Void> ship(@PathVariable Long id) {
        try {
            orderService.ship(id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        try {
            orderService.cancel(id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/refund")
    public Result<Void> refund(@PathVariable Long id) {
        try {
            orderService.refund(id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
