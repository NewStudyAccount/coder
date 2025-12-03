package com.example.demo.otc;

import java.time.LocalDateTime;

public class UserLedger {
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