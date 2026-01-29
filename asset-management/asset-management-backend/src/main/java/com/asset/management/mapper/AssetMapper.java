package com.asset.management.mapper;

import com.asset.management.entity.Asset;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 资产Mapper接口
 */
@Mapper
public interface AssetMapper {

    /**
     * 查询所有资产
     */
    List<Asset> findAll();

    /**
     * 根据ID查询资产
     */
    Asset findById(Long id);

    /**
     * 条件查询资产
     */
    List<Asset> findByCondition(@Param("assetName") String assetName,
                                @Param("categoryId") Long categoryId,
                                @Param("status") String status);

    /**
     * 新增资产
     */
    int insert(Asset asset);

    /**
     * 更新资产
     */
    int update(Asset asset);

    /**
     * 删除资产
     */
    int delete(Long id);

    /**
     * 统计各状态资产数量
     */
    List<Map<String, Object>> countByStatus();

    /**
     * 统计各分类资产数量
     */
    List<Map<String, Object>> countByCategory();
}
