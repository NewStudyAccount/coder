package com.admin.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class UserCreateRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度2-20个字符")
    private String username;

    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称最多50个字符")
    private String nickname;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度6-20个字符")
    private String password;

    private Long deptId;
    private String email;
    private String phone;
    private Integer gender;
    private Integer status;
    private String remark;
    private List<Long> roleIds;
    private List<Long> postIds;
}
