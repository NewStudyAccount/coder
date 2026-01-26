package com.example.demo.otc;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface GroupMemberTerminationMapper {

    // Query Order Line
    List<OcOrderLine> queryOrderLine(@Param("orderId") Long orderId, 
                                     @Param("tradeTypeCode") String tradeTypeCode, 
                                     @Param("lineLevel") Integer lineLevel, 
                                     @Param("parentSerialNumber") String parentSerialNumber);

    OcOrderLine getOrderLine(@Param("orderId") Long orderId, @Param("orderLineId") Long orderLineId);

    // Query OTC Product
    List<OcOrderProduct> queryOrderProduct(@Param("orderId") Long orderId, 
                                           @Param("orderLineId") Long orderLineId);

    // Query Product Items
    List<OcOrderProductItem> queryOrderProductItems(@Param("orderId") Long orderId, 
                                                    @Param("prodItemId") Long prodItemId);

    // Query Elements
    List<OcOrderElement> queryOrderElements(@Param("orderId") Long orderId, 
                                            @Param("orderLineId") Long orderLineId,
                                            @Param("prodItemId") Long prodItemId);

    // Query Element Items
    List<OcOrderElementItem> queryOrderElementItems(@Param("orderId") Long orderId, 
                                                    @Param("elementItemId") Long elementItemId);

    // Query Group Members
    List<TfFUserRelation> queryGroupMembers(@Param("primaryUserId") Long primaryUserId);

    // Check existing member order
    List<OcOrderLine> checkMemberOrder(@Param("orderId") Long orderId, 
                                       @Param("userId") Long userId, 
                                       @Param("tradeTypeCode") String tradeTypeCode,
                                       @Param("parentSerialNumber") String parentSerialNumber);

    // Query User Info
    TfFUser getUserInfo(@Param("userId") Long userId);
    
    // Query User Diff Code
    String getUserDiffCode(@Param("userId") Long userId);
    
    // Query Hunting Users (using rsrv_value logic)
    List<TfFUser> queryHuntingUsers(@Param("userId") Long userId);

    // Insert methods
    void insertOrderLine(OcOrderLine orderLine);
    void insertOrderProduct(OcOrderProduct product);
    void insertOrderProductItem(OcOrderProductItem item);
    void insertOrderElement(OcOrderElement element);
    void insertOrderElementItem(OcOrderElementItem item);

    // For step 3: query group completion order
    List<OcOrderLine> queryGroupCompletionOrder(@Param("orderId") Long orderId, 
                                                @Param("tradeTypeCode") String tradeTypeCode,
                                                @Param("lineLevel") Integer lineLevel,
                                                @Param("netTypeCode") String netTypeCode);
}
