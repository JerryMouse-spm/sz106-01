package com.itheima.health.service;

import com.itheima.health.exception.MyException;
import com.itheima.health.pojo.Order;

import java.util.Map;

public interface OrderService {
    /**
     * 预约提交
     * @param orderInfo
     * @return
     */
    Order submitOrder(Map<String, String> orderInfo) throws MyException;

    /**
     * 预约成功
     * @param id
     * @return
     */
    Map<String, String> findById(Integer id);
}
