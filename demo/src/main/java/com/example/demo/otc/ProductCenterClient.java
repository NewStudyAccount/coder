package com.example.demo.otc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 产品中心判定桩：基于配置项 otc.dn.product-type-codes 判定是否为 DN 级 OTC 产品
 */
@Component
public class ProductCenterClient {

    private final Set<String> dnOtcTypes;

    public ProductCenterClient(@Value("${otc.dn.product-type-codes:OTC_DN,DN_OTC}") String dnOtcTypesCsv) {
        if (dnOtcTypesCsv == null || dnOtcTypesCsv.trim().isEmpty()) {
            this.dnOtcTypes = new HashSet<>();
        } else {
            this.dnOtcTypes = new HashSet<>();
            Arrays.stream(dnOtcTypesCsv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(s -> this.dnOtcTypes.add(s.toUpperCase()));
        }
    }

    /**
     * 基于 product_type_code 判定
     */
    public boolean isDnLevelOtcProduct(String productTypeCode) {
        if (productTypeCode == null) return false;
        return dnOtcTypes.contains(productTypeCode.toUpperCase());
    }
}