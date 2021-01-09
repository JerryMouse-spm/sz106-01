package com.itheima.health;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class ServiceApplication {
    public static void main(String[] args) throws IOException {
        /**
         * 读取配置文件加载核心容器
         */
        new ClassPathXmlApplicationContext("classpath:spring-service.xml");
        /**
         * 不要让程序关闭，确保服务器是在运行状态
         */
        System.in.read();
    }
}
