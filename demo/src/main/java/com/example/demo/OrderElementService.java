package com.example.demo;

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 订单元素台账处理服务
 * 实现资费属性删除、合约期判断、台账生成等核心逻辑
 */
public class OrderElementService {

    // 模拟数据库表
    private List<Map<String, Object>> ocOrderProductElement = new ArrayList<>();
    private List<Map<String, Object>> ocOrderElementItem = new ArrayList<>();
    private List<Map<String, Object>> tfFUserDiscntItem = new ArrayList<>();
    private List<Map<String, Object>> tfFUserOther = new ArrayList<>();
    private List<Map<String, Object>> ocOrderExpansion = new ArrayList<>();
    private List<Map<String, Object>> tfFUserServiceItem = new ArrayList<>();
    private List<Map<String, Object>> tfFUserSvcState = new ArrayList<>();
    private List<Map<String, Object>> ocOrderNumSvcState = new ArrayList<>();

    /**
     * 主处理入口
     */
    public void deleteOrderElementWithContractLogic(String orderId, String orderLineId, String modifyTag, String elementTypeCode, String srd, String userId) {
        List<Map<String, Object>> productElements = queryProductElements(orderId, orderLineId, modifyTag, elementTypeCode);

        for (Map<String, Object> pe : productElements) {
            String elementId = (String) pe.get("element_id");
            String elementItemId = (String) pe.get("element_item_id");
            String elementSubType = (String) pe.get("element_sub_type");
            String endDate = (String) pe.get("end_date");

            boolean hasElementItem = queryElementItem(orderLineId, elementId, elementTypeCode, modifyTag, elementItemId);

            if (!hasElementItem) {
                if ("MRC".equalsIgnoreCase(elementSubType) || "rebate".equalsIgnoreCase(elementSubType)) {
                    String contractEndDateStd = getContractEndDateStd(userId, elementId, elementItemId, endDate);
                    boolean inContract = compareSRDWithContractEnd(srd, contractEndDateStd);

                    if (inContract) {
                        List<Map<String, Object>> discntAttrs = queryDiscntAttrs(userId, elementId, elementItemId, endDate);
                        for (Map<String, Object> attr : discntAttrs) {
                            insertDeleteElementItem(attr, endDate);
                        }
                    } else {
                        List<Map<String, Object>> discntAttrs = queryDiscntAttrs(userId, elementId, elementItemId, endDate);
                        for (Map<String, Object> attr : discntAttrs) {
                            updateProductElementEndDate(orderId, orderLineId, elementTypeCode, elementId, elementItemId, contractEndDateStd);
                            insertDeleteElementItem(attr, contractEndDateStd);
                            if ("MRC".equalsIgnoreCase(elementSubType)) {
                                insertStandardMRCTally(orderId, orderLineId, elementId, elementItemId, contractEndDateStd, endDate);
                            }
                        }
                    }
                } else {
                    List<Map<String, Object>> discntAttrs = queryDiscntAttrs(userId, elementId, elementItemId, endDate);
                    for (Map<String, Object> attr : discntAttrs) {
                        insertDeleteElementItem(attr, endDate);
                    }
                }
            }
        }

        List<Map<String, Object>> expansionAttrs = queryExpansionAttrs(orderId, orderLineId, elementTypeCode, modifyTag);
        for (Map<String, Object> attr : expansionAttrs) {
            String snUserId = userId;
            String attrCode = (String) attr.get("attr_code");
            String attrValue = (String) attr.get("attr_value");
            String startDate = getUserOtherStartDate(snUserId, attrCode, attrValue);
            String expansionEndDate = (String) attr.get("end_date");
            insertOrderExpansion(attrCode, attrValue, "1", startDate, expansionEndDate);
        }
    }

    // 查询 oc_order_product_element
    private List<Map<String, Object>> queryProductElements(String orderId, String orderLineId, String modifyTag, String elementTypeCode) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : ocOrderProductElement) {
            if (Objects.equals(row.get("order_id"), orderId)
                    && Objects.equals(row.get("order_line_id"), orderLineId)
                    && Objects.equals(row.get("modify_tag"), modifyTag)
                    && Objects.equals(row.get("element_type_code"), elementTypeCode)) {
                result.add(row);
            }
        }
        return result;
    }

    // 查询 oc_order_element_item 是否有记录
    private boolean queryElementItem(String orderLineId, String elementId, String elementTypeCode, String modifyTag, String elementItemId) {
        for (Map<String, Object> row : ocOrderElementItem) {
            if (Objects.equals(row.get("order_line_id"), orderLineId)
                    && Objects.equals(row.get("element_id"), elementId)
                    && Objects.equals(row.get("element_type_code"), elementTypeCode)
                    && Objects.equals(row.get("modify_tag"), modifyTag)
                    && Objects.equals(row.get("element_item_id"), elementItemId)) {
                return true;
            }
        }
        return false;
    }

    // 获取合约结束时间
    private String getContractEndDateStd(String userId, String elementId, String elementItemId, String endDate) {
        for (Map<String, Object> row : tfFUserDiscntItem) {
            if (Objects.equals(row.get("user_id"), userId)
                    && Objects.equals(row.get("discnt_code"), elementId)
                    && Objects.equals(row.get("dst_item_id"), elementItemId)
                    && Objects.equals(row.get("attr_code"), "contract_end_date_std")
                    && compareDate((String) row.get("end_date"), endDate) > 0) {
                String val = (String) row.get("attr_value");
                if (val != null && compareDate(val, "2099-12-31T23:59:59") < 0) {
                    return val;
                }
            }
        }
        return "2099-12-31T23:59:59";
    }

    // 日期比较辅助
    private int compareDate(String d1, String d2) {
        try {
            LocalDateTime dt1 = LocalDateTime.parse(d1, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime dt2 = LocalDateTime.parse(d2, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return dt1.compareTo(dt2);
        } catch (Exception e) {
            return 0;
        }
    }

    // 比较 SRD-1天 <= contract_end_date_std
    private boolean compareSRDWithContractEnd(String srd, String contractEndDateStd) {
        try {
            LocalDateTime srdDate = LocalDateTime.parse(srd, DateTimeFormatter.ISO_LOCAL_DATE_TIME).minusDays(1);
            LocalDateTime contractEnd = LocalDateTime.parse(contractEndDateStd, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return !srdDate.isAfter(contractEnd);
        } catch (Exception e) {
            return false;
        }
    }

    // 查询资费属性
    private List<Map<String, Object>> queryDiscntAttrs(String userId, String elementId, String elementItemId, String endDate) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : tfFUserDiscntItem) {
            if (Objects.equals(row.get("user_id"), userId)
                    && Objects.equals(row.get("discnt_code"), elementId)
                    && Objects.equals(row.get("dst_item_id"), elementItemId)
                    && compareDate((String) row.get("end_date"), endDate) > 0) {
                result.add(row);
            }
        }
        return result;
    }

    // 插入删除台账
    private void insertDeleteElementItem(Map<String, Object> attr, String endDate) {
        Map<String, Object> item = new HashMap<>(attr);
        item.put("action", "delete");
        item.put("end_date", endDate);
        ocOrderElementItem.add(item);
    }

    // 修改当前资费台账的结束时间
    private void updateProductElementEndDate(String orderId, String orderLineId, String elementTypeCode, String elementId, String elementItemId, String contractEndDateStd) {
        for (Map<String, Object> row : ocOrderProductElement) {
            if (Objects.equals(row.get("order_id"), orderId)
                    && Objects.equals(row.get("order_line_id"), orderLineId)
                    && Objects.equals(row.get("element_type_code"), elementTypeCode)
                    && Objects.equals(row.get("element_id"), elementId)
                    && Objects.equals(row.get("element_item_id"), elementItemId)) {
                row.put("end_date", contractEndDateStd);
            }
        }
    }

    // 增补标准月租台账及属性
    private void insertStandardMRCTally(String orderId, String orderLineId, String elementId, String elementItemId, String contractEndDateStd, String endDate) {
        Map<String, Object> productElement = new HashMap<>();
        productElement.put("order_id", orderId);
        productElement.put("order_line_id", orderLineId);
        productElement.put("element_id", elementId);
        productElement.put("element_item_id", elementItemId);
        productElement.put("start_date", plusSeconds(contractEndDateStd, 1));
        productElement.put("end_date", endDate);
        productElement.put("modify_tag", "0");
        productElement.put("element_type_code", "D");
        ocOrderProductElement.add(productElement);

        Map<String, Object> elementItem = new HashMap<>();
        elementItem.put("element_item_id", elementItemId);
        elementItem.put("start_date", plusSeconds(contractEndDateStd, 1));
        elementItem.put("end_date", endDate);
        elementItem.put("modify_tag", "0");
        elementItem.put("contract_perior", "FTG");
        elementItem.put("contract_start_date", plusSeconds(contractEndDateStd, 1));
        elementItem.put("contract_end_date", endDate);
        elementItem.put("contract_end_date_std", endDate);
        ocOrderElementItem.add(elementItem);
    }

    // 时间加秒辅助
    private String plusSeconds(String dateTime, int seconds) {
        try {
            LocalDateTime dt = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME).plusSeconds(seconds);
            return dt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return dateTime;
        }
    }

    // 查询 oc_order_element_item 需扩展的属性
    private List<Map<String, Object>> queryExpansionAttrs(String orderId, String orderLineId, String elementTypeCode, String modifyTag) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : ocOrderElementItem) {
            if (Objects.equals(row.get("order_id"), orderId)
                    && Objects.equals(row.get("order_line_id"), orderLineId)
                    && Objects.equals(row.get("element_type_code"), elementTypeCode)
                    && Objects.equals(row.get("modify_tag"), modifyTag)
                    && compareDate((String) row.get("end_date"), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) > 0) {
                result.add(row);
            }
        }
        return result;
    }

    // 查询 tf_F_user_other 获取 start_date
    private String getUserOtherStartDate(String snUserId, String attrCode, String attrValue) {
        for (Map<String, Object> row : tfFUserOther) {
            if (Objects.equals(row.get("user_id"), snUserId)
                    && Objects.equals(row.get("rsrv_value_code"), attrCode)
                    && Objects.equals(row.get("rsrv_value"), attrValue)
                    && compareDate((String) row.get("end_date"), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) > 0) {
                return (String) row.get("start_date");
            }
        }
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // 插入 oc_order_expansion 台账
    private void insertOrderExpansion(String reservedCode, String reservedValue, String modifyTag, String startDate, String endDate) {
        Map<String, Object> expansion = new HashMap<>();
        expansion.put("RESERVED_CODE", reservedCode);
        expansion.put("RESERVED_VALUE", reservedValue);
        expansion.put("modify_tag", modifyTag);
        expansion.put("start_date", startDate);
        expansion.put("end_date", endDate);
        ocOrderExpansion.add(expansion);
    }

    /**
     * 服务类（element_type_code='S'）属性删除与台账处理
     */
    public void deleteOrderServiceElementWithContractLogic(String orderId, String orderLineId, String modifyTag, String elementTypeCode, String userId, String snSerialNumber, String srd) {
        List<Map<String, Object>> productElements = queryProductElements(orderId, orderLineId, modifyTag, elementTypeCode);

        for (Map<String, Object> pe : productElements) {
            String elementId = (String) pe.get("element_id");
            String elementItemId = (String) pe.get("element_item_id");
            String endDate = (String) pe.get("end_date");
            boolean isMainElement = "1".equals(String.valueOf(pe.get("is_main_element")));

            // 1.1 删除服务属性
            boolean hasElementItem = queryElementItem(orderLineId, elementId, elementTypeCode, modifyTag, elementItemId);
            if (!hasElementItem) {
                List<Map<String, Object>> serviceAttrs = queryServiceAttrs(userId, elementId, elementItemId, endDate);
                for (Map<String, Object> attr : serviceAttrs) {
                    insertDeleteServiceElementItem(orderId, orderLineId, attr, elementItemId, endDate);
                }
            }

            // 1.2 主服务状态终止
            if (isMainElement) {
                Map<String, Object> svcState = queryMainServiceState(userId, elementId);
                if (svcState != null) {
                    insertTerminateMainServiceState(orderId, orderLineId, snSerialNumber, userId, elementId, svcState, endDate);
                }
            }

            // 2.2 非主服务状态终止（如IDD服务，参数配置）
            if (isIddService(elementId)) {
                Map<String, Object> svcState = queryServiceState(userId, elementId);
                if (svcState != null) {
                    insertTerminateNumServiceState(orderId, orderLineId, snSerialNumber, userId, elementId, svcState, endDate);
                }
            }
        }

        List<Map<String, Object>> expansionAttrs = queryExpansionAttrs(orderId, orderLineId, elementTypeCode, modifyTag, Arrays.asList("PCFN", "OWDN", "GRUP", "INTC"));
        for (Map<String, Object> attr : expansionAttrs) {
            String attrCode = (String) attr.get("attr_code");
            String attrValue = (String) attr.get("attr_value");
            String startDate = getUserOtherStartDate(userId, attrCode, attrValue);
            String expansionEndDate = (String) attr.get("end_date");
            insertOrderExpansion(attrCode, attrValue, "1", startDate, expansionEndDate);
        }
    }

    // 查询服务属性
    private List<Map<String, Object>> queryServiceAttrs(String userId, String serviceId, String serviceItemId, String endDate) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : tfFUserServiceItem) {
            if (Objects.equals(row.get("user_id"), userId)
                    && Objects.equals(row.get("service_id"), serviceId)
                    && Objects.equals(row.get("service_item_id"), serviceItemId)
                    && compareDate((String) row.get("end_date"), endDate) > 0) {
                result.add(row);
            }
        }
        return result;
    }

    // 插入删除服务属性台账到 oc_order_element_item
    private void insertDeleteServiceElementItem(String orderId, String orderLineId, Map<String, Object> attr, String serviceItemId, String endDate) {
        Map<String, Object> item = new HashMap<>(attr);
        item.put("order_id", orderId);
        item.put("order_line_id", orderLineId);
        item.put("element_item_id", serviceItemId);
        item.put("action", "delete_service");
        item.put("end_date", endDate);
        item.put("modify_tag", "1");
        ocOrderElementItem.add(item);
    }

    // 查询主服务状态
    private Map<String, Object> queryMainServiceState(String userId, String serviceId) {
        for (Map<String, Object> row : tfFUserSvcState) {
            if (Objects.equals(row.get("user_id"), userId)
                    && Objects.equals(row.get("service_id"), serviceId)
                    && Objects.equals(row.get("main_tag"), "1")
                    && compareDate((String) row.get("end_date"), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) > 0) {
                return row;
            }
        }
        return null;
    }

    // 插入终止主服务状态台账
    private void insertTerminateMainServiceState(String orderId, String orderLineId, String snSerialNumber, String userId, String serviceId, Map<String, Object> svcState, String endDate) {
        Map<String, Object> item = new HashMap<>(svcState);
        item.put("order_id", orderId);
        item.put("order_line_id", orderLineId);
        item.put("SERIAL_NUMBER", snSerialNumber);
        item.put("user_id", userId);
        item.put("SERVICE_ID", serviceId);
        item.put("SERVICE_STATE_CODE", svcState.get("service_state_code"));
        item.put("MAIN_TAG", svcState.get("main_tag"));
        item.put("START_DATE", svcState.get("start_date"));
        item.put("end_date", endDate);
        item.put("modify_tag", "1");
        ocOrderElementItem.add(item);
    }

    // 判断是否为IDD服务（参数配置）
    private boolean isIddService(String elementId) {
        return "IDD".equalsIgnoreCase(elementId);
    }

    // 查询服务状态
    private Map<String, Object> queryServiceState(String userId, String serviceId) {
        for (Map<String, Object> row : tfFUserSvcState) {
            if (Objects.equals(row.get("user_id"), userId)
                    && Objects.equals(row.get("service_id"), serviceId)
                    && compareDate((String) row.get("end_date"), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) > 0) {
                return row;
            }
        }
        return null;
    }

    // 插入oc_order_num_svcstate
    private void insertTerminateNumServiceState(String orderId, String orderLineId, String snSerialNumber, String userId, String serviceId, Map<String, Object> svcState, String endDate) {
        Map<String, Object> item = new HashMap<>(svcState);
        item.put("order_id", orderId);
        item.put("order_line_id", orderLineId);
        item.put("SERIAL_NUMBER", snSerialNumber);
        item.put("user_id", userId);
        item.put("SERVICE_ID", serviceId);
        item.put("SERVICE_STATE_CODE", svcState.get("service_state_code"));
        item.put("MAIN_TAG", svcState.get("main_tag"));
        item.put("START_DATE", svcState.get("start_date"));
        item.put("end_date", endDate);
        item.put("modify_tag", "1");
        ocOrderNumSvcState.add(item);
    }

    // 扩展台账相关模拟方法（重载，支持属性过滤）
    private List<Map<String, Object>> queryExpansionAttrs(String orderId, String orderLineId, String elementTypeCode, String modifyTag, List<String> attrCodes) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : ocOrderElementItem) {
            if (Objects.equals(row.get("order_id"), orderId)
                    && Objects.equals(row.get("order_line_id"), orderLineId)
                    && Objects.equals(row.get("element_type_code"), elementTypeCode)
                    && Objects.equals(row.get("modify_tag"), modifyTag)
                    && attrCodes.contains(row.get("attr_code"))
                    && compareDate((String) row.get("end_date"), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) > 0) {
                result.add(row);
            }
        }
        return result;
    }
}