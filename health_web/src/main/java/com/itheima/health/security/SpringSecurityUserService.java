package com.itheima.health.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.pojo.Permission;
import com.itheima.health.pojo.Role;
import com.itheima.health.pojo.User;
import com.itheima.health.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SpringSecurityUserService implements UserDetailsService {

    /**
     * 服务
     */
    @Reference
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 调用服务的方法，跟据用户名查询用户相关信息
        User user = userService.findByUsername(username);
        // 判断这个User如果不为空，就从这个User里取出用户名、密码、角色和权限集合
        if (null != user){
            //创建一个集合，把角色和权限都存进这个集合里
            // 泛型为GrantedAuthority，这是一个接口，所有要把角色和权限的存到它的实现类，在把这个接口的实现类存到这个接口的集合里
            List<GrantedAuthority> authorities = new ArrayList<>();
            // 获取User查询到的角色集合
            Set<Role> userRoles = user.getRoles();
            // 判断这个角色集合是不是空的
            if (null != userRoles){
                // 如果不是空的就遍历整个集合，取出每个角色，获取每个觉得keyWord存到GrantedAuthority接口的实现类里
                // 然后添加到泛型为GrantedAuthority接口的集合里
                for (Role role : userRoles) {
                    authorities.add(new SimpleGrantedAuthority(role.getKeyword()));
                    // 取出这个角色的所有权限
                    Set<Permission> permissions = role.getPermissions();
                    // 判断这个权限集合不为空
                    if (null != permissions){
                        // 遍历取出每个权限，剩余的操作跟操作角色一样
                        for (Permission permission : permissions) {
                            authorities.add(new SimpleGrantedAuthority(permission.getKeyword()));
                        }
                    }
                }
            }
            //创建一个UserDetails的实现类。并把这个存满了用户权限信息的实现类返回
            org.springframework.security.core.userdetails.User securityUser = new org.springframework.security.core.userdetails.User(
                    username,
                    user.getPassword(),
                    authorities
            );
            return securityUser;
        }
        return null;
    }
}
