package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealService;
import com.itheima.health.utils.QiNiuUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    /**
     * 定义一个常量，打印SetmealController的日志
     */
    private static final Logger log = LoggerFactory.getLogger(SetmealController.class);
    /**
     * 业务层对象
     */
    @Reference
    private SetmealService setmealService;

    /**
     * 上传文件
     * @param imgFile
     * @return
     */
    @PostMapping("/upload")
    public Result upload(MultipartFile imgFile){
        //1.获取源文件名
        String originalFilename = imgFile.getOriginalFilename();
        //2.截取源文件的后缀名
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        //3.生成唯一ID
        String soleId = UUID.randomUUID().toString();
        //4.ID+后缀名拼接成唯一文件名
        String fileName = soleId + substring;
        try {
            //5.调用七牛工具上传图片,传入一个图片文件的字节流和文件名
            QiNiuUtils.uploadViaByte(imgFile.getBytes(),fileName);
            //6.构造返回数据 页面需要文件名和七牛域名，可以把数据封装到map返回页面 initialCapacity是初始容量
            Map<String,String> map = new HashMap<>(2);
            //文件名
            map.put("imgName",fileName);
            //七牛域名
            map.put("domain",QiNiuUtils.DOMAIN);
            return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS,map);
        } catch (IOException e) {
            log.error("上传图片失败",e);
            //通知客户端上传失败
            return new Result(true, MessageConstant.PIC_UPLOAD_FAIL);
        }
    }

    @PostMapping("/add")
    public Result add(@RequestBody Setmeal setmeal,Integer[] checkgroupIds){
        /**
         * 调用业务层方法添加数据
         */
        setmealService.add(setmeal,checkgroupIds);
        /**
         * 通知客户端处理结果
         */
        return new Result(true, MessageConstant.ADD_SETMEAL_SUCCESS);
    }

    @PostMapping("/findPage")
    public Result findPage(@RequestBody QueryPageBean queryPageBean){
        /**
         * 调用业务层的方法
         */
        PageResult<Setmeal> pageResult = setmealService.findPage(queryPageBean);
        /**
         * 通知客户端结果，并返回数据
         */
        return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS,pageResult);
    }

    /**
     * 根据ID查询套餐信息
     * @param id
     * @return
     */
    @GetMapping("/findById")
    public Result findById(Integer id){
        /**
         * 调用业务层方法根据ID查询到一个套餐信息
         */
        Setmeal setmeal = setmealService.findById(id);
        /**
         * 因为页面需要拼接完成路径才能回显图片，所以把千牛的域名也返回给页面
         * 把setmeal和千牛域名存到一个map返回
         */
        Map<String,Object> map = new HashMap<>();
        map.put("setmeal",setmeal);
        map.put("domain",QiNiuUtils.DOMAIN);
        return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS,map);
    }

    /**
     * 查询套餐的相关信息(用于回显)
     * @param id
     * @return
     */
    @GetMapping("/findCheckGroupIdBySetmealId")
    public Result findCheckGroupIdBySetmealId(Integer id){
        /**
         * 调用业务层的方法
         */
        List<Integer> checkgroupIds = setmealService.findCheckGroupIdBySetmealId(id);
        /**
         * 通知客户端结果，并返回数据
         */
        return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkgroupIds);
    }

    /**
     * 修改更新套餐信息
     * @param setmeal
     * @param checkgroupIds
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody Setmeal setmeal,Integer[] checkgroupIds){
        /**
         * 调用业务层方法更新套餐数据
         */
        setmealService.update(setmeal,checkgroupIds);
        /**
         * 通知客户端处理结果
         */
        return new Result(true, MessageConstant.EDIT_SETMEAL_SUCCESS);
    }

    @PostMapping("deleteById")
    public Result deleteById(Integer id){
        /**
         * 调用业务层方法删除该套餐和该套餐的相关信息
         */
        setmealService.deleteById(id);
        /**
         * 通知客户端处理结果
         */
        return new Result(true, MessageConstant.DELETE_SETMEAL_SUCCESS);
    }
}
