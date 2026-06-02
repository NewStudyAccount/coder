package com.admin.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class RoleCreateRequest {

    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称最多50个字符")
    private String roleName;

    @NotBlank(message = "角色标识不能为空")
    @Size(max = 50, message = "角色标识最多50个字符")
    private String roleKey;

    @NotNull(message = "排序不能为空")
    private Integer roleSort;

    private Integer dataScope;
    private Integer status;
    private String remark;
    private List<Long> menuIds;
}
