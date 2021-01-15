package com.itheima.health.dao;

import com.itheima.health.pojo.User;

public interface UserDao {
    /**
     * 根据用户名查询对应的用户信息、角色和权限
     * @param username
     * @return
     */
    User findByUsername(String username);
}
