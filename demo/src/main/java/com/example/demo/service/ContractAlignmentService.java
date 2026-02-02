package com.example.demo.service;

import com.example.demo.entity.OcOrderElementItem;
import com.example.demo.entity.OcOrderProduct;
import com.example.demo.entity.OcOrderProductElement;
import com.example.demo.mapper.ContractAlignmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 合约对齐服务类
 * 对齐非主资费的合约与主资费的UAT
 */
@Service
public class ContractAlignmentService {

    @Autowired
    private ContractAlignmentMapper contractAlignmentMapper;

    @Autowired
    private RestTemplate restTemplate;

    private static final String DEFAULT_END_DATE = "2099-12-31 23:59:59";
    private static final String MODIFY_TAG_NEW = "0";
    private static final String ELEMENT_TYPE_D = "D";
    private static final String IS_MAIN_ELEMENT_YES = "1";
    private static final String IS_MAIN_ELEMENT_NO = "0";

    /**
     * 对齐非主资费合约与主资费UAT
     * @param orderId 订单ID
     * @param orderLineId 订单行ID
     * @param srd 服务请求日期
     */
    @Transactional(rollbackFor = Exception.class)
    public void alignNonMainContractWithMainUat(String orderId, String orderLineId, String srd) {
        // 1. 获取是否有新增产品的台账
        List<OcOrderProduct> newProducts = contractAlignmentMapper.queryNewProducts(
                orderId, orderLineId, MODIFY_TAG_NEW);

        if (newProducts == null || newProducts.isEmpty()) {
            return;
        }

        // 循环处理每个新增产品
        for (OcOrderProduct product : newProducts) {
            processProduct(orderId, orderLineId, product, srd);
        }
    }

    /**
     * 处理单个产品
     */
    private void processProduct(String orderId, String orderLineId, OcOrderProduct product, String srd) {
        List<Map<String, String>> mainContractAttrList = new ArrayList<>();

        // 1.0 通过product_id调用产品中心服务获取产品标签
        if (shouldSkipProduct(product.getProductId())) {
            return;
        }

        // 1.1 判断是否有UAT属性:查询主资费元素
        OcOrderProductElement mainElement = contractAlignmentMapper.queryMainElement(
                orderId, orderLineId, product.getProductId(), product.getProdItemId(),
                ELEMENT_TYPE_D, MODIFY_TAG_NEW, IS_MAIN_ELEMENT_YES);

        if (mainElement == null) {
            return;
        }

        // 1.1.1 是否有有效的UAT属性
        OcOrderElementItem uatPeriodItem = contractAlignmentMapper.queryElementItem(
                orderId, orderLineId, mainElement.getElementId(), mainElement.getElementItemId(),
                "uat_period", MODIFY_TAG_NEW);

        if (uatPeriodItem == null) {
            return;
        }

        String mainElementUatPeriod = uatPeriodItem.getAttrValue();

        // 1.1.1.1 补齐UAT相关属性
        String uatEndDate = calculateUatEndDate(orderId, orderLineId, mainElement, mainElementUatPeriod, srd);

        // 补充uat_end_date属性
        insertElementItem(orderId, orderLineId, mainElement.getElementId(), mainElement.getElementItemId(),
                "uat_end_date", uatEndDate, srd, DEFAULT_END_DATE);

        // 补充uat_start_date属性
        insertElementItem(orderId, orderLineId, mainElement.getElementId(), mainElement.getElementItemId(),
                "uat_start_date", srd, srd, DEFAULT_END_DATE);

        // 1.1.1.2 补齐MRC相关属性
        String contractStartDate = plusSeconds(uatEndDate, 1);

        // 计算contract_end_date_std
        String contractEndDateStd = calculateContractEndDate(orderId, orderLineId, mainElement, uatEndDate);

        // 补充contract_start_date
        insertElementItem(orderId, orderLineId, mainElement.getElementId(), mainElement.getElementItemId(),
                "contract_start_date", contractStartDate, srd, DEFAULT_END_DATE);

        // 补充contract_end_date_std
        insertElementItem(orderId, orderLineId, mainElement.getElementId(), mainElement.getElementItemId(),
                "contract_end_date_std", contractEndDateStd, srd, DEFAULT_END_DATE);

        // 1.1.1.3 获取主资费的全部合约相关属性
        List<String> attrCodes = Arrays.asList("uat_period", "uat_period_method", "uat_start_date",
                "uat_end_date", "contract_start_date", "contract_end_date_std", "contract_period");

        List<OcOrderElementItem> contractAttrs = contractAlignmentMapper.queryElementItems(
                orderId, orderLineId, mainElement.getElementId(), mainElement.getElementItemId(),
                attrCodes, MODIFY_TAG_NEW);

        if (contractAttrs != null && !contractAttrs.isEmpty()) {
            for (OcOrderElementItem attr : contractAttrs) {
                Map<String, String> attrMap = new HashMap<>();
                attrMap.put("attrCode", attr.getAttrCode());
                attrMap.put("attrValue", attr.getAttrValue());
                attrMap.put("startDate", attr.getStartDate());
                attrMap.put("endDate", attr.getEndDate());
                mainContractAttrList.add(attrMap);
            }
        }

        // 1.1.1.4 对齐每个产品下的非主资费与主资费
        if (!mainContractAttrList.isEmpty()) {
            alignNonMainElements(orderId, orderLineId, product, mainContractAttrList);
        }
    }

    /**
     * 调用产品中心服务判断是否应跳过该产品
     */
    private boolean shouldSkipProduct(String productId) {
        try {
            // 调用产品中心服务获取产品标签
            // 这里使用RestTemplate调用外部服务示例
            String url = "http://product-center-service/api/product/label?productId=" + productId + "&labelKey=misaligned_with_main_contract";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && "1".equals(String.valueOf(response.get("labelValue")))) {
                return true;
            }
        } catch (Exception e) {
            // 如果服务调用失败,记录日志但不中断流程
            System.err.println("调用产品中心服务失败: " + e.getMessage());
        }
        return false;
    }

    /**
     * 计算UAT结束日期
     */
    private String calculateUatEndDate(String orderId, String orderLineId, OcOrderProductElement mainElement,
                                      String mainElementUatPeriod, String srd) {
        // 获取uat_period_method
        OcOrderElementItem methodItem = contractAlignmentMapper.queryElementItem(
                orderId, orderLineId, mainElement.getElementId(), mainElement.getElementItemId(),
                "uat_period_method", MODIFY_TAG_NEW);

        String uatPeriodMethod = methodItem != null ? methodItem.getAttrValue() : "";

        if ("uat_days".equals(uatPeriodMethod)) {
            // SRD + mainElementUatPeriod天 + 23:59:59
            return plusDays(srd, Integer.parseInt(mainElementUatPeriod)) + " 23:59:59";
        } else if ("uat_end_day".equals(uatPeriodMethod)) {
            // 直接使用mainElementUatPeriod
            return mainElementUatPeriod;
        }
        return srd;
    }

    /**
     * 计算合约结束日期
     */
    private String calculateContractEndDate(String orderId, String orderLineId, OcOrderProductElement mainElement,
                                           String uatEndDate) {
        // 获取contract_period和contract_period_type
        OcOrderElementItem periodItem = contractAlignmentMapper.queryElementItem(
                orderId, orderLineId, mainElement.getElementId(), mainElement.getElementItemId(),
                "contract_period", MODIFY_TAG_NEW);

        OcOrderElementItem periodTypeItem = contractAlignmentMapper.queryElementItem(
                orderId, orderLineId, mainElement.getElementId(), mainElement.getElementItemId(),
                "pm_contract_period_type", MODIFY_TAG_NEW);

        String contractPeriod = periodItem != null ? periodItem.getAttrValue() : "";
        String contractPeriodType = periodTypeItem != null ? periodTypeItem.getAttrValue() : "Standard_Contract_Period";

        String contractStartDatePlusOne = plusSeconds(uatEndDate, 1);

        return calculateContractEndDateBasedOnType(contractPeriodType, contractPeriod, contractStartDatePlusOne);
    }

    /**
     * 根据类型计算合约结束日期
     */
    private String calculateContractEndDateBasedOnType(String contractPeriodType, String contractPeriod, String startDate) {
        if ("Customized_Contract_End_Date".equals(contractPeriodType)) {
            return contractPeriod;
        } else {
            // Standard_Contract_Period: 按月数计算
            int months = Integer.parseInt(contractPeriod);
            return plusMonths(startDate, months);
        }
    }

    /**
     * 对齐非主资费元素
     */
    private void alignNonMainElements(String orderId, String orderLineId, OcOrderProduct product,
                                     List<Map<String, String>> mainContractAttrList) {
        // 查询非主资费元素列表
        List<OcOrderProductElement> nonMainElements = contractAlignmentMapper.queryNonMainElements(
                orderId, orderLineId, product.getProductId(), product.getProdItemId(),
                ELEMENT_TYPE_D, MODIFY_TAG_NEW, IS_MAIN_ELEMENT_NO);

        if (nonMainElements == null || nonMainElements.isEmpty()) {
            return;
        }

        // 批量插入属性列表
        List<OcOrderElementItem> itemsToInsert = new ArrayList<>();

        for (OcOrderProductElement element : nonMainElements) {
            for (Map<String, String> attrMap : mainContractAttrList) {
                OcOrderElementItem item = new OcOrderElementItem();
                item.setOrderId(orderId);
                item.setOrderLineId(orderLineId);
                item.setElementId(element.getElementId());
                item.setElementItemId(element.getElementItemId());
                item.setElementTypeCode(ELEMENT_TYPE_D);
                item.setAttrCode(attrMap.get("attrCode"));
                item.setAttrValue(attrMap.get("attrValue"));
                item.setModifyTag(MODIFY_TAG_NEW);
                item.setStartDate(attrMap.get("startDate"));
                item.setEndDate(attrMap.get("endDate"));
                itemsToInsert.add(item);
            }
        }

        if (!itemsToInsert.isEmpty()) {
            contractAlignmentMapper.batchInsertElementItems(itemsToInsert);
        }
    }

    /**
     * 插入元素属性
     */
    private void insertElementItem(String orderId, String orderLineId, String elementId, String elementItemId,
                                   String attrCode, String attrValue, String startDate, String endDate) {
        OcOrderElementItem item = new OcOrderElementItem();
        item.setOrderId(orderId);
        item.setOrderLineId(orderLineId);
        item.setElementId(elementId);
        item.setElementItemId(elementItemId);
        item.setElementTypeCode(ELEMENT_TYPE_D);
        item.setAttrCode(attrCode);
        item.setAttrValue(attrValue);
        item.setModifyTag(MODIFY_TAG_NEW);
        item.setStartDate(startDate);
        item.setEndDate(endDate);
        contractAlignmentMapper.insertElementItem(item);
    }

    /**
     * 日期加秒数
     */
    private String plusSeconds(String dateTime, int seconds) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(dateTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.SECOND, seconds);
            return sdf.format(cal.getTime());
        } catch (Exception e) {
            return dateTime;
        }
    }

    /**
     * 日期加天数
     */
    private String plusDays(String dateTime, int days) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(dateTime.split(" ")[0] + " 00:00:00");
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, days);
            return sdf.format(cal.getTime());
        } catch (Exception e) {
            return dateTime;
        }
    }

    /**
     * 日期加月数
     */
    private String plusMonths(String dateTime, int months) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(dateTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.MONTH, months);
            return sdf.format(cal.getTime());
        } catch (Exception e) {
            return dateTime;
        }
    }
}
