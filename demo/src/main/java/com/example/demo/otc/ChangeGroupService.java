package com.example.demo.otc;

import com.example.demo.SnowflakeIdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * change group 处理逻辑（Mock版）
 * 处理群组用户过户（101）、移机（3410）连带增补成员过户、移机订单信息。
 * 未知值使用 default，占位编排步骤以注释与文本输出。
 *
 * 核心流程（简化与Mock）：
 * 1) 过滤产品类型：根据 oc_param_config(PARAM_CODE='NO_DEAL_PRODUCT_TYPE_CODE') 与 trade_type_code=101/3410 决定是否跳过
 * 2) 基于入参的 order_id + order_line_id + modify_tag='0' 查询安装/关系/付费/行属性（Mock集合代替）
 * 2.1) 获取群组订单产品与OTC属性（Mock产品中心判断为订单级OTC或DN级OTC），收集产品/元素/属性到列表；isAddOrderLevelOTC=false
 * 3) 根据订单行 SN_USER_ID 调用户中心（Mock）获取所有成员关系（mem_user_id、mem_serial_number、is_main_number、relation_type_code），逐一增补：
 *    - 0) 拉取 tf_F_user（Mock）信息（net_type_code、product_id、cust_id、remove_tag），校验无效则跳过
 *    - 检查是否存在 termination/leave group（Mock：根据 scene_type/line_level/cancel_tag 条件从 oc_order_line 集合查重），有则跳过
 *    - 1) 生成序列 order_line_id（Snowflake）
 *    - 2) 增补每个DN的 oc_order_line（复制基线行，替换关键信息）
 *    - trade_type_code=101：
 *      * 复制并插入 oc_order_relation_other、oc_order_payrelation、oc_order_line_item（替换 order_line_id、user_id、serial_number）
 *      * 若 relation_type_code=huning（简化为 "huning" 字符串）：
 *        - 有 DN级OTC：复制 DN级 OTC 产品与元素台账（oc_order_product / element / item），替换 order_line_id
 *        - 若 is_main_number in (1,2)：有 订单级OTC：复制订单级OTC产品与元素台账，替换 order_line_id、prod_item_id、element_item_id
 *      * 否则（非huning）：
 *        - 有 DN级OTC：复制 DN级OTC台账
 *        - 若 isAddOrderLevelOTC == false：随机一个成员复制订单级OTC台账，并设置 isAddOrderLevelOTC = true
 *    - trade_type_code=3410：
 *      * 复制并插入 oc_order_install（modify_tag=0）、oc_order_install_item（modify_tag=0）
 *      * 复制并插入 oc_order_line_item
 *      * 按 trade_type_code=101 逻辑补充 OTC 费用订单
 * 4) 作废步骤2.1收集的原OTC台账记录：将 end_date = start_date - 1 秒（Mock字段替换）
 *
 * 输出：执行结果、创建的行/台账记录以及编排步骤说明
 */
@Service
public class ChangeGroupService {

    // === Mock 配置参数 ===
    private final Set<String> noDealProductTypeCodeFor101 = new HashSet<>(Arrays.asList("EC", "OneComm", "Trunk")); // 仅示例
    private final Set<String> noDealProductTypeCodeCommon = new HashSet<>(Arrays.asList("citinet", "default")); // 适用于 101/3410
    private final Map<String, Set<String>> ocParamConfig = new HashMap<>(); // PARAM_CODE=NO_DEAL_PRODUCT_TYPE_CODE -> set

    // === Mock 台账与订单集合（作为数据库的替代） ===
    private final List<OcOrderLine> ocOrderLines = Collections.synchronizedList(new ArrayList<>());
    private final List<OcOrderInstall> ocOrderInstalls = Collections.synchronizedList(new ArrayList<>());
    private final List<OcOrderInstallItem> ocOrderInstallItems = Collections.synchronizedList(new ArrayList<>());
    private final List<OcOrderRelationOther> ocOrderRelationOthers = Collections.synchronizedList(new ArrayList<>());
    private final List<OcOrderPayRelation> ocOrderPayRelations = Collections.synchronizedList(new ArrayList<>());
    private final List<OcOrderLineItem> ocOrderLineItems = Collections.synchronizedList(new ArrayList<>());

    private final List<OcOrderProduct> ocOrderProducts = Collections.synchronizedList(new ArrayList<>());
    private final List<OcOrderProductElement> ocOrderProductElements = Collections.synchronizedList(new ArrayList<>());
    private final List<OcOrderElementItem> ocOrderElementItems = Collections.synchronizedList(new ArrayList<>());

    public ChangeGroupService() {
        // 初始化配置
        ocParamConfig.put("NO_DEAL_PRODUCT_TYPE_CODE", new HashSet<>(Arrays.asList("citinet", "EC", "OneComm", "Trunk", "default")));

        // 初始化一条群组订单基线行
        ocOrderLines.add(OcOrderLine.builder()
                .orderId(9001L)
                .orderLineId(8001L)
                .tradeTypeCode("101") // 过户
                .sceneType("19200")
                .cancelTag("0")
                .lineLevel(2)
                .snUserId("U-GROUP-9001")
                .serialNumber("13900139000")
                .productTypeCode("default") // 用于过滤
                .netTypeCode("10")
                .userId("U-GROUP-001")
                .custId("CUST-001")
                .mainProductId("PROD-9001")
                .mainProductName("Group Main Product")
                .mainProductType("BUNDLE")
                .productFamily("GROUP")
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(30))
                .extraAttrs(new HashMap<>())
                .build());

        // 初始化安装/关系/付费/属性记录（modify_tag=0）
        ocOrderInstalls.add(new OcOrderInstall(9001L, 8001L, "0", "default-install-attr"));
        ocOrderInstallItems.add(new OcOrderInstallItem(9001L, 8001L, "0", "default-install-item-attr"));
        ocOrderRelationOthers.add(new OcOrderRelationOther(9001L, 8001L, "default-user", "default-serial", "default-rel-attr"));
        ocOrderPayRelations.add(new OcOrderPayRelation(9001L, 8001L, "default-user", "default-serial", "default-pay-attr"));
        ocOrderLineItems.add(new OcOrderLineItem(9001L, 8001L, "default-attr-code", "default-attr-value"));

        // 初始化产品与OTC（订单级、DN级各一）
        ocOrderProducts.add(new OcOrderProduct(9001L, "ORD-OTC-PROD-1", "ORDER")); // 订单级OTC
        ocOrderProducts.add(new OcOrderProduct(9001L, "DN-OTC-PROD-1", "DN")); // DN级OTC
        ocOrderProductElements.add(new OcOrderProductElement(9001L, "ORD-OTC-PROD-1", "ORD-ELEM-1", "ORD-PROD-ITEM-1", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(30)));
        ocOrderProductElements.add(new OcOrderProductElement(9001L, "DN-OTC-PROD-1", "DN-ELEM-1", "DN-PROD-ITEM-1", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(30)));
        ocOrderElementItems.add(new OcOrderElementItem(9001L, "ORD-ELEM-1", "ORD-ELEM-ITEM-1", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(30)));
        ocOrderElementItems.add(new OcOrderElementItem(9001L, "DN-ELEM-1", "DN-ELEM-ITEM-1", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(30)));
    }

    /**
     * 执行主流程
     */
    public ChangeGroupResponse execute(ChangeGroupRequest req) {
        ChangeGroupResponse resp = new ChangeGroupResponse();
        resp.success = false;
        resp.message = "default";
        resp.createdOrderLines = new ArrayList<>();
        resp.createdInstalls = new ArrayList<>();
        resp.createdInstallItems = new ArrayList<>();
        resp.createdRelationOthers = new ArrayList<>();
        resp.createdPayRelations = new ArrayList<>();
        resp.createdLineItems = new ArrayList<>();
        resp.createdProducts = new ArrayList<>();
        resp.createdProductElements = new ArrayList<>();
        resp.createdElementItems = new ArrayList<>();
        resp.steps = new ArrayList<>();

        // 1) 过滤产品类型 + 业务类型配置
        // 使用基线行作为参考（从集合中查找 order_id/order_line_id）
        Optional<OcOrderLine> baseLineOpt = ocOrderLines.stream()
                .filter(l -> Objects.equals(l.getOrderId(), req.orderId))
                .filter(l -> Objects.equals(l.getOrderLineId(), req.orderLineId))
                .findFirst();

        if (baseLineOpt.isEmpty()) {
            resp.message = "未找到基线订单行";
            return resp;
        }
        OcOrderLine baseLine = baseLineOpt.get();

        // 根据配置跳过
        Set<String> skipTypes = ocParamConfig.getOrDefault("NO_DEAL_PRODUCT_TYPE_CODE", Collections.emptySet());
        if (skipTypes.contains(baseLine.getProductTypeCode())) {
            resp.message = "产品类型在 NO_DEAL_PRODUCT_TYPE_CODE 中，跳过处理";
            resp.success = true;
            return resp;
        }
        if ("101".equals(baseLine.getTradeTypeCode()) && noDealProductTypeCodeFor101.contains(baseLine.getProductTypeCode())) {
            resp.message = "业务类型101 + 产品类型过滤，跳过处理";
            resp.success = true;
            return resp;
        }

        // 2) 查询安装/关系/付费/属性（modify_tag='0'）
        List<OcOrderInstall> qInstalls = ocOrderInstalls.stream()
                .filter(i -> Objects.equals(i.orderId, req.orderId) && Objects.equals(i.orderLineId, req.orderLineId) && "0".equals(i.modifyTag))
                .collect(Collectors.toList());
        List<OcOrderInstallItem> qInstallItems = ocOrderInstallItems.stream()
                .filter(i -> Objects.equals(i.orderId, req.orderId) && Objects.equals(i.orderLineId, req.orderLineId) && "0".equals(i.modifyTag))
                .collect(Collectors.toList());
        List<OcOrderRelationOther> qRelationOthers = ocOrderRelationOthers.stream()
                .filter(i -> Objects.equals(i.orderId, req.orderId) && Objects.equals(i.orderLineId, req.orderLineId))
                .collect(Collectors.toList());
        List<OcOrderPayRelation> qPayRelations = ocOrderPayRelations.stream()
                .filter(i -> Objects.equals(i.orderId, req.orderId) && Objects.equals(i.orderLineId, req.orderLineId))
                .collect(Collectors.toList());
        List<OcOrderLineItem> qLineItems = ocOrderLineItems.stream()
                .filter(i -> Objects.equals(i.orderId, req.orderId) && Objects.equals(i.orderLineId, req.orderLineId))
                .collect(Collectors.toList());

        // 2.1) 获取群组订单的产品/资费/资费属性（Mock产品中心判断OTC类型）
        List<OcOrderProduct> qProducts = ocOrderProducts.stream()
                .filter(p -> Objects.equals(p.orderId, req.orderId))
                .collect(Collectors.toList());
        List<OcOrderProduct> orderLevelOtcProds = qProducts.stream().filter(p -> "ORDER".equalsIgnoreCase(p.otcLevel)).collect(Collectors.toList());
        List<OcOrderProduct> dnLevelOtcProds = qProducts.stream().filter(p -> "DN".equalsIgnoreCase(p.otcLevel)).collect(Collectors.toList());
        List<OcOrderProductElement> dnElems = ocOrderProductElements.stream()
                .filter(e -> Objects.equals(e.orderId, req.orderId) && dnLevelOtcProds.stream().anyMatch(p -> p.productId.equals(e.productId)))
                .collect(Collectors.toList());
        List<OcOrderProductElement> orderElems = ocOrderProductElements.stream()
                .filter(e -> Objects.equals(e.orderId, req.orderId) && orderLevelOtcProds.stream().anyMatch(p -> p.productId.equals(e.productId)))
                .collect(Collectors.toList());
        List<OcOrderElementItem> dnElemItems = ocOrderElementItems.stream()
                .filter(it -> dnElems.stream().anyMatch(e -> e.elementId.equals(it.elementId)))
                .collect(Collectors.toList());
        List<OcOrderElementItem> orderElemItems = ocOrderElementItems.stream()
                .filter(it -> orderElems.stream().anyMatch(e -> e.elementId.equals(it.elementId)))
                .collect(Collectors.toList());

        boolean isAddOrderLevelOTC = false;

        // 3) 获取成员（Mock用户中心：根据 SN_USER_ID 返回三个成员）
        List<MemberRel> members = mockQueryMembers(baseLine.getSnUserId());

        Random random = new Random();
        for (MemberRel mem : members) {
            // 0) 查询 tf_F_user（Mock）
            TfFUserInfo tfUser = mockTfFUser(mem.memUserId);
            if (tfUser.removeTag != 0 || tfUser.netTypeCode == null) {
                resp.steps.add("成员 " + mem.memUserId + " 无效或已移除，跳过");
                continue;
            }

            // 检查重复/终止单（Mock）
            boolean exists = ocOrderLines.stream().anyMatch(l ->
                    Objects.equals(l.getOrderId(), req.orderId)
                            && Objects.equals(l.getSnUserId(), mem.memUserId)
                            && "0".equals(l.getCancelTag())
                            && Arrays.asList("19200", "19201", "34002", "34100", "34101").contains(l.getSceneType())
                            && Objects.equals(l.getLineLevel(), 1)
            );
            if (exists) {
                resp.steps.add("成员 " + mem.memUserId + " 已存在终止/离群相关订单，跳过");
                continue;
            }

            // 1) 生成序列
            long newLineId = SnowflakeIdGenerator.nextId();

            // 2) 增补每个DN的 oc_order_line
            OcOrderLine newLine = OcOrderLine.builder()
                    .orderId(req.orderId)
                    .orderLineId(newLineId)
                    .tradeTypeCode(baseLine.getTradeTypeCode())
                    .sceneType(baseLine.getSceneType())
                    .cancelTag("0")
                    .lineLevel(1)
                    .snUserId(mem.memUserId)
                    .serialNumber(mem.memSerialNumber)
                    .productTypeCode(baseLine.getProductTypeCode())
                    .netTypeCode(tfUser.netTypeCode)
                    .userId(tfUser.userId)
                    .custId(tfUser.custId)
                    .mainProductId(tfUser.productId)
                    .mainProductName("default-main-product-name")
                    .mainProductType("default-main-product-type")
                    .productFamily("default-product-family")
                    .startDate(LocalDateTime.now())
                    .endDate(LocalDateTime.now().plusDays(30))
                    .extraAttrs(new HashMap<>())
                    .build();
            ocOrderLines.add(newLine);
            resp.createdOrderLines.add(newLine);

            if ("101".equals(baseLine.getTradeTypeCode())) {
                // 过户：复制关系/付费/属性
                for (OcOrderRelationOther r : qRelationOthers) {
                    OcOrderRelationOther nr = new OcOrderRelationOther(req.orderId, newLineId, tfUser.userId, mem.memSerialNumber, r.attr);
                    ocOrderRelationOthers.add(nr);
                    resp.createdRelationOthers.add(nr);
                }
                for (OcOrderPayRelation p : qPayRelations) {
                    OcOrderPayRelation np = new OcOrderPayRelation(req.orderId, newLineId, tfUser.userId, mem.memSerialNumber, p.attr);
                    ocOrderPayRelations.add(np);
                    resp.createdPayRelations.add(np);
                }
                for (OcOrderLineItem li : qLineItems) {
                    OcOrderLineItem nli = new OcOrderLineItem(req.orderId, newLineId, li.attrCode, li.attrValue);
                    ocOrderLineItems.add(nli);
                    resp.createdLineItems.add(nli);
                }

                // hunting
                if ("huning".equalsIgnoreCase(mem.relationTypeCode)) {
                    // DN级OTC
                    if (!dnElems.isEmpty()) {
                        copyDnLevelOtc(req.orderId, newLineId, dnElems, dnElemItems, resp);
                    }
                    // 主号：订单级OTC
                    if (mem.isMainNumber == 1 || mem.isMainNumber == 2) {
                        if (!orderElems.isEmpty()) {
                            copyOrderLevelOtc(req.orderId, newLineId, orderElems, orderElemItems, resp);
                        }
                    }
                } else {
                    // 非huning
                    if (!dnElems.isEmpty()) {
                        copyDnLevelOtc(req.orderId, newLineId, dnElems, dnElemItems, resp);
                    }
                    if (!orderElems.isEmpty() && !isAddOrderLevelOTC) {
                        copyOrderLevelOtc(req.orderId, newLineId, orderElems, orderElemItems, resp);
                        isAddOrderLevelOTC = true;
                    }
                }

            } else if ("3410".equals(baseLine.getTradeTypeCode())) {
                // 移机：复制安装与属性
                for (OcOrderInstall i : qInstalls) {
                    OcOrderInstall ni = new OcOrderInstall(req.orderId, newLineId, "0", i.attr);
                    ocOrderInstalls.add(ni);
                    resp.createdInstalls.add(ni);
                }
                for (OcOrderInstallItem ii : qInstallItems) {
                    OcOrderInstallItem nii = new OcOrderInstallItem(req.orderId, newLineId, "0", ii.attr);
                    ocOrderInstallItems.add(nii);
                    resp.createdInstallItems.add(nii);
                }
                for (OcOrderLineItem li : qLineItems) {
                    OcOrderLineItem nli = new OcOrderLineItem(req.orderId, newLineId, li.attrCode, li.attrValue);
                    ocOrderLineItems.add(nli);
                    resp.createdLineItems.add(nli);
                }
                // OTC费用同 101
                if (!dnElems.isEmpty()) {
                    copyDnLevelOtc(req.orderId, newLineId, dnElems, dnElemItems, resp);
                }
                if (!orderElems.isEmpty() && !isAddOrderLevelOTC) {
                    copyOrderLevelOtc(req.orderId, newLineId, orderElems, orderElemItems, resp);
                    isAddOrderLevelOTC = true;
                }
            }
        }

        // 4) 作废原OTC台账：end_date = start_date - 1s
        for (OcOrderProductElement e : ocOrderProductElements) {
            if (Objects.equals(e.orderId, req.orderId)) {
                e.endDate = e.startDate.minusSeconds(1);
            }
        }
        for (OcOrderElementItem it : ocOrderElementItems) {
            it.endDate = it.startDate.minusSeconds(1);
        }
        resp.steps.add("作废原OTC台账：end_date = start_date - 1 秒");

        resp.success = true;
        resp.message = "处理完成";
        return resp;
    }

    private void copyDnLevelOtc(Long orderId, Long newLineId,
                                List<OcOrderProductElement> dnElems,
                                List<OcOrderElementItem> dnElemItems,
                                ChangeGroupResponse resp) {
        for (OcOrderProductElement e : dnElems) {
            OcOrderProductElement ne = new OcOrderProductElement(orderId, e.productId, e.elementId, e.prodItemId, LocalDateTime.now(), LocalDateTime.now().plusDays(30));
            // 关联到新行（此处仅记录文本，真实应有行级关联字段）
            resp.createdProductElements.add(ne);
        }
        for (OcOrderElementItem it : dnElemItems) {
            OcOrderElementItem nit = new OcOrderElementItem(orderId, it.elementId, it.elementItemId, LocalDateTime.now(), LocalDateTime.now().plusDays(30));
            resp.createdElementItems.add(nit);
        }
        resp.steps.add("复制 DN级OTC 产品与元素到新行 " + newLineId);
    }

    private void copyOrderLevelOtc(Long orderId, Long newLineId,
                                   List<OcOrderProductElement> orderElems,
                                   List<OcOrderElementItem> orderElemItems,
                                   ChangeGroupResponse resp) {
        for (OcOrderProductElement e : orderElems) {
            OcOrderProductElement ne = new OcOrderProductElement(orderId, e.productId, e.elementId, e.prodItemId, LocalDateTime.now(), LocalDateTime.now().plusDays(30));
            resp.createdProductElements.add(ne);
        }
        for (OcOrderElementItem it : orderElemItems) {
            OcOrderElementItem nit = new OcOrderElementItem(orderId, it.elementId, it.elementItemId, LocalDateTime.now(), LocalDateTime.now().plusDays(30));
            resp.createdElementItems.add(nit);
        }
        resp.steps.add("复制 订单级OTC 产品与元素到新行 " + newLineId);
    }

    // === Mock 用户中心/成员关系/用户信息 ===

    private List<MemberRel> mockQueryMembers(String snUserId) {
        List<MemberRel> list = new ArrayList<>();
        list.add(new MemberRel("MEM-U-001", "13900139001", 1, "huning"));
        list.add(new MemberRel("MEM-U-002", "13900139002", 0, "normal"));
        list.add(new MemberRel("MEM-U-003", "13900139003", 2, "normal"));
        return list;
    }

    private TfFUserInfo mockTfFUser(String memUserId) {
        TfFUserInfo info = new TfFUserInfo();
        info.userId = memUserId;
        info.netTypeCode = "30";
        info.productId = "PROD-" + memUserId.substring(Math.max(0, memUserId.length() - 3));
        info.custId = "C-" + memUserId.substring(Math.max(0, memUserId.length() - 3));
        info.removeTag = 0;
        return info;
    }

    // === DTOs 与表结构（简化） ===

    public static class ChangeGroupRequest {
        public Long orderId;
        public Long orderLineId;
    }

    public static class ChangeGroupResponse {
        public boolean success;
        public String message;
        public List<OcOrderLine> createdOrderLines;
        public List<OcOrderInstall> createdInstalls;
        public List<OcOrderInstallItem> createdInstallItems;
        public List<OcOrderRelationOther> createdRelationOthers;
        public List<OcOrderPayRelation> createdPayRelations;
        public List<OcOrderLineItem> createdLineItems;
        public List<OcOrderProduct> createdProducts;
        public List<OcOrderProductElement> createdProductElements;
        public List<OcOrderElementItem> createdElementItems;
        public List<String> steps;
    }

    public static class MemberRel {
        public String memUserId;
        public String memSerialNumber;
        public int isMainNumber; // 1/2 主号；其他值为副号
        public String relationTypeCode;

        public MemberRel(String memUserId, String memSerialNumber, int isMainNumber, String relationTypeCode) {
            this.memUserId = memUserId;
            this.memSerialNumber = memSerialNumber;
            this.isMainNumber = isMainNumber;
            this.relationTypeCode = relationTypeCode;
        }
    }

    public static class TfFUserInfo {
        public String userId;
        public String netTypeCode;
        public String productId;
        public String custId;
        public int removeTag;
    }

    public static class OcOrderLine {
        private Long orderId;
        private Long orderLineId;
        private String tradeTypeCode;
        private String sceneType;
        private String cancelTag;
        private Integer lineLevel;
        private String snUserId;
        private String serialNumber;
        private String productTypeCode;
        private String netTypeCode;
        private String userId;
        private String custId;
        private String mainProductId;
        private String mainProductName;
        private String mainProductType;
        private String productFamily;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Map<String, Object> extraAttrs;

        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private final OcOrderLine o = new OcOrderLine();
            public Builder orderId(Long v) { o.orderId = v; return this; }
            public Builder orderLineId(Long v) { o.orderLineId = v; return this; }
            public Builder tradeTypeCode(String v) { o.tradeTypeCode = v; return this; }
            public Builder sceneType(String v) { o.sceneType = v; return this; }
            public Builder cancelTag(String v) { o.cancelTag = v; return this; }
            public Builder lineLevel(Integer v) { o.lineLevel = v; return this; }
            public Builder snUserId(String v) { o.snUserId = v; return this; }
            public Builder serialNumber(String v) { o.serialNumber = v; return this; }
            public Builder productTypeCode(String v) { o.productTypeCode = v; return this; }
            public Builder netTypeCode(String v) { o.netTypeCode = v; return this; }
            public Builder userId(String v) { o.userId = v; return this; }
            public Builder custId(String v) { o.custId = v; return this; }
            public Builder mainProductId(String v) { o.mainProductId = v; return this; }
            public Builder mainProductName(String v) { o.mainProductName = v; return this; }
            public Builder mainProductType(String v) { o.mainProductType = v; return this; }
            public Builder productFamily(String v) { o.productFamily = v; return this; }
            public Builder startDate(LocalDateTime v) { o.startDate = v; return this; }
            public Builder endDate(LocalDateTime v) { o.endDate = v; return this; }
            public Builder extraAttrs(Map<String, Object> v) { o.extraAttrs = v; return this; }
            public OcOrderLine build() { return o; }
        }

        public Long getOrderId() { return orderId; }
        public Long getOrderLineId() { return orderLineId; }
        public String getTradeTypeCode() { return tradeTypeCode; }
        public String getSceneType() { return sceneType; }
        public String getCancelTag() { return cancelTag; }
        public Integer getLineLevel() { return lineLevel; }
        public String getSnUserId() { return snUserId; }
        public String getSerialNumber() { return serialNumber; }
        public String getProductTypeCode() { return productTypeCode; }
        public String getNetTypeCode() { return netTypeCode; }
        public String getUserId() { return userId; }
        public String getCustId() { return custId; }
        public String getMainProductId() { return mainProductId; }
        public String getMainProductName() { return mainProductName; }
        public String getMainProductType() { return mainProductType; }
        public String getProductFamily() { return productFamily; }
        public LocalDateTime getStartDate() { return startDate; }
        public LocalDateTime getEndDate() { return endDate; }
        public Map<String, Object> getExtraAttrs() { return extraAttrs; }
    }

    public static class OcOrderInstall {
        public Long orderId;
        public Long orderLineId;
        public String modifyTag;
        public String attr;

        public OcOrderInstall(Long orderId, Long orderLineId, String modifyTag, String attr) {
            this.orderId = orderId;
            this.orderLineId = orderLineId;
            this.modifyTag = modifyTag;
            this.attr = attr;
        }
    }

    public static class OcOrderInstallItem {
        public Long orderId;
        public Long orderLineId;
        public String modifyTag;
        public String attr;

        public OcOrderInstallItem(Long orderId, Long orderLineId, String modifyTag, String attr) {
            this.orderId = orderId;
            this.orderLineId = orderLineId;
            this.modifyTag = modifyTag;
            this.attr = attr;
        }
    }

    public static class OcOrderRelationOther {
        public Long orderId;
        public Long orderLineId;
        public String userId;
        public String serialNumber;
        public String attr;

        public OcOrderRelationOther(Long orderId, Long orderLineId, String userId, String serialNumber, String attr) {
            this.orderId = orderId;
            this.orderLineId = orderLineId;
            this.userId = userId;
            this.serialNumber = serialNumber;
            this.attr = attr;
        }
    }

    public static class OcOrderPayRelation {
        public Long orderId;
        public Long orderLineId;
        public String userId;
        public String serialNumber;
        public String attr;

        public OcOrderPayRelation(Long orderId, Long orderLineId, String userId, String serialNumber, String attr) {
            this.orderId = orderId;
            this.orderLineId = orderLineId;
            this.userId = userId;
            this.serialNumber = serialNumber;
            this.attr = attr;
        }
    }

    public static class OcOrderLineItem {
        public Long orderId;
        public Long orderLineId;
        public String attrCode;
        public String attrValue;

        public OcOrderLineItem(Long orderId, Long orderLineId, String attrCode, String attrValue) {
            this.orderId = orderId;
            this.orderLineId = orderLineId;
            this.attrCode = attrCode;
            this.attrValue = attrValue;
        }
    }

    public static class OcOrderProduct {
        public Long orderId;
        public String productId;
        public String otcLevel; // ORDER / DN

        public OcOrderProduct(Long orderId, String productId, String otcLevel) {
            this.orderId = orderId;
            this.productId = productId;
            this.otcLevel = otcLevel;
        }
    }

    public static class OcOrderProductElement {
        public Long orderId;
        public String productId;
        public String elementId;
        public String prodItemId;
        public LocalDateTime startDate;
        public LocalDateTime endDate;

        public OcOrderProductElement(Long orderId, String productId, String elementId, String prodItemId, LocalDateTime startDate, LocalDateTime endDate) {
            this.orderId = orderId;
            this.productId = productId;
            this.elementId = elementId;
            this.prodItemId = prodItemId;
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    public static class OcOrderElementItem {
        public Long orderId;
        public String elementId;
        public String elementItemId;
        public LocalDateTime startDate;
        public LocalDateTime endDate;

        public OcOrderElementItem(Long orderId, String elementId, String elementItemId, LocalDateTime startDate, LocalDateTime endDate) {
            this.orderId = orderId;
            this.elementId = elementId;
            this.elementItemId = elementItemId;
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }
}