package com.example.demo.otc;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TfFUserMapper {

    @Select("SELECT * FROM tf_f_user WHERE serial_number = #{serialNumber} AND net_type_code = #{netTypeCode} AND remove_tag = #{removeTag}")
    TfFUser selectBySerialAndNetType(@Param("serialNumber") String serialNumber,
                                     @Param("netTypeCode") String netTypeCode,
                                     @Param("removeTag") String removeTag);
}