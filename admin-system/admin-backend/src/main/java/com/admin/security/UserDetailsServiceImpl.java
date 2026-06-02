package com.admin.security;

import com.admin.entity.User;
import com.admin.mapper.UserMapper;
import com.admin.mapper.MenuMapper;
import com.admin.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;
    private final MenuMapper menuMapper;
    private final RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        if (user.getStatus() == 0) {
            throw new UsernameNotFoundException("用户已停用: " + username);
        }

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setPassword(user.getPassword());
        loginUser.setNickname(user.getNickname());
        loginUser.setDeptId(user.getDeptId());

        List<Long> roleIds = roleMapper.selectRoleIdsByUserId(user.getId());
        loginUser.setRoleIds(new HashSet<>(roleIds));

        Set<String> permissions = new HashSet<>();
        if (roleIds.contains(1L)) {
            permissions.add("*:*:*");
        } else {
            List<String> perms = menuMapper.selectPermsByUserId(user.getId());
            permissions.addAll(perms);
        }
        loginUser.setPermissions(permissions);

        return loginUser;
    }
}
