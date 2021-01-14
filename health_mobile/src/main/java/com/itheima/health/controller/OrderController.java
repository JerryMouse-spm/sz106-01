package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Order;
import com.itheima.health.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.sql.ResultSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/order")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private JedisPool jedisPool;

    @Reference
    private OrderService orderService;

    /**
     * 预约提交
     * @param orderInfo
     * @return
     */
    @PostMapping("/submit")
    public Result submit(@RequestBody Map<String,String> orderInfo){
        // 创建一个jedis客户端
        Jedis jedis = jedisPool.getResource();
        // 然后从Map里去手机号码拼接成key
        String key = RedisMessageConstant.SENDTYPE_ORDER + "_" + orderInfo.get("telephone");
        // 然后使用这个key在redis里匹配日看看有没有相同的key
        if (!jedis.exists(key)) {
            // 如果没有，就通知客户端重新获取验证码
            return new Result(true, "请重新获取验证码！");
        }
        // 如果有的话接着通过这个key取出redis里对应的值
        String validateCode = jedis.get(key);
        // 然后拿这个值跟客户端传过来的验证码对比
        if (!validateCode.equals(orderInfo.get("validateCode"))) {
            // 如果不相同的话就通知客户端验证码错误
            return new Result(true, "验证码有误，请重新输入！");
        }
        // 校验完没问题后查询redis中key
        jedis.del(key);
        //设置预约类型的，因客户端访问的是现在这个health_mobile，所以是微信预约
        orderInfo.put("orderType",Order.ORDERTYPE_WEIXIN);
        // 如果相同的话就调用业务层方法处理请求,把处理好的订单数据返回给客户端
        Order order = orderService.submitOrder(orderInfo);
        return new Result(true, MessageConstant.ORDER_SUCCESS,order);
    }

    @GetMapping("/findById")
    public Result findById(Integer id){
        /**
         * 调用业务层方法根据客户端需要的内容去查询
         */
        Map<String,String> map = orderService.findById(id);
        /**
         * 通知客户端结果，并返回数据
         */
        return new Result(true, MessageConstant.ORDER_SUCCESS,map);
    }
}
