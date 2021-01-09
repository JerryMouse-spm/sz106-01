package com.itheima.health.service;

import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.exception.MyException;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.pojo.Setmeal;

import java.util.List;

public interface CheckItemService {
    /**
     * 查询所有
     * 查询检查项列表
     * @return
     */
    List<CheckItem> findAll();

    /**
     * 添加检查项
     * @param checkItem
     */
    void add(CheckItem checkItem);

    /**
     * 分页查询检查项
     * @param queryPageBean
     * @return
     */
    PageResult<CheckItem> findPage(QueryPageBean queryPageBean);

    /**
     * 根据ID查询检查项信息
     * @param id
     * @return
     */
    CheckItem findById(Integer id);

    /**
     * 修改检查项信息
     * @param checkItem
     */
    void update(CheckItem checkItem);

    /**
     * 根据ID删除检查项
     * @param id
     */
    void deleteById(Integer id) throws MyException;

}
