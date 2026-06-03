package com.admin.aspect;

import com.admin.annotation.DataScope;
import com.admin.entity.Role;
import com.admin.mapper.RoleMapper;
import com.admin.security.LoginUser;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
public class DataScopeAspect {

    private final RoleMapper roleMapper;

    public DataScopeAspect(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Before("@annotation(dataScope)")
    public void doBefore(JoinPoint point, DataScope dataScope) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loginUser == null) {
            return;
        }

        if (loginUser.getPermissions().contains("*:*:*")) {
            return;
        }

        StringBuilder sqlString = new StringBuilder();
        List<Long> roleIds = List.copyOf(loginUser.getRoleIds());

        List<Role> roles = roleMapper.selectBatchIds(roleIds);
        for (Role role : roles) {
            String scope = getDataScopeFilter(role.getDataScope(), dataScope.deptAlias(), loginUser);
            if (scope != null) {
                sqlString.append(scope);
            }
        }

        if (sqlString.length() > 0) {
            Object params = point.getArgs().length > 0 ? point.getArgs()[0] : null;
            if (params instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> map = (java.util.Map<String, Object>) params;
                map.put("dataScope", " AND (" + sqlString.substring(4) + ")");
            }
        }
    }

    private String getDataScopeFilter(int dataScope, String deptAlias, LoginUser loginUser) {
        return switch (dataScope) {
            case 1 -> null;
            case 2 -> String.format(" OR %s.id = %d", deptAlias, loginUser.getDeptId());
            case 3 -> String.format(" OR %s.id IN (SELECT dept_id FROM sys_role_dept WHERE role_id IN (%s))",
                    deptAlias, loginUser.getRoleIds().stream().map(String::valueOf).collect(Collectors.joining(",")));
            case 4 -> String.format(" OR %s.id = %d", deptAlias, loginUser.getDeptId());
            case 5 -> String.format(" OR %s.id IN (SELECT dept_id FROM sys_dept WHERE dept_id = %d OR find_in_set(%d , ancestors))",
                    deptAlias, loginUser.getDeptId(), loginUser.getDeptId());
            default -> null;
        };
    }
}
