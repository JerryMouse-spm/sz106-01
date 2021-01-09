package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.service.CheckGroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/checkgroup")
public class CheckGroupController {
    /**
     * 业务层对象，注意注解一定要alibaba的包,否则为null
     */
    @Reference
    private CheckGroupService checkGroupService;

    /**
     * 添加检查组：
     * 参数CheckGroup使用RequestBody注解是因为他是通过请求体发送过来的参数
     * 参数checkitemIds是通过url上key=value的方式传过来的，接收参数的属性名必须和key一致，否则就使用RequestParam注解
     * @param checkGroup
     * @param checkitemIds
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody CheckGroup checkGroup,Integer[] checkitemIds){
        /**
         * 调用业务层方法处理请求
         */
        checkGroupService.add(checkGroup,checkitemIds);
        /**
         * 通知客户端处理结果
         */
        return new Result(true, MessageConstant.ADD_CHECKGROUP_SUCCESS);
    }

    /**
     * 分页查询检查组数据
     * @param queryPageBean
     * @return
     */
    @PostMapping("/findPage")
    public Result findPage(@RequestBody QueryPageBean queryPageBean){
        /**
         * 调用业务层方法处理请求
         * 把分页条件数据传过去
         */
        PageResult<CheckGroup> pageResult = checkGroupService.findPage(queryPageBean);
        /**
         * 通知客户端结果并返回数据
         */
        return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS,pageResult);
    }

    @GetMapping("/findById")
    public Result findById(Integer id){
        /**
         * 调用业务层方法查询检查组信息，得到一个CheckGroup
         */
        CheckGroup checkGroup = checkGroupService.findById(id);
        /**
         * 通知客户端并返回数据
         */
        return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkGroup);
    }

    @GetMapping("/findCheckItemIdByCheckGroupId")
    public Result findCheckItemIdByCheckGroupId(Integer id){
        /**
         * 调用业务层方法根据检查组ID查询对应的检查项ID集合
         */
        List<Integer> checkitemIds = checkGroupService.findCheckItemIdByCheckGroupId(id);
        /**
         * 通知客户端结果并返回数据
         */
        return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS,checkitemIds);
    }

    @PostMapping("/update")
    public Result update(@RequestBody CheckGroup checkGroup,Integer[] checkitemIds){
        /**
         * 调用业务层方法，修改检查组相关信息
         */
        checkGroupService.update(checkGroup,checkitemIds);
        /**
         * 通知客户端修改结果
         */
        return new Result(true, MessageConstant.EDIT_CHECKGROUP_SUCCESS);
    }

    @GetMapping("/findAll")
    public Result findAll(){
        /**
         * 调用业务层方法处理请求
         */
        List<CheckGroup> list = checkGroupService.findAll();
        /**
         * 通知客户端查询结果并返回数据
         */
        return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS,list);
    }

    @PostMapping("/deleteById")
    public Result deleteById(Integer id){
        /**
         * 调用业务层处理请求
         */
        checkGroupService.deleteById(id);
        /**
         * 返回处理结果
         */
        return new Result(true, MessageConstant.DELETE_CHECKGROUP_SUCCESS);
    }
}
