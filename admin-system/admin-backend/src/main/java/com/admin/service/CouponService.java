package com.admin.service;

import com.admin.entity.Coupon;
import com.admin.mapper.CouponMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CouponService {

    @Autowired
    private CouponMapper couponMapper;

    public List<Coupon> getList(String name, Integer type, Integer status) {
        return couponMapper.selectList(name, type, status);
    }

    public Coupon getById(Long id) {
        return couponMapper.selectById(id);
    }

    @Transactional
    public void add(Coupon coupon) {
        coupon.setStatus(coupon.getStatus() != null ? coupon.getStatus() : 1);
        coupon.setRemainCount(coupon.getTotalCount());
        couponMapper.insert(coupon);
    }

    @Transactional
    public void update(Coupon coupon) {
        couponMapper.update(coupon);
    }

    @Transactional
    public void deleteById(Long id) {
        couponMapper.deleteById(id);
    }

    @Transactional
    public void deleteBatch(List<Long> ids) {
        couponMapper.deleteBatch(ids);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        couponMapper.updateStatus(id, status);
    }
}
