package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.OrderSettingDao;
import com.itheima.health.exception.MyException;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderSettingService.class)
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    private OrderSettingDao orderSettingDao;

    /**
     * 批量导入预约数据
     * 注意事务控制
     * @param orderSettingList
     */
    @Override
    @Transactional
    public void addBatch(List<OrderSetting> orderSettingList) throws MyException {
        //先判断这个预约数据不为空再作处理
        if (!CollectionUtils.isEmpty(orderSettingList)){
            //如果它不是空的就进行遍历
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (OrderSetting os : orderSettingList) {
                //然后取出它的日期，去数据库查询有没有相同日期的预约信息
                OrderSetting orderSetting = orderSettingDao.findByOrderDate(os.getOrderDate());
                //然后进行判断，如果没有预约信息就直接调用Dao添加预约数据
                if (orderSetting == null) {
                    orderSettingDao.add(os);
                }else {
                    //如果已经有了预约数据
                    //取出已预约人数
                    int reservations = orderSetting.getReservations();
                    //取出最大预约数
                    int number = os.getNumber();
                    //首先判断已经预约的人数是否大于要更新的最大预约数
                    if (reservations > number){
                        //判断，如果大于预约数的话就报错
                        throw new MyException(sdf.format(os.getOrderDate())+"：最大预约数不能小于预约人数");
                    }else {
                        //如果小于最大预约数的话，就可以了更新最大预约数
                        orderSettingDao.updateNumber(os);
                    }
                }
            }
        }
    }

    /**
     * 根据月份查询当月预约信息
     * @param month
     * @return
     */
    @Override
    public List<Map<String, Object>> getOrderSettingByMonth(String month) {
        month+="%";
        return orderSettingDao.getOrderSettingByMonth(month);
    }

    /**
     * 根据日期设置可预约数
     * @param orderSetting
     */
    @Override
    @Transactional
    public void editNumberByDate(OrderSetting orderSetting) {
        //日期格式化
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //然后取出它的日期，去数据库查询有没有相同日期的预约信息
        OrderSetting order = orderSettingDao.findByOrderDate(orderSetting.getOrderDate());
        //然后进行判断，如果没有预约信息就直接调用Dao添加预约数据
        if (order == null) {
            orderSettingDao.add(orderSetting);
        } else {
            //如果已经有了预约数据
            //取出已预约人数
            int reservations = order.getReservations();
            //取出最大预约数
            int number = orderSetting.getNumber();
            //首先判断已经预约的人数是否大于要更新的最大预约数
            if (reservations > number) {
                //判断，如果大于预约数的话就报错
                throw new MyException(sdf.format(orderSetting.getOrderDate()) + "：最大预约数不能小于已预约人数");
            } else {
                //如果小于最大预约数的话，就可以了更新最大预约数
                orderSettingDao.updateNumber(orderSetting);
            }
        }
    }

}
