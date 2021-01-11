package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealService;
import com.itheima.health.utils.QiNiuUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Reference
    private SetmealService setmealService;

    @GetMapping("/getSetmeal")
    public Result getSetmeal(){
        System.out.println("接收到了请求");
        /**
         * 调用业务层处理请求
         */
        List<Setmeal> setmealList = setmealService.getSetmeal();
        /**
         * 因为从数据库查出来的数据图片是只有图片名，客户端需要完整图片路径来展示图片
         * 遍历取出每个套餐信息，设置他的img属性，拼接成完整路径再返回
         */
        setmealList.forEach(setmeal -> setmeal.setImg(QiNiuUtils.DOMAIN+setmeal.getImg()));

        return new Result(true, MessageConstant.QUERY_SETMEALLIST_SUCCESS,setmealList);
    }

    /**
     * 根据套餐ID查询套餐相关信息
     * @param id
     * @return
     */
    @GetMapping("/findDetailById")
    public Result findDetailById(Integer id){
        /**
         * 调用业务层查询相关信息，得到套餐所有相关信息
         */
        Setmeal setmeal = setmealService.findDetailById(id);
        /**
         * 因为从数据库查出来的数据图片是只有图片名，客户端需要完整图片路径来展示图片
         * 遍历取出每个套餐信息，设置他的img属性，拼接成完整路径再返回
         */
        setmeal.setImg(QiNiuUtils.DOMAIN+setmeal.getImg());
        /**
         * 通知客户端结果并返回数据
         */
        return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
    }

    @GetMapping("/findDetailById2")
    public Result findDetailById2(Integer id) {
        /**
         * 调用业务层查询相关信息，得到套餐所有相关信息
         */
        Setmeal setmeal = setmealService.findDetailById2(id);
        /**
         * 因为从数据库查出来的数据图片是只有图片名，客户端需要完整图片路径来展示图片
         * 遍历取出每个套餐信息，设置他的img属性，拼接成完整路径再返回
         */
        setmeal.setImg(QiNiuUtils.DOMAIN + setmeal.getImg());
        /**
         * 通知客户端结果并返回数据
         */
        return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS, setmeal);
    }

    @GetMapping("/findDetailById3")
    public Result findDetailById3(Integer id) {
        /**
         * 调用业务层查询相关信息，得到套餐所有相关信息
         */
        Setmeal setmeal = setmealService.findDetailById3(id);
        /**
         * 因为从数据库查出来的数据图片是只有图片名，客户端需要完整图片路径来展示图片
         * 遍历取出每个套餐信息，设置他的img属性，拼接成完整路径再返回
         */
        setmeal.setImg(QiNiuUtils.DOMAIN + setmeal.getImg());
        /**
         * 通知客户端结果并返回数据
         */
        return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS, setmeal);
    }
}
