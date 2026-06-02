package com.admin.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class UserUpdateRequest {

    @NotNull(message = "用户ID不能为空")
    private Long id;

    @Size(min = 2, max = 20, message = "用户名长度2-20个字符")
    private String username;

    @Size(max = 50, message = "昵称最多50个字符")
    private String nickname;

    private Long deptId;
    private String email;
    private String phone;
    private Integer gender;
    private Integer status;
    private String remark;
    private List<Long> roleIds;
    private List<Long> postIds;
}
