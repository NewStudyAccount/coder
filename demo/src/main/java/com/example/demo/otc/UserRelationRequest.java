package com.example.demo.otc;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UserRelationRequest {
    @NotNull(message = "order_id不能为空")
    public Long order_id;
    @NotNull(message = "order_line_id不能为空")
    public Long order_line_id;
    @NotBlank(message = "serial_number不能为空")
    public String serial_number;
    public String parent_serial_number;
    @NotBlank(message = "sn_user_id不能为空")
    public String sn_user_id;
}