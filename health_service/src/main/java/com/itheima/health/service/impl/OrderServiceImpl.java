package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.MemberDao;
import com.itheima.health.dao.OrderDao;
import com.itheima.health.dao.OrderSettingDao;
import com.itheima.health.exception.MyException;
import com.itheima.health.pojo.Member;
import com.itheima.health.pojo.Order;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.security.Key;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private OrderSettingDao orderSettingDao;

    /**
     * 预约提交
     * @param orderInfo
     * @return
     * @throws MyException
     */
    @Override
    @Transactional
    public Order submitOrder(Map<String, String> orderInfo) throws MyException {
        Set<String> strings = orderInfo.keySet();
        for (String string : strings) {
            System.out.println(string);
        }
        // 从map里取出时间，根据时间去查询看看有没有相关的预约设置
        String orderDateStr = orderInfo.get("orderDate");
        // 因为他是个字符串类型的，所以创建一个SimpleDateFormat把他转换成Date类型
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date orderDate = null;
        try {
            orderDate = sdf.parse(orderDateStr);
        } catch (ParseException e) {
            throw new MyException("日期格式转换错误");
        }
        // 然后调用Dao的方法根据日期查询对应的预约设置
        OrderSetting orderSetting = orderSettingDao.findByOrderDate(orderDate);
        // 判断这个预约设置是不是空的
        if (null != orderSetting){
            // 如果不是空的，就取出这个可预约数和已预约数相比较
            int number = orderSetting.getNumber();
            int reservations = orderSetting.getReservations();
            if (number <= reservations){
                // 如果可预约数小于等于已预约数，就证明已经预约满了，不能预约了
                throw new MyException("该日期已约满，请重新选择日期");
            }
        }else {
            // 如果为null，就委婉通知客户端让他重新选择日期
            throw new MyException("该日期非工作日，请重新选择日期");
        }
        // 日期校验完了，最终目的就是要 有设置，并且没预约满，就会来到这个位置，其他情况都会终止代码继续执行
        // 然后根据手机号码去数据库查询t_member表看看这个用户是不是会员
        String telephone = orderInfo.get("telephone");
        Member member = memberDao.findByTelephone(telephone);

        Order order = new Order();
        Integer setmealId = Integer.valueOf(orderInfo.get("setmealId"));
        order.setOrderDate(orderDate);
        order.setSetmealId(setmealId);

        // 然后判断这个会员对象是不是空的
        if (null == member){
            // 如果这个member是空的话，就证明这个用户是第一次来我们这里，自动为它添加到会员表里
            member = new Member();
            member.setName(orderInfo.get("name"));
            member.setSex(orderInfo.get("sex"));
            member.setIdCard(orderInfo.get("idCard"));
            member.setPhoneNumber(telephone);
            member.setRegTime(new Date());
            member.setPassword(orderInfo.get("idCard").substring(orderInfo.get("idCard").length()-6));
            member.setRemark("微信预约自动注册");
            // 全部属性都设置好了之后就调用Dao的添加方法，添加这个会员信息
            // 记得获取自增长的ID
            memberDao.add(member);
            order.setMemberId(member.getId());
        }else {
            // 如果这个member不等于空，那就证明，这个用户之前来过
            // 就开始判断他有没有重复预约，去查询t_order(订单表)
            Integer memberId = member.getId();

            // 利用memeberId和setmealId和用户需要预约的时间，去订单表查询，看有没有对应的数据
            order.setMemberId(memberId);

            List<Order> orderList = orderDao.findByCondition(order);
            // 查询之后得到一个orderList,然后判断这个List
            if (!CollectionUtils.isEmpty(orderList)) {
                // 如果这个orderList不等于空，就就证明重复预约了
                throw new MyException("您已经预约过了，请按时就诊");
            }
        }
        // 然后在预约设置里，往对应的预约日期的已预约人数+1,把一开始的orderSetting传过去
        //行锁，如果更新成功返回1，如果更新失败返回0
        int result = orderSettingDao.editReservationsByOrderDate(orderSetting);
        log.info("更新已预约人数是否成功:{}",result);
        if (result == 0){
            throw new MyException("该日期已约满，请重新选择日期");
        }
        // 如果这个orderList是空的就不会进到if语句，就证明没有重复预约

        // 所有条件都允许了，就开始新增订单，使用刚才的那个order补充完所有订单信息之后可以作为参数添加到t_order表
        // 记得添加的时候获取自增的ID
        order.setOrderType(orderInfo.get("orderType"));
        order.setOrderStatus(Order.ORDERSTATUS_NO);
        orderDao.add(order);
        return order;
    }

    /**
     * 根据订单ID查询预约成功数据
     * @param id
     * @return
     */
    @Override
    public Map<String, String> findById(Integer id) {
        Map map = orderDao.findById4Detail(id);
        Set set = map.keySet();
        for (Object o : set) {
            Object value = map.get(o);
            System.out.println("key:"+o+",value:"+value);
        }
        return map;
    }
}
