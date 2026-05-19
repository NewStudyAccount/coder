package com.admin.mapper;

import com.admin.entity.OssFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OssFileMapper {

    List<OssFile> selectList(@Param("originalName") String originalName);

    OssFile selectById(Long id);

    int insert(OssFile ossFile);

    int deleteById(Long id);
}
