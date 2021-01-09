package com.itheima.health.jobs;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.service.SetmealService;
import com.itheima.health.utils.QiNiuUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CleanImgJob {

    private static final Logger log = LoggerFactory.getLogger(CleanImgJob.class);

    @Reference
    private SetmealService setmealService;

    /**
     * 定时情路垃圾图片：
     * Scheduled注解用户定义触发时机
     * initialDelay属性表示启动后延迟3000毫秒(3秒)触发
     * fixedDelay属性表示间隔1800000毫秒(30分钟)触发
     */
    @Scheduled(initialDelay = 3000,fixedDelay = 1800000)
    public void cleanImg(){
        log.info("开始执行清理垃圾图片..");
        //1.调用QiNiuUtils,查询所有图片
        List<String> imgIn7Niu = QiNiuUtils.listFile();
        log.debug("七牛总共有{}张图片",imgIn7Niu.size());
        //2.调用setmealService查询数据库的所有图片
        List<String> imgInDb = setmealService.findImgs();
        log.debug("数据库里总共有{}张图片..",imgInDb==null?0:imgInDb.size());
        //3.把七牛上的减去数据库的，剩下的就是要删除的图片
        //从7牛的list集合里移除数据库对应的数据后，7牛list集合里剩下的都是要删除的
        imgIn7Niu.removeAll(imgInDb);
        if (imgIn7Niu.size()>0){
            log.debug("要清理的垃圾图片有{}张",imgIn7Niu.size());
            //4.调用七牛工具删除垃圾图片
            QiNiuUtils.removeFiles(imgIn7Niu.toArray(new String[]{}));
        }else {
            log.debug("没有要清理的垃圾图片");
        }
        log.info("清理垃圾图片完成..");
    }
}
