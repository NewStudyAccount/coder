package com.example.demo.otc;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GroupMemberTerminationService {

    private final GroupMemberTerminationMapper mapper;

    public GroupMemberTerminationService(GroupMemberTerminationMapper mapper) {
        this.mapper = mapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public String process(Long orderId, String parentSerialNumber, Long groupUserId) {
        // 1. 随机获取一条成员拆机订单行
        List<OcOrderLine> lines = mapper.queryOrderLine(orderId, "192", 1, parentSerialNumber);
        if (lines == null || lines.isEmpty()) {
            throw new RuntimeException("No member termination order line found for orderId: " + orderId);
        }
        
        // 1.1 获取对应的订单行数据 (作为模板)
        OcOrderLine templateLine = lines.get(0);
        Long templateOrderLineId = templateLine.getOrderLineId();

        // 1.2 获取对应DN级的OTC产品
        OcOrderProduct templateProduct = null;
        List<OcOrderProductItem> templateProductItems = new ArrayList<>();
        List<OcOrderElement> templateElements = new ArrayList<>();
        List<OcOrderElementItem> templateElementItems = new ArrayList<>();

        List<OcOrderProduct> products = mapper.queryOrderProduct(orderId, templateOrderLineId);
        for (OcOrderProduct product : products) {
            // 模拟校验是否为DN级别的OTC产品
            if (isDnLevelOtcProduct(product.getProductId())) {
                templateProduct = product;
                // 获取关联的 Items, Elements, ElementItems
                templateProductItems = mapper.queryOrderProductItems(orderId, product.getProdItemId());
                templateElements = mapper.queryOrderElements(orderId, templateOrderLineId, product.getProdItemId());
                for (OcOrderElement ele : templateElements) {
                    List<OcOrderElementItem> items = mapper.queryOrderElementItems(orderId, ele.getElementItemId());
                    templateElementItems.addAll(items);
                }
                break; // 假设只找一个
            }
        }

        // 2. 获取拆机群组下的所有成员
        List<TfFUserRelation> members = mapper.queryGroupMembers(groupUserId);
        
        for (TfFUserRelation member : members) {
            Long memberUserId = member.getUserIdB(); // 假设 user_id_b 是成员 ID
            
            // 2.1 判断群组成员是否有对应的拆机台账
            List<OcOrderLine> existingLines = mapper.checkMemberOrder(orderId, memberUserId, "192", parentSerialNumber);
            if (existingLines != null && !existingLines.isEmpty()) {
                continue; // 跳过
            }

            // 2.2 增补订单行数据
            Long newOrderLineId = generateId();
            OcOrderLine newLine = copyOrderLine(templateLine, newOrderLineId, member);
            mapper.insertOrderLine(newLine);

            // 2.3 增加DN级OTC产品订单数据
            if (templateProduct != null) {
                Long newProdItemId = generateId();
                OcOrderProduct newProduct = copyProduct(templateProduct, newLine.getOrderLineId(), newProdItemId);
                mapper.insertOrderProduct(newProduct);

                // 复制 Product Items
                for (OcOrderProductItem item : templateProductItems) {
                    mapper.insertOrderProductItem(copyProductItem(item, newProdItemId));
                }

                // 复制 Elements and Items
                for (OcOrderElement ele : templateElements) {
                    Long newElementItemId = generateId();
                    mapper.insertOrderElement(copyElement(ele, newLine.getOrderLineId(), newProdItemId, newElementItemId));
                    
                    // Find items belonging to this element (simplified matching by ID logic if strict needed, here assumes bulk copy logic needs refinement if multiple elements exist, but for demo taking all items matching old element ID logic is complex without map. Simplified: Assuming one element for demo or flat structure)
                    // Correct logic: Filter specific items for this element.
                    // Since we collected all element items into one list earlier, we need to filter or query again.
                    // Better approach: Query DB again for specific element template
                    List<OcOrderElementItem> specificItems = mapper.queryOrderElementItems(orderId, ele.getElementItemId());
                    for (OcOrderElementItem item : specificItems) {
                        mapper.insertOrderElementItem(copyElementItem(item, newElementItemId));
                    }
                }
            }

            // 2.4 编排公共构建服务 (Mock)
            mockCommonConstructionServices(newLine);
        }

        // 3. 针对citinet 群组拆机连带终止关联的hunting群组
        // 获取拆机的群组订单行 (CP网)
        List<OcOrderLine> groupLines = mapper.queryGroupCompletionOrder(orderId, "192", 2, "CP");
        for (OcOrderLine groupLine : groupLines) {
             Long snUserId = groupLine.getSnUserId();
             String userDiffCode = mapper.getUserDiffCode(snUserId);
             
             // 判断是否是 citinet 群组 (假设 code 为 'citinet')
             if ("citinet".equalsIgnoreCase(userDiffCode)) {
                 List<TfFUser> huntingUsers = mapper.queryHuntingUsers(snUserId);
                 for (TfFUser huntingUser : huntingUsers) {
                     // 后台循环模拟生成群组的拆机订单
                     createHuntingTerminationOrder(orderId, huntingUser);
                 }
             }
        }

        return "Process completed successfully";
    }

    private boolean isDnLevelOtcProduct(String productId) {
        // Mock check
        return true;
    }

    private Long generateId() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }

    private OcOrderLine copyOrderLine(OcOrderLine template, Long newId, TfFUserRelation member) {
        OcOrderLine newLine = new OcOrderLine();
        newLine.setOrderId(template.getOrderId());
        newLine.setOrderLineId(newId);
        newLine.setSerialNumber(member.getSerialNumberB());
        newLine.setSnUserId(member.getUserIdB());
        newLine.setSnCustId(null); // Explicitly null
        newLine.setTradeTypeCode(template.getTradeTypeCode());
        newLine.setLineLevel(template.getLineLevel());
        newLine.setParentSerialNumber(template.getParentSerialNumber());
        newLine.setSrd(template.getSrd());
        newLine.setNetTypeCode(template.getNetTypeCode());
        newLine.setSceneType(template.getSceneType());
        // Copy other fields as necessary
        return newLine;
    }

    private OcOrderProduct copyProduct(OcOrderProduct template, Long orderLineId, Long prodItemId) {
        OcOrderProduct p = new OcOrderProduct();
        p.setOrderId(template.getOrderId());
        p.setOrderLineId(orderLineId);
        p.setProdItemId(prodItemId);
        p.setProductId(template.getProductId());
        p.setProductTypeCode(template.getProductTypeCode());
        return p;
    }

    private OcOrderProductItem copyProductItem(OcOrderProductItem template, Long prodItemId) {
        OcOrderProductItem i = new OcOrderProductItem();
        i.setOrderId(template.getOrderId());
        i.setProdItemId(prodItemId);
        i.setAttrCode(template.getAttrCode());
        i.setAttrValue(template.getAttrValue());
        return i;
    }

    private OcOrderElement copyElement(OcOrderElement template, Long orderLineId, Long prodItemId, Long elementItemId) {
        OcOrderElement e = new OcOrderElement();
        e.setOrderId(template.getOrderId());
        e.setOrderLineId(orderLineId);
        e.setProdItemId(prodItemId);
        e.setElementItemId(elementItemId);
        e.setElementId(template.getElementId());
        return e;
    }

    private OcOrderElementItem copyElementItem(OcOrderElementItem template, Long elementItemId) {
        OcOrderElementItem i = new OcOrderElementItem();
        i.setOrderId(template.getOrderId());
        i.setElementItemId(elementItemId);
        i.setAttrCode(template.getAttrCode());
        i.setAttrValue(template.getAttrValue());
        return i;
    }

    private void mockCommonConstructionServices(OcOrderLine line) {
        // Mock steps 9, 10, 11, 12, 13, 4
        System.out.println("Executing common construction services for line: " + line.getOrderLineId());
    }

    private void createHuntingTerminationOrder(Long orderId, TfFUser huntingUser) {
        OcOrderLine line = new OcOrderLine();
        line.setOrderId(orderId);
        line.setOrderLineId(generateId());
        line.setSerialNumber(huntingUser.getSerialNumber());
        line.setSnUserId(huntingUser.getUserId());
        line.setTradeTypeCode("192");
        line.setSceneType("19201");
        line.setLineLevel(2);
        line.setSrd(LocalDateTime.now());
        // Insert line
        mapper.insertOrderLine(line);
        
        // Mock adding ORDER_LINE_ATTR_INFO for Cease Rental Date
        System.out.println("Adding Cease Rental Date attr for hunting user: " + huntingUser.getUserId());
    }
}
