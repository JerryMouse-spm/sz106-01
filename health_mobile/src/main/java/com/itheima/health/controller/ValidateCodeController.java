package com.itheima.health.controller;

import com.aliyuncs.exceptions.ClientException;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.exception.MyException;
import com.itheima.health.utils.SMSUtils;
import com.itheima.health.utils.ValidateCodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {

    private static final Logger log = LoggerFactory.getLogger(ValidateCodeController.class);

    @Autowired
    private JedisPool jedisPool;

    /**
     * 发送验证码
     * @param telephone
     * @return
     */
    @PostMapping("/send4Order")
    public Result send4Order(String telephone){
        // 创建一个redis客户端
        Jedis jedis = jedisPool.getResource();
        // 然后把客户的传进来的手机号码拼接成指定的格式(为了方便去redis里查找对应的key，例如以下的为：001_客户端传进来的手机号码)
        String key = RedisMessageConstant.SENDTYPE_ORDER + "_" + telephone;
        // 然后通过这个key去redis里面匹配看看有没有对应key,找到就是true没找到就是false
        if (jedis.exists(key)) {
            //如果能找到，就证明这个手机号码已经发过短信了，抛出异常信息通知客户端
            return new Result(false, "请勿频繁发送验证码，请留意短信查收");
        }
        // 如果没找到对应的key，证明没有发送过或者验证码已过期, 就重新发送
        // 首先随机生成一个指定长度为6位数的验证码
        Integer validateCode = ValidateCodeUtils.generateValidateCode(6);
//        try {
//            // 然后调用SMSUtils工具类发送验证码到指定的手机号码，传三个参数一个模板一个手机号码一个验证码
//            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,validateCode+"");
//        } catch (ClientException e) {
//            log.error("发送验证码失败！{},{}",telephone,validateCode);
//            // 如果出现异常就通知客户端端发送失败
//            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
//        }
        // 如果没有出现异常就证明验证码发送成功了，就把这个key和这个随机生成的验证码存储到redis里,定时10分钟(注意第二个参数单位为”秒钟“)
        jedis.setex(key,10*60,validateCode+"");
        jedis.close();
        // 通知客户端发送成功
        return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }

    /**
     * 登录发送验证码
     * (中间省略了云短信发送，比较上个方法即可看出)
     * @param telephone
     * @return
     */
    @PostMapping("/send4Login")
    public Result send4Login(String telephone){
        // 创建一个redis客户端
        Jedis jedis = jedisPool.getResource();
        // 然后把客户端传过来的手机号码拼接成key
        String key = RedisMessageConstant.SENDTYPE_LOGIN +"_"+ telephone;
        // 然后根据这个key去redis里匹配看看有没有对应的key
        if (jedis.exists(key)){
            // 如果能查询到就证明已经发送过验证码，通知客户端不要频繁发送
            return new Result(true, "请勿频繁获取验证码，留意短信通知");
        }
        // 如果没有查到对应的key
        // 首先就获取一个随机的指定长度的验证码
        Integer validateCode = ValidateCodeUtils.generateValidateCode(4);
        //然后把这个随机获取到的验证码存到redis里，定时有效期10分钟
        jedis.setex(key,10*60,validateCode+"");
        // 然后释放资源
        jedis.close();
        // 通知客户端发送成功
        return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }
}
