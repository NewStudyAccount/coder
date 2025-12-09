package com.example.demo.otc;

import com.example.demo.SnowflakeIdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * IDAP 取消号段并连带对散号拆机的业务服务（Mock实现）
 *
 * 需求摘要：
 * 1. 获取取消号段的订单：通过 order_id + trade_type_code = 110 获取群组用户的订单行
 * 2. 获取对应取消的号段：通过 order_id + order_line_id + modify_tag='1' + action_type='30' 查询 oc_order_num_segment
 * 3. 获取号段对应的散号：
 *    - 号段解析出散号：号段 + 00..99
 *    - 通过入参 user_id（群组用户标识，tf_F_user_relation.user_id_a） + 拆分号码（tf_F_user_relation.serial_number_b截取后8位）
 *      调用户中心服务判断是否有对应的 uu 关系（此处 Mock），有则继续
 * 4. 轮询对每个散号增补拆机订单：
 *    4.1 增加 oc_order_line：复制步骤1的行，替换 order_line_id（序列生成）、net_type_code=30、line_level=1、
 *        serial_number、user_id、cust_id、main_product_id、MAIN_PRODUCT_NAME、MAIN_PRODUCT_TYPE、PRODUCT_FAMILY
 *        （通过用户中心/商品中心获取，Mock）
 *    4.2 编排公共构建服务：九、十、十一、十二、四（占位，写注释与返回）
 *
 * 说明：
 * - 本实现为内存与 Mock，未知数据用 "default" 或 0/空集合填充
 * - 可与前端直接打通展示
 */
@Service
public class IdapCancelSegmentService {

    /**
     * Mock: oc_order_line 表记录
     */
    private final List<OcOrderLine> ocOrderLines = Collections.synchronizedList(new ArrayList<>());

    /**
     * Mock: oc_order_num_segment 号段表记录
     */
    private final List<OcOrderNumSegment> ocOrderNumSegments = Collections.synchronizedList(new ArrayList<>());

    /**
     * 初始化一些 Mock 数据，便于演示
     */
    public IdapCancelSegmentService() {
        // 初始化一条群组订单行 trade_type_code = 110
        ocOrderLines.add(OcOrderLine.builder()
                .orderId(1001L)
                .orderLineId(2001L)
                .tradeTypeCode("110")
                .netTypeCode("10")
                .lineLevel(2)
                .serialNumber("13800138000")
                .userId("U-GROUP-001")
                .custId("CUST-001")
                .mainProductId("PROD-001")
                .mainProductName("Group Main Product")
                .mainProductType("BUNDLE")
                .productFamily("GROUP")
                .extraAttrs(new HashMap<>())
                .build());

        // 初始化对应取消号段 modify_tag=1, action_type=30
        ocOrderNumSegments.add(OcOrderNumSegment.builder()
                .orderId(1001L)
                .orderLineId(2001L)
                .modifyTag("1")
                .actionType("30")
                .segmentPrefix("13800138") // 号段前缀（示例）
                .build());
    }

    /**
     * 执行业务主流程
     */
    public IdapCancelSegmentResponse execute(IdapCancelSegmentRequest req) {
        IdapCancelSegmentResponse resp = new IdapCancelSegmentResponse();
        resp.setSuccess(false);
        resp.setMessage("default");
        resp.setCreatedLines(new ArrayList<>());
        resp.setBuildSteps(new ArrayList<>());

        // 1. 获取取消号段的订单行（trade_type_code = 110）
        List<OcOrderLine> groupLines = ocOrderLines.stream()
                .filter(l -> Objects.equals(l.getOrderId(), req.getOrderId()))
                .filter(l -> "110".equals(l.getTradeTypeCode()))
                .collect(Collectors.toList());

        if (groupLines.isEmpty()) {
            resp.setMessage("未找到 trade_type_code=110 的群组订单行");
            return resp;
        }
        // 简化：取第一条作为基线
        OcOrderLine baseLine = groupLines.get(0);

        // 2. 查询对应取消的号段
        Optional<OcOrderNumSegment> segmentOpt = ocOrderNumSegments.stream()
                .filter(s -> Objects.equals(s.getOrderId(), baseLine.getOrderId()))
                .filter(s -> Objects.equals(s.getOrderLineId(), baseLine.getOrderLineId()))
                .filter(s -> "1".equals(s.getModifyTag()))
                .filter(s -> "30".equals(s.getActionType()))
                .findFirst();

        if (segmentOpt.isEmpty()) {
            resp.setMessage("未找到符合条件的号段记录（modify_tag=1, action_type=30）");
            return resp;
        }

        String segmentPrefix = segmentOpt.get().getSegmentPrefix(); // 如 "13800138"
        // 3. 号段解析散号：prefix + 00..99
        List<String> scatteredNumbers = new ArrayList<>();
        for (int i = 0; i <= 99; i++) {
            scatteredNumbers.add(segmentPrefix + String.format("%02d", i));
        }

        // 4. 轮询每个散号，判断是否有 uu 关系（Mock），有则增补拆机订单
        List<String> buildSteps = resp.getBuildSteps();

        for (String msisdn : scatteredNumbers) {
            boolean hasUuRel = mockHasUuRelation(req.getUserId(), msisdn);
            if (!hasUuRel) {
                buildSteps.add("散号 " + msisdn + " 无 uu 关系，跳过");
                continue;
            }
            // 4.1 复制并生成新拆机订单行
            OcOrderLine newLine = buildDisassembleOrderLine(baseLine, msisdn, req);
            ocOrderLines.add(newLine);
            resp.getCreatedLines().add(newLine);

            // 4.2 编排公共构建服务（占位）
            buildSteps.add("编排服务-九：default");
            buildSteps.add("编排服务-十：default");
            buildSteps.add("编排服务-十一：default");
            buildSteps.add("编排服务-十二：default");
            buildSteps.add("编排服务-四：default");
        }

        resp.setSuccess(true);
        resp.setMessage("处理完成");
        return resp;
    }

    /**
     * Mock：判断是否存在 UU 关系
     * 简化逻辑：如果号码末尾为偶数则认为存在 UU 关系
     */
    private boolean mockHasUuRelation(String userIdA, String serialNumberB) {
        if (serialNumberB == null || serialNumberB.isEmpty()) return false;
        char last = serialNumberB.charAt(serialNumberB.length() - 1);
        return Character.isDigit(last) && ((last - '0') % 2 == 0);
    }

    /**
     * 构建拆机订单行（复制基线订单行并替换必要字段）
     */
    private OcOrderLine buildDisassembleOrderLine(OcOrderLine base, String msisdn, IdapCancelSegmentRequest req) {
        long newLineId = SnowflakeIdGenerator.nextId();

        // Mock 用户中心：通过 serialNumber + net_type_code=30 获取 user_id/cust_id/main_product_id
        UserCenterInfo userCenter = mockQueryUserCenter(msisdn, "30");

        // Mock 商品中心：通过 main_product_id 获取产品名称/类型/族群
        ProductInfo productInfo = mockQueryProductCenter(userCenter.getMainProductId());

        Map<String, Object> newAttrs = new HashMap<>();
        newAttrs.put("created_at", LocalDateTime.now().toString());
        newAttrs.put("source_line_id", base.getOrderLineId());
        newAttrs.put("disassemble_reason", "IDAP");

        return OcOrderLine.builder()
                .orderId(base.getOrderId())
                .orderLineId(newLineId)
                .tradeTypeCode("110") // 与基线一致
                .netTypeCode("30")    // 指定拆机网别
                .lineLevel(1)         // 拆机行级别
                .serialNumber(msisdn)
                .userId(userCenter.getUserId())
                .custId(userCenter.getCustId())
                .mainProductId(userCenter.getMainProductId())
                .mainProductName(productInfo.getMainProductName())
                .mainProductType(productInfo.getMainProductType())
                .productFamily(productInfo.getProductFamily())
                .extraAttrs(newAttrs)
                .build();
    }

    // Mock: 用户中心返回信息
    private UserCenterInfo mockQueryUserCenter(String serialNumber, String netTypeCode) {
        String suffix = (serialNumber != null && serialNumber.length() >= 4)
                ? serialNumber.substring(serialNumber.length() - 4)
                : "0000";
        UserCenterInfo info = new UserCenterInfo();
        info.setSerialNumber(serialNumber);
        info.setUserId("U-" + suffix);
        info.setCustId("C-" + suffix);
        info.setMainProductId("PROD-" + suffix);
        return info;
    }

    // Mock: 商品中心返回信息
    private ProductInfo mockQueryProductCenter(String mainProductId) {
        ProductInfo info = new ProductInfo();
        info.setMainProductName("default-product-name");
        info.setMainProductType("default-product-type");
        info.setProductFamily("default-product-family");
        return info;
    }

    // ========= DTO / Mock 表结构 =========

    public static class IdapCancelSegmentRequest {
        private Long orderId;
        private String userId;

        public Long getOrderId() {
            return orderId;
        }
        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }
        public String getUserId() {
            return userId;
        }
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    public static class IdapCancelSegmentResponse {
        private boolean success;
        private String message;
        private List<OcOrderLine> createdLines;
        private List<String> buildSteps;

        public boolean isSuccess() {
            return success;
        }
        public void setSuccess(boolean success) {
            this.success = success;
        }
        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        public List<OcOrderLine> getCreatedLines() {
            return createdLines;
        }
        public void setCreatedLines(List<OcOrderLine> createdLines) {
            this.createdLines = createdLines;
        }
        public List<String> getBuildSteps() {
            return buildSteps;
        }
        public void setBuildSteps(List<String> buildSteps) {
            this.buildSteps = buildSteps;
        }
    }

    public static class OcOrderLine {
        private Long orderId;
        private Long orderLineId;
        private String tradeTypeCode;
        private String netTypeCode;
        private Integer lineLevel;
        private String serialNumber;
        private String userId;
        private String custId;
        private String mainProductId;
        private String mainProductName;
        private String mainProductType;
        private String productFamily;
        private Map<String, Object> extraAttrs;

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private final OcOrderLine o = new OcOrderLine();
            public Builder orderId(Long v) { o.orderId = v; return this; }
            public Builder orderLineId(Long v) { o.orderLineId = v; return this; }
            public Builder tradeTypeCode(String v) { o.tradeTypeCode = v; return this; }
            public Builder netTypeCode(String v) { o.netTypeCode = v; return this; }
            public Builder lineLevel(Integer v) { o.lineLevel = v; return this; }
            public Builder serialNumber(String v) { o.serialNumber = v; return this; }
            public Builder userId(String v) { o.userId = v; return this; }
            public Builder custId(String v) { o.custId = v; return this; }
            public Builder mainProductId(String v) { o.mainProductId = v; return this; }
            public Builder mainProductName(String v) { o.mainProductName = v; return this; }
            public Builder mainProductType(String v) { o.mainProductType = v; return this; }
            public Builder productFamily(String v) { o.productFamily = v; return this; }
            public Builder extraAttrs(Map<String, Object> v) { o.extraAttrs = v; return this; }
            public OcOrderLine build() { return o; }
        }

        public Long getOrderId() { return orderId; }
        public Long getOrderLineId() { return orderLineId; }
        public String getTradeTypeCode() { return tradeTypeCode; }
        public String getNetTypeCode() { return netTypeCode; }
        public Integer getLineLevel() { return lineLevel; }
        public String getSerialNumber() { return serialNumber; }
        public String getUserId() { return userId; }
        public String getCustId() { return custId; }
        public String getMainProductId() { return mainProductId; }
        public String getMainProductName() { return mainProductName; }
        public String getMainProductType() { return mainProductType; }
        public String getProductFamily() { return productFamily; }
        public Map<String, Object> getExtraAttrs() { return extraAttrs; }
    }

    public static class OcOrderNumSegment {
        private Long orderId;
        private Long orderLineId;
        private String modifyTag;
        private String actionType;
        private String segmentPrefix;

        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private final OcOrderNumSegment s = new OcOrderNumSegment();
            public Builder orderId(Long v) { s.orderId = v; return this; }
            public Builder orderLineId(Long v) { s.orderLineId = v; return this; }
            public Builder modifyTag(String v) { s.modifyTag = v; return this; }
            public Builder actionType(String v) { s.actionType = v; return this; }
            public Builder segmentPrefix(String v) { s.segmentPrefix = v; return this; }
            public OcOrderNumSegment build() { return s; }
        }

        public Long getOrderId() { return orderId; }
        public Long getOrderLineId() { return orderLineId; }
        public String getModifyTag() { return modifyTag; }
        public String getActionType() { return actionType; }
        public String getSegmentPrefix() { return segmentPrefix; }
    }

    public static class UserCenterInfo {
        private String serialNumber;
        private String userId;
        private String custId;
        private String mainProductId;

        public String getSerialNumber() { return serialNumber; }
        public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getCustId() { return custId; }
        public void setCustId(String custId) { this.custId = custId; }
        public String getMainProductId() { return mainProductId; }
        public void setMainProductId(String mainProductId) { this.mainProductId = mainProductId; }
    }

    public static class ProductInfo {
        private String mainProductName;
        private String mainProductType;
        private String productFamily;

        public String getMainProductName() { return mainProductName; }
        public void setMainProductName(String mainProductName) { this.mainProductName = mainProductName; }
        public String getMainProductType() { return mainProductType; }
        public void setMainProductType(String mainProductType) { this.mainProductType = mainProductType; }
        public String getProductFamily() { return productFamily; }
        public void setProductFamily(String productFamily) { this.productFamily = productFamily; }
    }
}