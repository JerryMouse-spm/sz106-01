package com.itheima.health.dao;

import com.itheima.health.pojo.OrderSetting;

import java.util.Date;

public interface OrderSettingDao {
    /**
     * 通过日期查询有没有对应的预约信息
     * @param orderDate
     * @return
     */
    OrderSetting findByOrderDate(Date orderDate);

    /**
     * 修改最大预约数
     * @param os
     */
    void updateNumber(OrderSetting os);

    /**
     * 导入预约信息
     * @param os
     */
    void add(OrderSetting os);
}
