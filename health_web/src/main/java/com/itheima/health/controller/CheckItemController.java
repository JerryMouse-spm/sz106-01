package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.Page;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.service.CheckItemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RestController注解表示这个类的所有方法的返回值都是返回json数据
 */
@RestController
@RequestMapping("/checkitem")
public class CheckItemController {
    /**
     * 使用alibaba的Reference注解，获取到Service的动态代理对象
     */
    @Reference
    private CheckItemService checkItemService;

    @GetMapping("/findAll")
    public Result findAll(){
        /**
         * 调用业务层方法得到一个检查项集合
         */
        List<CheckItem> checkItemList = checkItemService.findAll();
        /**
         * 封装数据，返回给客户端
         */
        return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkItemList);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('CHECKITEM_ADD')")
    public Result add(@RequestBody CheckItem checkItem){
        /**
         * 调用业务层方法处理请求
         */
        checkItemService.add(checkItem);
        /**
         * 把结果返回给客户端
         */
        return new Result(true, MessageConstant.ADD_CHECKITEM_SUCCESS);
    }

    /**
     * 分页查询检查项
     * @param queryPageBean
     * @return
     */
    @PostMapping("/findPage")
    public Result findPage(@RequestBody QueryPageBean queryPageBean){
        /**
         * 调用业务层对象处理分页数据
         */
        PageResult<CheckItem> pageResult = checkItemService.findPage(queryPageBean);
        /**
         * 返回客户端查询结果
         */
        return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS,pageResult);
    }

    /**
     * 根据ID查询检查项信息
     * @param id
     * @return
     */
    @GetMapping("/findById")
    public Result findById(Integer id){
        /**
         * 调用业务层方法根据ID查询出对应的检查项
         */
        CheckItem checkItem = checkItemService.findById(id);
        /**
         * 通知客户端结果并返回数据
         */
        return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS,checkItem);
    }

    @PostMapping("/update")
    public Result update(@RequestBody CheckItem checkItem){
        /**
         * 调用业务层方法修改检查项信息
         */
        checkItemService.update(checkItem);
        /**
         * 通知客户端结果
         */
        return new Result(true, MessageConstant.EDIT_CHECKITEM_SUCCESS);
    }

    @PostMapping("/deleteById")
    public Result deleteById(Integer id){
        /**
         * 调用业务层方法处理请求
         */
        checkItemService.deleteById(id);
        /**
         * 通知客户端处理结果
         */
        return new Result(true, MessageConstant.DELETE_CHECKITEM_SUCCESS);
    }
}
