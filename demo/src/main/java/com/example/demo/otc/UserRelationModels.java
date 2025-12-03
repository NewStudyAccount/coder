package com.example.demo.otc;

import java.time.LocalDateTime;

public class UserRelationModels {

    public static class UserRelationRequest {
        public Long order_id;
        public Long order_line_id;
        public String serial_number;
        public String parent_serial_number;
        public String sn_user_id;
    }

    public static class UserRelationResponse {
        public boolean success;
        public String message;
        public UserLedger ledger;
    }

    public static class UserLedger {
        public Long order_id;
        public Long order_line_id;
        public String relation_type_code;
        public String user_id_a;
        public String user_id_b;
        public String serial_number_a;
        public String serial_number_b;
        public String call_sequence;
        public String is_primary_number;
        public Integer modify_tag;
        public LocalDateTime start_date;
        public LocalDateTime end_date;
    }
}