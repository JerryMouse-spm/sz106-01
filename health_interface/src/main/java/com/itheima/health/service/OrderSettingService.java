package com.itheima.health.service;

import com.itheima.health.exception.MyException;
import com.itheima.health.pojo.OrderSetting;

import java.util.List;

public interface OrderSettingService {
    /**
     * 批量导入预约设置
     * @param orderSettingList
     */
    void addBatch(List<OrderSetting> orderSettingList) throws MyException;
}
