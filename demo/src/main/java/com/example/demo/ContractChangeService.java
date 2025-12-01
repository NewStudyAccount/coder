package com.example.demo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 合约属性变更台账处理服务
 * 实现合约属性变更的全流程台账生成与属性处理
 */
public class ContractChangeService {

    // 模拟数据库表
    private List<Map<String, Object>> ocOrderElementItem = new ArrayList<>();
    private List<Map<String, Object>> ocOrderProductElement = new ArrayList<>();
    private List<Map<String, Object>> ocOrderLine = new ArrayList<>();
    private List<Map<String, Object>> ocOrderLineItem = new ArrayList<>();
    private List<Map<String, Object>> userDiscntList = new ArrayList<>();
    private List<Map<String, Object>> userDiscntAttrList = new ArrayList<>();

    // 配置：合约属性编码
    private static final Set<String> CONTRACT_ATTR_CODES = Set.of(
            "rent_fee", "rebate_fee", "contract_period", "contract_start_date", "contract_end_date", "qty"
    );
    // 配置：无需拷贝的属性
    private static final Set<String> NO_COPY_ATTR_MRC = Set.of(
            "penalty_derate", "ref_sub_comp_id"
    );
    private static final Set<String> NO_COPY_ATTR_REBATE = Set.of(
            "penalty_derate", "ref_sub_comp_id", "qty", "contract_perior", "contract_start_date", "contract_end_date", "contract_end_date_std"
    );

    /**
     * 合约属性变更台账主流程
     */
    public void processContractChange(String orderId, String srd) {
        // 1. 查询合约属性变更台账信息，按order_line_id分组
        Map<String, List<Map<String, Object>>> lineGroup = ocOrderElementItem.stream()
                .filter(item -> orderId.equals(item.get("order_id"))
                        && "2".equals(item.get("modify_tag"))
                        && "D".equals(item.get("element_type_code"))
                        && CONTRACT_ATTR_CODES.contains(item.get("attr_code")))
                .collect(Collectors.groupingBy(item -> (String) item.get("order_line_id")));

        for (String orderLineId : lineGroup.keySet()) {
            List<Map<String, Object>> lineItems = lineGroup.get(orderLineId);

            // 2. 获取用户原合约信息
            Map<String, Object> orderLine = ocOrderLine.stream()
                    .filter(l -> orderId.equals(l.get("order_id")) && orderLineId.equals(l.get("order_line_id")))
                    .findFirst().orElse(null);
            if (orderLine == null) continue;
            String userId = (String) orderLine.get("user_id");
            String acceptDate = (String) orderLine.get("accept_date");

            // 3. 判断合约属性变更生效方式
            String appendContractValue = ocOrderLineItem.stream()
                    .filter(item -> orderLineId.equals(item.get("order_line_id"))
                            && "Append Contract".equals(item.get("attr_code"))
                            && "0".equals(item.get("modify_tag")))
                    .map(item -> (String) item.get("value"))
                    .findFirst().orElse("0");
            boolean isAppend = "1".equals(appendContractValue);

            // 4. 处理资费台账，按element_item_id分组
            Map<String, List<Map<String, Object>>> itemGroup = lineItems.stream()
                    .collect(Collectors.groupingBy(item -> (String) item.get("element_item_id")));
            for (String elementItemId : itemGroup.keySet()) {
                List<Map<String, Object>> itemList = itemGroup.get(elementItemId);
                String elementId = (String) itemList.get(0).get("element_id");

                // 4.0 判断element_id的sub_element_type
                String subElementType = getSubElementType(elementId);

                // 4.1 获取用户原资费、资费属性
                Map<String, Object> userDiscnt = userDiscntList.stream()
                        .filter(d -> userId.equals(d.get("user_id")) && elementId.equals(d.get("element_id")) && elementItemId.equals(d.get("element_item_id")))
                        .findFirst().orElse(null);
                List<Map<String, Object>> userDiscntAttrs = userDiscntAttrList.stream()
                        .filter(a -> userId.equals(a.get("user_id")) && elementItemId.equals(a.get("element_item_id")))
                        .collect(Collectors.toList());

                // 4.2 判断合约内/外
                String contractEndDateStd = userDiscntAttrs.stream()
                        .filter(a -> "contract_end_date_std".equals(a.get("attr_code")))
                        .map(a -> (String) a.get("attr_value"))
                        .findFirst().orElse("2099-12-31T23:59:59");
                boolean isInContract = compareDate(contractEndDateStd, acceptDate) > 0;

                // 4.3 合约内，立即生效，SRD <= contract_end_date_std
                if (isInContract && !isAppend && compareDate(srd, contractEndDateStd) <= 0) {
                    // 4.3.1 终止原资费
                    insertProductElement(orderId, orderLineId, elementId, elementItemId, minusSeconds(srd, 1), "1");
                    // 4.3.2 新增资费
                    String newItemId = genItemId();
                    insertProductElement(orderId, orderLineId, elementId, newItemId, srd, "0");
                    // 4.3.3 终止原资费属性
                    for (Map<String, Object> attr : userDiscntAttrs) {
                        insertElementItem(orderId, orderLineId, elementId, elementItemId, attr, minusSeconds(srd, 1), "1", minusSeconds(srd, 1));
                    }
                    // 4.3.4 新增资费属性
                    for (Map<String, Object> attr : userDiscntAttrs) {
                        insertElementItem(orderId, orderLineId, elementId, newItemId, attr, srd, "0", null);
                    }
                    // 4.3.5 追加未修改属性
                    // 省略，实际应对比属性集合并补充
                }
                // 4.4 合约内，顺延
                else if (isInContract && isAppend) {
                    // 4.4.1 终止原资费
                    insertProductElement(orderId, orderLineId, elementId, elementItemId, contractEndDateStd, "1");
                    // 4.4.2 新增资费
                    String newItemId = genItemId();
                    String newStart = plusSeconds(contractEndDateStd, 1);
                    insertProductElement(orderId, orderLineId, elementId, newItemId, newStart, "0");
                    // 4.4.3 终止原资费属性
                    for (Map<String, Object> attr : userDiscntAttrs) {
                        insertElementItem(orderId, orderLineId, elementId, elementItemId, attr, contractEndDateStd, "1", contractEndDateStd);
                    }
                    // 4.4.4 新增资费属性
                    for (Map<String, Object> attr : userDiscntAttrs) {
                        insertElementItem(orderId, orderLineId, elementId, newItemId, attr, newStart, "0", null);
                    }
                    // 4.4.5 追加未修改属性
                }
                // 4.5 合约外，顺延
                else if (!isInContract && isAppend) {
                    insertProductElement(orderId, orderLineId, elementId, elementItemId, contractEndDateStd, "1");
                    String newItemId = genItemId();
                    String newStart = plusSeconds(contractEndDateStd, 1);
                    insertProductElement(orderId, orderLineId, elementId, newItemId, newStart, "0");
                    for (Map<String, Object> attr : userDiscntAttrs) {
                        insertElementItem(orderId, orderLineId, elementId, elementItemId, attr, contractEndDateStd, "1", contractEndDateStd);
                        insertElementItem(orderId, orderLineId, elementId, newItemId, attr, newStart, "0", null);
                    }
                }
                // 4.6 合约外，SRD > contract_end_date_std，无顺延
                else if (!isInContract && !isAppend && compareDate(srd, contractEndDateStd) > 0) {
                    insertProductElement(orderId, orderLineId, elementId, elementItemId, contractEndDateStd, "1");
                    if (compareDate(minusSeconds(srd, 1), contractEndDateStd) > 0 && "MRC".equals(subElementType)) {
                        String noContractItemId = genItemId();
                        String noContractStart = plusSeconds(contractEndDateStd, 1);
                        String noContractEnd = minusSeconds(srd, 1);
                        insertProductElement(orderId, orderLineId, elementId, noContractItemId, noContractStart, "0", noContractEnd);
                        for (Map<String, Object> attr : userDiscntAttrs) {
                            if (!NO_COPY_ATTR_MRC.contains(attr.get("attr_code"))) {
                                insertElementItem(orderId, orderLineId, elementId, noContractItemId, attr, noContractStart, "0", noContractEnd);
                            }
                        }
                        // 增补contract_perior等属性
                        insertExtraContractAttrs(orderId, orderLineId, elementId, noContractItemId, noContractStart, noContractEnd);
                    }
                    // 新合约
                    String newItemId = genItemId();
                    insertProductElement(orderId, orderLineId, elementId, newItemId, srd, "0");
                    for (Map<String, Object> attr : userDiscntAttrs) {
                        insertElementItem(orderId, orderLineId, elementId, elementItemId, attr, contractEndDateStd, "1", contractEndDateStd);
                        insertElementItem(orderId, orderLineId, elementId, newItemId, attr, srd, "0", null);
                    }
                }
                // 4.7 合约内，非顺延，SRD > contract_end_date_std
                else if (isInContract && !isAppend && compareDate(srd, contractEndDateStd) > 0) {
                    insertProductElement(orderId, orderLineId, elementId, elementItemId, contractEndDateStd, "1");
                    if (compareDate(minusSeconds(srd, 1), contractEndDateStd) > 0 && "MRC".equals(subElementType)) {
                        String noContractItemId = genItemId();
                        String noContractStart = plusSeconds(contractEndDateStd, 1);
                        String noContractEnd = minusSeconds(srd, 1);
                        insertProductElement(orderId, orderLineId, elementId, noContractItemId, noContractStart, "0", noContractEnd);
                        for (Map<String, Object> attr : userDiscntAttrs) {
                            if (!NO_COPY_ATTR_MRC.contains(attr.get("attr_code"))) {
                                insertElementItem(orderId, orderLineId, elementId, noContractItemId, attr, noContractStart, "0", noContractEnd);
                            }
                        }
                        insertExtraContractAttrs(orderId, orderLineId, elementId, noContractItemId, noContractStart, noContractEnd);
                    }
                    String newItemId = genItemId();
                    insertProductElement(orderId, orderLineId, elementId, newItemId, srd, "0");
                    for (Map<String, Object> attr : userDiscntAttrs) {
                        insertElementItem(orderId, orderLineId, elementId, elementItemId, attr, contractEndDateStd, "1", contractEndDateStd);
                        insertElementItem(orderId, orderLineId, elementId, newItemId, attr, srd, "0", null);
                    }
                }
            }
        }
    }

    // 获取资费类型
    private String getSubElementType(String elementId) {
        // 实际应查产品表，这里模拟
        if (elementId != null && elementId.startsWith("MRC")) return "MRC";
        if (elementId != null && elementId.startsWith("REB")) return "rebate";
        return "other";
    }

    /**
     * 只修改MRC时自动补全rebate属性变更台账
     */
    public void supplementRebateChangeForMRC(String orderId, String orderLineId, String userId, String srd) {
        // 1. 查询本次合约属性变更的MRC
        List<Map<String, Object>> mrcItems = ocOrderElementItem.stream()
                .filter(item -> orderId.equals(item.get("order_id"))
                        && orderLineId.equals(item.get("order_line_id"))
                        && "2".equals(item.get("modify_tag"))
                        && "D".equals(item.get("element_type_code"))
                        && CONTRACT_ATTR_CODES.contains(item.get("attr_code")))
                .collect(Collectors.groupingBy(item -> (String) item.get("element_id")))
                .entrySet().stream()
                .filter(e -> "MRC".equals(getSubElementType(e.getKey())))
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toList());

        for (Map<String, Object> mrc : mrcItems) {
            String elementItemId = (String) mrc.get("element_item_id");
            // 1.2 获取MRC对应rebate
            Optional<Map<String, Object>> ref = userDiscntAttrList.stream()
                    .filter(a -> userId.equals(a.get("user_id"))
                            && "ref_sub_comp_id".equals(a.get("attr_code"))
                            && elementItemId.equals(a.get("attr_value"))
                            && compareDate((String) a.getOrDefault("end_date", "2099-12-31T23:59:59"), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) > 0)
                    .findFirst();
            if (ref.isEmpty()) continue;
            String rebateItemId = (String) ref.get().get("dst_item_id");
            // 1.3.1 获取rebate discnt_code
            Optional<Map<String, Object>> rebate = userDiscntList.stream()
                    .filter(d -> userId.equals(d.get("user_id"))
                            && rebateItemId.equals(d.get("element_item_id"))
                            && compareDate((String) d.getOrDefault("end_date", "2099-12-31T23:59:59"), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) > 0)
                    .findFirst();
            if (rebate.isEmpty()) continue;
            String rebateDiscntCode = (String) rebate.get().get("element_id");
            // 1.3.2 判断rebate是否已有属性变更
            boolean hasRebateChange = ocOrderElementItem.stream()
                    .anyMatch(item -> orderId.equals(item.get("order_id"))
                            && orderLineId.equals(item.get("order_line_id"))
                            && rebateDiscntCode.equals(item.get("element_id"))
                            && "D".equals(item.get("element_type_code"))
                            && CONTRACT_ATTR_CODES.contains(item.get("attr_code"))
                            && rebateItemId.equals(item.get("element_item_id"))
                            && "2".equals(item.get("modify_tag"))
                            && compareDate((String) item.getOrDefault("end_date", "2099-12-31T23:59:59"), (String) item.getOrDefault("start_date", "1970-01-01T00:00:00")) > 0);
            if (hasRebateChange) continue;
            // 1.3.3 判断rebate是否被删除
            boolean rebateDeleted = ocOrderProductElement.stream()
                    .anyMatch(pe -> orderId.equals(pe.get("order_id"))
                            && orderLineId.equals(pe.get("order_line_id"))
                            && rebateDiscntCode.equals(pe.get("element_id"))
                            && "D".equals(pe.get("element_type_code"))
                            && "1".equals(pe.get("modify_tag")));
            if (rebateDeleted) continue;
            // 1.3.4 增补rebate属性变更台账
            Optional<Map<String, Object>> rebateFee = userDiscntAttrList.stream()
                    .filter(a -> userId.equals(a.get("user_id"))
                            && rebateItemId.equals(a.get("element_item_id"))
                            && "rebate_fee".equals(a.get("attr_code"))
                            && compareDate((String) a.getOrDefault("end_date", "2099-12-31T23:59:59"), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) > 0)
                    .findFirst();
            if (rebateFee.isPresent()) {
                Map<String, Object> row = new HashMap<>();
                row.put("order_id", orderId);
                row.put("order_line_id", orderLineId);
                row.put("element_id", rebateDiscntCode);
                row.put("element_type_code", "D");
                row.put("element_item_id", rebateItemId);
                row.put("attr_code", "rebate_fee");
                row.put("attr_value", rebateFee.get().get("attr_value"));
                row.put("start_date", srd);
                row.put("end_date", "2099-12-31T23:59:59");
                row.put("modify_tag", "2");
                ocOrderElementItem.add(row);
            }
        }
    }

    /**
     * 只修改rebate时自动补全MRC属性变更台账
     */
    public void supplementMRCChangeForRebate(String orderId, String orderLineId, String userId, String srd) {
        // 1. 查询本次合约属性变更的rebate
        List<Map<String, Object>> rebateItems = ocOrderElementItem.stream()
                .filter(item -> orderId.equals(item.get("order_id"))
                        && orderLineId.equals(item.get("order_line_id"))
                        && "2".equals(item.get("modify_tag"))
                        && "D".equals(item.get("element_type_code"))
                        && CONTRACT_ATTR_CODES.contains(item.get("attr_code")))
                .collect(Collectors.groupingBy(item -> (String) item.get("element_id")))
                .entrySet().stream()
                .filter(e -> "rebate".equals(getSubElementType(e.getKey())))
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toList());

        for (Map<String, Object> rebate : rebateItems) {
            String elementItemId = (String) rebate.get("element_item_id");
            // 1.2 获取rebate对应MRC
            Optional<Map<String, Object>> ref = userDiscntList.stream()
                    .filter(d -> userId.equals(d.get("user_id"))
                            && elementItemId.equals(d.get("dst_item_id"))
                            && "ref_sub_comp_id".equals(d.get("attr_code"))
                            && compareDate((String) d.getOrDefault("end_date", "2099-12-31T23:59:59"), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) > 0)
                    .findFirst();
            if (ref.isEmpty()) continue;
            String mrcItemId = (String) ref.get().get("attr_value");
            // 1.3.1 获取MRC discnt_code
            Optional<Map<String, Object>> mrc = userDiscntList.stream()
                    .filter(d -> userId.equals(d.get("user_id"))
                            && mrcItemId.equals(d.get("element_item_id"))
                            && compareDate((String) d.getOrDefault("end_date", "2099-12-31T23:59:59"), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) > 0)
                    .findFirst();
            if (mrc.isEmpty()) continue;
            String mrcDiscntCode = (String) mrc.get().get("element_id");
            // 1.3.2 判断MRC是否已有属性变更
            boolean hasMRCChange = ocOrderElementItem.stream()
                    .anyMatch(item -> orderId.equals(item.get("order_id"))
                            && orderLineId.equals(item.get("order_line_id"))
                            && mrcDiscntCode.equals(item.get("element_id"))
                            && "D".equals(item.get("element_type_code"))
                            && CONTRACT_ATTR_CODES.contains(item.get("attr_code"))
                            && "2".equals(item.get("modify_tag"))
                            && compareDate((String) item.getOrDefault("end_date", "2099-12-31T23:59:59"), (String) item.getOrDefault("start_date", "1970-01-01T00:00:00")) > 0);
            if (hasMRCChange) continue;
            // 1.3.4 增补MRC属性变更台账
            Optional<Map<String, Object>> rentFee = userDiscntAttrList.stream()
                    .filter(a -> userId.equals(a.get("user_id"))
                            && mrcItemId.equals(a.get("element_item_id"))
                            && "rent_fee".equals(a.get("attr_code"))
                            && compareDate((String) a.getOrDefault("end_date", "2099-12-31T23:59:59"), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) > 0)
                    .findFirst();
            if (rentFee.isPresent()) {
                Map<String, Object> row = new HashMap<>();
                row.put("order_id", orderId);
                row.put("order_line_id", orderLineId);
                row.put("element_id", mrcDiscntCode);
                row.put("element_type_code", "D");
                row.put("element_item_id", mrcItemId);
                row.put("attr_code", "rent_fee");
                row.put("attr_value", rentFee.get().get("attr_value"));
                row.put("start_date", srd);
                row.put("end_date", "2099-12-31T23:59:59");
                row.put("modify_tag", "2");
                ocOrderElementItem.add(row);
            }
        }
    }

    /**
     * 针对DDI line变更时MRC的qty属性台账增补
     */
    public void supplementMRCQtyForDDILine(String orderId, String orderLineId, String snUserId, String srd) {
        // 1. 查询ddi_line_qty
        Optional<Map<String, Object>> ddiLine = ocOrderProductElement.stream()
                .filter(item -> orderId.equals(item.get("order_id"))
                        && orderLineId.equals(item.get("order_line_id"))
                        && "ddi_line_qty".equals(item.get("attr_code"))
                        && "0".equals(item.get("modify_tag")))
                .findFirst();
        if (ddiLine.isEmpty()) return;
        String qtyValue = (String) ddiLine.get().get("attr_value");
        String productId = (String) ddiLine.get().get("product_id");
        String prodItemId = (String) ddiLine.get().get("prod_item_id");

        // 1.1 增补本次有合约属性变更的MRC的qty属性
        Set<String> chgMrcContractSet = new HashSet<>();
        List<Map<String, Object>> mrcChangeList = ocOrderElementItem.stream()
                .filter(item -> orderId.equals(item.get("order_id"))
                        && orderLineId.equals(item.get("order_line_id"))
                        && CONTRACT_ATTR_CODES.contains(item.get("attr_code"))
                        && "2".equals(item.get("modify_tag")))
                .collect(Collectors.toList());
        for (Map<String, Object> mrc : mrcChangeList) {
            String elementId = (String) mrc.get("element_id");
            String elementItemId = (String) mrc.get("element_item_id");
            // 排除非本产品MRC
            boolean valid = userDiscntList.stream()
                    .anyMatch(d -> snUserId.equals(d.get("user_id"))
                            && productId.equals(d.get("product_id"))
                            && prodItemId.equals(d.get("prod_item_id"))
                            && elementId.equals(d.get("discnt_code"))
                            && elementItemId.equals(d.get("dst_item_id"))
                            && compareDate((String) d.getOrDefault("end_date", "2099-12-31T23:59:59"), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) > 0);
            if (!valid) continue;
            // 随机取一条合约属性
            Optional<Map<String, Object>> attr = ocOrderElementItem.stream()
                    .filter(item -> orderId.equals(item.get("order_id"))
                            && orderLineId.equals(item.get("order_line_id"))
                            && elementId.equals(item.get("element_id"))
                            && "D".equals(item.get("element_type_code"))
                            && elementItemId.equals(item.get("element_item_id"))
                            && CONTRACT_ATTR_CODES.contains(item.get("attr_code"))
                            && "2".equals(item.get("modify_tag")))
                    .findFirst();
            if (attr.isPresent()) {
                Map<String, Object> row = new HashMap<>(attr.get());
                row.put("attr_code", "qty");
                row.put("attr_value", qtyValue);
                ocOrderElementItem.add(row);
                chgMrcContractSet.add(elementId + "#" + elementItemId);
            }
        }
        // 1.2 增补本次未变更的MRC的qty
        List<Map<String, Object>> userMRCs = userDiscntList.stream()
                .filter(d -> snUserId.equals(d.get("user_id"))
                        && productId.equals(d.get("product_id"))
                        && prodItemId.equals(d.get("prod_item_id"))
                        && compareDate((String) d.getOrDefault("end_date", "2099-12-31T23:59:59"), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) > 0)
                .collect(Collectors.toList());
        for (Map<String, Object> mrc : userMRCs) {
            String discntCode = (String) mrc.get("discnt_code");
            String dstItemId = (String) mrc.get("dst_item_id");
            if (chgMrcContractSet.contains(discntCode + "#" + dstItemId)) continue;
            // 排除本次订单删除的MRC
            boolean deleted = ocOrderProductElement.stream()
                    .anyMatch(pe -> orderId.equals(pe.get("order_id"))
                            && orderLineId.equals(pe.get("order_line_id"))
                            && discntCode.equals(pe.get("element_id"))
                            && "D".equals(pe.get("element_type_code"))
                            && dstItemId.equals(pe.get("element_item_id"))
                            && "1".equals(pe.get("modify_tag")));
            if (deleted) continue;
            // 插入qty属性
            Map<String, Object> row = new HashMap<>();
            row.put("order_id", orderId);
            row.put("order_line_id", orderLineId);
            row.put("element_id", discntCode);
            row.put("element_type_code", "D");
            row.put("element_item_id", dstItemId);
            row.put("attr_code", "qty");
            row.put("attr_value", qtyValue);
            row.put("start_date", srd);
            row.put("end_date", "2099-12-31T23:59:59");
            row.put("modify_tag", "2");
            ocOrderElementItem.add(row);
        }
    }

    // 插入资费台账
    private void insertProductElement(String orderId, String orderLineId, String elementId, String elementItemId, String date, String modifyTag) {
        insertProductElement(orderId, orderLineId, elementId, elementItemId, date, modifyTag, null);
    }
    private void insertProductElement(String orderId, String orderLineId, String elementId, String elementItemId, String startDate, String modifyTag, String endDate) {
        Map<String, Object> row = new HashMap<>();
        row.put("order_id", orderId);
        row.put("order_line_id", orderLineId);
        row.put("element_id", elementId);
        row.put("element_item_id", elementItemId);
        row.put("start_date", startDate);
        row.put("modify_tag", modifyTag);
        if (endDate != null) row.put("end_date", endDate);
        ocOrderProductElement.add(row);
    }

    // 插入资费属性台账
    private void insertElementItem(String orderId, String orderLineId, String elementId, String elementItemId, Map<String, Object> attr, String date, String modifyTag, String endDate) {
        Map<String, Object> row = new HashMap<>(attr);
        row.put("order_id", orderId);
        row.put("order_line_id", orderLineId);
        row.put("element_id", elementId);
        row.put("element_item_id", elementItemId);
        row.put("start_date", date);
        row.put("modify_tag", modifyTag);
        if (endDate != null) {
            row.put("end_date", endDate);
            if ("contract_end_date".equals(row.get("attr_code"))) {
                row.put("attr_value", endDate);
            }
        }
        ocOrderElementItem.add(row);
    }

    // 增补contract_perior等属性
    private void insertExtraContractAttrs(String orderId, String orderLineId, String elementId, String elementItemId, String startDate, String endDate) {
        List<Map<String, Object>> attrs = List.of(
                Map.of("attr_code", "contract_perior", "attr_value", "FTG"),
                Map.of("attr_code", "contract_start_date", "attr_value", startDate),
                Map.of("attr_code", "contract_end_date", "attr_value", endDate),
                Map.of("attr_code", "contract_end_date_std", "attr_value", endDate)
        );
        for (Map<String, Object> attr : attrs) {
            Map<String, Object> row = new HashMap<>(attr);
            row.put("order_id", orderId);
            row.put("order_line_id", orderLineId);
            row.put("element_id", elementId);
            row.put("element_item_id", elementItemId);
            row.put("start_date", startDate);
            row.put("end_date", endDate);
            row.put("modify_tag", "0");
            ocOrderElementItem.add(row);
        }
    }

    // 时间加/减秒
    private String plusSeconds(String dateTime, int seconds) {
        try {
            LocalDateTime dt = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME).plusSeconds(seconds);
            return dt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return dateTime;
        }
    }
    private String minusSeconds(String dateTime, int seconds) {
        try {
            LocalDateTime dt = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME).minusSeconds(seconds);
            return dt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return dateTime;
        }
    }

    // 时间比较
    private int compareDate(String d1, String d2) {
        try {
            LocalDateTime dt1 = LocalDateTime.parse(d1, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime dt2 = LocalDateTime.parse(d2, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return dt1.compareTo(dt2);
        } catch (Exception e) {
            return 0;
        }
    }

    // 生成唯一item_id（模拟序列服务）
    private String genItemId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}