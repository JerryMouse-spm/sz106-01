package com.itheima.health.controller;

import com.itheima.health.entity.Result;
import com.itheima.health.exception.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



/**
 * 自定义异常的统一处理
 */
@RestControllerAdvice
public class MyExceptionAdvice {

    /**
     * 定一个常量，记录MyExceptionAdvice的日志信息
     */
    private static final Logger log = LoggerFactory.getLogger(MyExceptionAdvice.class);

    /**
     * 处理业务异常
     * @param e
     * @return
     */
    @ExceptionHandler(MyException.class)
    public Result handlerMyException(MyException e){
        return new Result(false,e.getMessage());
    }

    /**
     * 处理未知异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result handlerException(Exception e){
        //记录异常信息
        log.error("发生未知异常",e);
        return new Result(false,"发生未知异常，请联系管理员！");
    }
}
