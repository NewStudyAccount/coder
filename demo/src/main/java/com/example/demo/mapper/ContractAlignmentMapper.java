package com.example.demo.mapper;

import com.example.demo.entity.OcOrderProduct;
import com.example.demo.entity.OcOrderProductElement;
import com.example.demo.entity.OcOrderElementItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 合约对齐Mapper接口
 */
@Mapper
public interface ContractAlignmentMapper {

    /**
     * 查询新增产品列表
     */
    List<OcOrderProduct> queryNewProducts(@Param("orderId") String orderId,
                                          @Param("orderLineId") String orderLineId,
                                          @Param("modifyTag") String modifyTag);

    /**
     * 查询主资费元素
     */
    OcOrderProductElement queryMainElement(@Param("orderId") String orderId,
                                           @Param("orderLineId") String orderLineId,
                                           @Param("productId") String productId,
                                           @Param("prodItemId") String prodItemId,
                                           @Param("elementTypeCode") String elementTypeCode,
                                           @Param("modifyTag") String modifyTag,
                                           @Param("isMainElement") String isMainElement);

    /**
     * 查询元素属性
     */
    OcOrderElementItem queryElementItem(@Param("orderId") String orderId,
                                        @Param("orderLineId") String orderLineId,
                                        @Param("elementId") String elementId,
                                        @Param("elementItemId") String elementItemId,
                                        @Param("attrCode") String attrCode,
                                        @Param("modifyTag") String modifyTag);

    /**
     * 查询多个属性
     */
    List<OcOrderElementItem> queryElementItems(@Param("orderId") String orderId,
                                               @Param("orderLineId") String orderLineId,
                                               @Param("elementId") String elementId,
                                               @Param("elementItemId") String elementItemId,
                                               @Param("attrCodes") List<String> attrCodes,
                                               @Param("modifyTag") String modifyTag);

    /**
     * 查询非主资费元素列表
     */
    List<OcOrderProductElement> queryNonMainElements(@Param("orderId") String orderId,
                                                     @Param("orderLineId") String orderLineId,
                                                     @Param("productId") String productId,
                                                     @Param("prodItemId") String prodItemId,
                                                     @Param("elementTypeCode") String elementTypeCode,
                                                     @Param("modifyTag") String modifyTag,
                                                     @Param("isMainElement") String isMainElement);

    /**
     * 插入元素属性
     */
    int insertElementItem(OcOrderElementItem item);

    /**
     * 更新元素属性
     */
    int updateElementItem(@Param("orderId") String orderId,
                         @Param("orderLineId") String orderLineId,
                         @Param("elementId") String elementId,
                         @Param("elementItemId") String elementItemId,
                         @Param("attrCode") String attrCode,
                         @Param("modifyTag") String modifyTag,
                         @Param("attrValue") String attrValue);

    /**
     * 批量插入元素属性
     */
    int batchInsertElementItems(@Param("items") List<OcOrderElementItem> items);
}
