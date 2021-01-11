package com.itheima.health.service;

import com.itheima.health.exception.MyException;
import com.itheima.health.pojo.OrderSetting;

import java.util.List;
import java.util.Map;

public interface OrderSettingService {
    /**
     * 批量导入预约设置
     * @param orderSettingList
     */
    void addBatch(List<OrderSetting> orderSettingList) throws MyException;

    /**
     * 根据月份查询当月的预约信息
     * @param month
     * @return
     */
    List<Map<String,Object>> getOrderSettingByMonth(String month);

    /**
     * 根据日期修改可预约数量
     * @param orderSetting
     */
    void editNumberByDate(OrderSetting orderSetting);
}
