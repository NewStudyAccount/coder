package com.admin.controller;

import com.admin.common.Result;
import com.admin.entity.Coupon;
import com.admin.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping("/list")
    public Result<List<Coupon>> list(@RequestParam(required = false) String name,
                                     @RequestParam(required = false) Integer type,
                                     @RequestParam(required = false) Integer status) {
        List<Coupon> list = couponService.getList(name, type, status);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Coupon> getById(@PathVariable Long id) {
        Coupon coupon = couponService.getById(id);
        return Result.success(coupon);
    }

    @PostMapping
    public Result<Void> add(@RequestBody Coupon coupon) {
        couponService.add(coupon);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Coupon coupon) {
        couponService.update(coupon);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable Long id) {
        couponService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        couponService.deleteBatch(ids);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        if (status == null) {
            return Result.error("状态不能为空");
        }
        couponService.updateStatus(id, status);
        return Result.success();
    }
}
