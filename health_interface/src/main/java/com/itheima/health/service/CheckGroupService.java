package com.itheima.health.service;

import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.exception.MyException;
import com.itheima.health.pojo.CheckGroup;

import java.util.List;

public interface CheckGroupService {
    /**
     * 添加检查组
     * @param checkGroup
     * @param checkitemIds
     */
    void add(CheckGroup checkGroup,Integer[] checkitemIds);

    /**
     * 分页查询检查组数据
     * @param queryPageBean
     * @return
     */
    PageResult<CheckGroup> findPage(QueryPageBean queryPageBean);

    /**
     * 根据ID查询检查组
     * @param id
     * @return
     */
    CheckGroup findById(Integer id);

    /**
     * 根据ID查询检查项
     * @param id
     * @return
     */
    List<Integer> findCheckItemIdByCheckGroupId(Integer id);


    /**
     * 修改检查组信息
     * @param checkGroup
     * @param checkitemIds
     */
    void update(CheckGroup checkGroup,Integer[] checkitemIds);

    /**
     * 查询所有检查组
     * @return
     */
    List<CheckGroup> findAll();

    /**
     * 根据ID删除检查组
     * @param id
     */
    void deleteById(Integer id) throws MyException;
}
