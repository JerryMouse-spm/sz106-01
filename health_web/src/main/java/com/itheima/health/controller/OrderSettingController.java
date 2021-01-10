package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderSettingService;
import com.itheima.health.utils.POIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ordersetting")
public class OrderSettingController {

    private static final Logger log = LoggerFactory.getLogger(OrderSettingController.class);
    @Reference
    private OrderSettingService orderSettingService;

    @PostMapping("/upload")
    public Result upload(MultipartFile excelFile){
        try {
            //调用POI工具类解析Excel文件，得到一个List<String[]>
            //List里面的每个String数组代表着Excel文件的每一行
            List<String[]> excelData = POIUtils.readExcel(excelFile);
            log.debug("导入预约设置读取到了{}条记录",excelData.size());
            //每个String数组有两个值，索引为0的是日期，索引为1的数量
            //(在Excel文件的每一行每一列的第一个单元格索引都为0)
            //把这个List<String[]>通过流的方式转换成List<OrderSetting>
            //转换之后再调用业务层方法处理，这样做的目的只是为了见名知意
            //创建一个日期格式化类用于转换格式，因为从文件里取出来的日期是字符串，所以要进行转换
            final SimpleDateFormat sdf = new SimpleDateFormat(POIUtils.DATE_FORMAT);
            List<OrderSetting> orderSettingList = excelData.stream().map(arr -> {
                OrderSetting orderSetting = new OrderSetting();
                try {
                    //设置日期
                    orderSetting.setOrderDate(sdf.parse(arr[0]));
                    //设置数量
                    orderSetting.setNumber(Integer.valueOf(arr[1]));
                } catch (ParseException e) {
                    //蒙掉它
                }
                return orderSetting;
            }).collect(Collectors.toList());
            //调用业务层方法进行添加预约数据
            orderSettingService.addBatch(orderSettingList);
            //如果没出错的话就通知客户端导入成功
            return new Result(true, MessageConstant.IMPORT_ORDERSETTING_SUCCESS);
        } catch (IOException e) {
            log.error("导入预约数据失败",e);
            return new Result(false, MessageConstant.IMPORT_ORDERSETTING_FAIL);
        }
    }
}
