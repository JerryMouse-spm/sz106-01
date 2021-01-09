package com.itheima.health.exception;

/**
 * 自定义异常：
 * 友好提示
 * 区分异常类型
 * 终止已知不符合业务逻辑代码的继续执行
 */
public class MyException extends RuntimeException {
    public MyException(String message){
        super(message);
    }
}
