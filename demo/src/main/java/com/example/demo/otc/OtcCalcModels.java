package com.example.demo.otc;

import java.time.LocalDateTime;
import java.util.List;

public class OtcCalcModels {

    public static class OtcCalcRequest {
        public List<Long> amendOrderList;
        public String trade_type_code;
        public Integer cancel_tag;
        public Long cancelDnQty;
    }

    public static class OtcItem {
        public String trade_type_code;
        public Long product_id;
        public Long prod_item_id;
        public Long package_id;
        public Long element_id;
        public Long element_item_id;
        public LocalDateTime start_date;
        public LocalDateTime end_date;
        public Long standard_fee; // 分
        public Long otc_fee; // 分
        public Long rebate_fee; // 分
        public Long qty; // 数量
        public Long waived_fee; // 分
        public Long total_fee; // 分
    }

    public static class OtcCalcResponse {
        public long dnQty;
        public List<OtcItem> dnLevelOtcProductList;
        public List<OtcItem> orderLevelOtcProductList;
    }
}