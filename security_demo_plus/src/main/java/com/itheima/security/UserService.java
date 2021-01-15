package com.itheima.security;

import com.itheima.health.pojo.Permission;
import com.itheima.health.pojo.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserService implements UserDetailsService {
    /**
     * 获取认证用户信息(用户名、密码、权限集合)
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库查询，通过用户名查询用户信息，得到一个User对象
        com.itheima.health.pojo.User user = findByUsername(username);
        // 判断如果user等于null的话，就直接返回null
        if (null == user){
            return null;
        }
        // 返回用户信息。同样需要 用户名、密码、权限集合
        // 但是认证管理器需要的是UserDetails，这是一个接口
        // 他有一个实现类也叫User,这个User的属性里，刚好就有用户名、密码、权限集合
        // 这个实现类他是没有空参构造的，new这个对象需要3个参数，用户名、密码、权限集合
        // 刚好我们调用findByUsername都有查到了，全部赋值进去即可
        // 因为它的构造方法最后一个参数需要的是一个权限集合，所以创建一个集合，把从findByUsername查到的权限集合赋值进去
        List<GrantedAuthority> authorities = new ArrayList<>();
        // GrantedAuthority 这个泛型需要的是或实现了这个接口的实现类，因为securityUser构造方法的参数需要的就是这样的一个集合
        // 然后从User对象种取出刚才查询到用户的角色与权限的集合
        Set<Role> userRoles = user.getRoles();
        //判断这个集合不为空的话就遍历这个集合
        if (null != userRoles){
            for (Role role : userRoles) {
                SimpleGrantedAuthority sgai = new SimpleGrantedAuthority(role.getKeyword());
                //然后把这个授予了角色名的实现类存到authorities
                authorities.add(sgai);
                // 遍历出来的角色，都会有指定权限，获取它的权限集合，不为null就进行遍历
                Set<Permission> permissions = role.getPermissions();
                if (null != permissions){
                    for (Permission permission : permissions) {
                        // 遍历出来的每个权限都会有权限名
                        // 创建GrantedAuthority的实现类的，把每个权限的名字赋予这个实现类
                        // 然后添加到authorities里
                        authorities.add(new SimpleGrantedAuthority(permission.getKeyword()));
                    }
                }
            }
        }
        //把角色名和权限名赋值好了给authorities，
        User securityUser = new User(username,"{noop}"+user.getPassword(),authorities);
        return securityUser;
    }

    /**
     * 这个用户admin/admin, 有ROLE_ADMIN角色，角色下有ADD_CHECKITEM权限
     * 假设从数据库查询
     * @param username
     * @return
     */
    private com.itheima.health.pojo.User findByUsername (String username){
        if("admin".equals(username)) {
            com.itheima.health.pojo.User user = new com.itheima.health.pojo.User();
            user.setUsername("admin");
            // 使用密文
            user.setPassword("admin123");

            // 角色
            Role role = new Role();
            role.setKeyword("ROLE_ADMIN");

            // 权限
            Permission permission = new Permission();
            permission.setKeyword("ADD_CHECKITEM");

            // 给角色添加权限
            role.getPermissions().add(permission);

            // 把角色放进集合
            Set<Role> roleList = new HashSet<Role>();
            roleList.add(role);

            role = new Role();
            role.setKeyword("ABC");
            roleList.add(role);

            // 设置用户的角色
            user.setRoles(roleList);
            return user;
        }
        return null;
    }
}
