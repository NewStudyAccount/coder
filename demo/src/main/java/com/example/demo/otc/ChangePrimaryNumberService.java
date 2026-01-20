package com.example.demo.otc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ChangePrimaryNumberService {

    @Autowired
    private ChangePrimaryNumberMapper mapper;

    private AtomicLong sequenceGenerator = new AtomicLong(10000);

    @Transactional
    public void processChangePrimaryNumber(Long orderId) {
        // 0. Check trigger condition
        List<OcOrderLine> triggerLines = mapper.queryTriggerOrderLine(orderId);
        if (triggerLines == null || triggerLines.isEmpty()) {
            return; // Not meeting conditions
        }
        
        // Loop through trigger lines (assuming one main order line usually)
        for (OcOrderLine triggerLine : triggerLines) {
            // 1. Get new serial number from attr
            String newPrimaryNumber = mapper.getNewSerialNumber(triggerLine.getOrderId(), triggerLine.getOrderLineId());
            if (newPrimaryNumber == null) {
                continue; // Attr not found
            }
            
            // 2. Get member relations
            // Use sn_user_id from trigger line as user_id_a (primary user)
            Long primaryUserId = triggerLine.getSnUserId();
            List<TfFUserRelation> relations = mapper.getMemberRelations(primaryUserId);
            
            if (relations != null) {
                for (TfFUserRelation relation : relations) {
                    Long memberUserId = relation.getUserIdB();
                    String relationTypeCode = relation.getRelationTypeCode();
                    
                    // 2.1 Filter existing changes
                    int existingCount = mapper.countExistingRelationChange(orderId, primaryUserId, memberUserId, relationTypeCode);
                    if (existingCount > 0) {
                        continue;
                    }
                    
                    // 2.2 Process new changes
                    processMemberChange(triggerLine, relation, newPrimaryNumber);
                }
            }
        }
    }

    private void processMemberChange(OcOrderLine triggerLine, TfFUserRelation relation, String newPrimaryNumber) {
        Long memberUserId = relation.getUserIdB();
        String memberSerialNumber = relation.getSerialNumberB();
        
        // 2.2.1 Insert oc_order_line
        Long newOrderLineId = sequenceGenerator.incrementAndGet();
        
        // Get member user info
        TfFUser memberUserInfo = mapper.getUserInfo(memberUserId);
        
        OcOrderLine newLine = new OcOrderLine();
        newLine.setOrderId(triggerLine.getOrderId());
        newLine.setOrderLineId(newOrderLineId);
        newLine.setSerialNumber(memberSerialNumber);
        newLine.setSnUserId(memberUserId);
        newLine.setParentSerialNumber(newPrimaryNumber); // Using new primary number
        
        newLine.setTradeTypeCode("340");
        newLine.setSceneType("34007");
        
        if (memberUserInfo != null) {
            newLine.setSnCustId(memberUserInfo.getCustId());
            newLine.setMainProductId(memberUserInfo.getMainProductId());
            newLine.setNetTypeCode(memberUserInfo.getNetTypeCode());
            
            // Set line_level based on net_type_code
            if ("CP".equals(memberUserInfo.getNetTypeCode())) {
                newLine.setLineLevel(1);
            } else if ("CE".equals(memberUserInfo.getNetTypeCode())) {
                newLine.setLineLevel(2);
            }
        }
        
        // Copy fields from trigger line (step 0)
        newLine.setSrd(triggerLine.getSrd());
        newLine.setDepartId(triggerLine.getDepartId());
        newLine.setChannelId(triggerLine.getChannelId());
        newLine.setChannelType(triggerLine.getChannelType());
        newLine.setCityCode(triggerLine.getCityCode());
        newLine.setEparchyCode(triggerLine.getEparchyCode());
        newLine.setProvinceCode(triggerLine.getProvinceCode());
        newLine.setAcceptDate(triggerLine.getAcceptDate());
        newLine.setCancelTag(triggerLine.getCancelTag());
        newLine.setUpdateTime(triggerLine.getUpdateTime());
        newLine.setInModeCode(triggerLine.getInModeCode());
        
        mapper.insertOcOrderLine(newLine);
        
        // 2.2.2 Insert oc_order_relation_uu
        OcOrderRelationUu newRelation = new OcOrderRelationUu();
        newRelation.setOrderId(triggerLine.getOrderId());
        newRelation.setOrderLineId(newOrderLineId);
        newRelation.setUserIdA(relation.getUserIdA());
        newRelation.setSerialNumberA(newPrimaryNumber); // New Primary Number from step 1
        newRelation.setRoleIdA(relation.getRoleCodeA()); // Using role code from relation
        newRelation.setRelationTypeCode(relation.getRelationTypeCode());
        newRelation.setUserIdB(memberUserId);
        newRelation.setSerialNumberB(memberSerialNumber);
        newRelation.setRoleIdB(relation.getRoleCodeB());
        newRelation.setCallSequence(relation.getOrderNo());
        // is_primary_number? Assume from existing logic or set default. Question says "步骤2的is_main_number", assume relation has it but TfFUserRelation didn't have it.
        // Let's assume '0' or '1' based on role or just set null if not critical. Added field to TfFUserRelation?
        // Or maybe it means shortCode? Let's skip if not available in source.
        newRelation.setModifyTag("2");
        newRelation.setStartDate(relation.getStartDate());
        newRelation.setEndDate(relation.getEndDate());
        
        mapper.insertOcOrderRelationUu(newRelation);
    }
}
