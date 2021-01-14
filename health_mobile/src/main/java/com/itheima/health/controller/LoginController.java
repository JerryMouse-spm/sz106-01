package com.itheima.health.controller;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Member;
import com.itheima.health.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private JedisPool jedisPool;

    /**
     * 业务层
     */
    @Reference
    private MemberService memberService;

    @PostMapping("/check")
    public Result check (@RequestBody Map<String,String> loginInfo){
        //首先取出手机号码。拼接成redis中的key
        String telephone = loginInfo.get("telephone");
        String key = RedisMessageConstant.SENDTYPE_LOGIN + "_" + telephone;
        log.info("拼接完成后的key:{}",key);
        // 然后创建一个redis客户端
        Jedis jedis = jedisPool.getResource();
        // 然后通过这个key去redis里查找有没有对应的key
        String vailDateCode = jedis.get(key);
        log.info("通过key查找的值为:{}",vailDateCode);
        // 然后判断这个为不为null
        if (StringUtils.isEmpty(vailDateCode)){
            // 如果这个key为null,那就说明验证码没发送成功，或者验证码已过期
            return new Result(false, "请重新获取验证码");
        }
        // 如果不为null,那就比较这两个redis里查出来的和客户端传过来的，是否相同
        if (vailDateCode.equals(loginInfo.get("vailDateCode"))){
            // 如果不相同那就抛出异常，验证码不正确
            return new Result(false, "验证码错误，请重新输入");
        }
        // 验证完毕，删除redis中的key
        jedis.del(key);
        // 如果相同，验证码就校验完毕
        // 然后根据手机号码去数据库查询t_member表，看看表里有没有对应的用户
        Member member = memberService.findByTelePhone(telephone);
        // 查询会返回一个member对象，判断这个对象为不为空
        if (null == member){
            // 如果不是会员，就添加到会员，添加之前，把我们所知道的信息设置到member中
            member = new Member();
            member.setRemark("手机快速登录");
            member.setRegTime(new Date());
            member.setPhoneNumber(telephone);
            memberService.add(member);
        }
        // 把更新好的用户存到Cookie，进行Cookie跟踪
        Cookie cookie = new Cookie("login_member_telephoe",telephone);
        // 设置cookie的声明周期
        cookie.setMaxAge(30*24*60*60);
        // 设置Cookie的路径为“/”，这样只要你登录之后，在整个项目里，不管你去到那里，都会带着Cookie
        // 通知客户端，返回结构
        return new Result(true, MessageConstant.LOGIN_SUCCESS);
    }
}
