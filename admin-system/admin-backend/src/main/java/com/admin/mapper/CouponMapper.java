package com.admin.mapper;

import com.admin.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CouponMapper {

    List<Coupon> selectList(@Param("name") String name, @Param("type") Integer type,
                            @Param("status") Integer status);

    Coupon selectById(Long id);

    int insert(Coupon coupon);

    int update(Coupon coupon);

    int deleteById(Long id);

    int deleteBatch(List<Long> ids);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
