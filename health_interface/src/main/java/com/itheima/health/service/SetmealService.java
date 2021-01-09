package com.itheima.health.service;

import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.exception.MyException;
import com.itheima.health.pojo.Setmeal;

import java.util.List;

public interface SetmealService {
    /**
     * 添加套餐和相关信息
     * @param setmeal
     * @param checkgroupIds
     */
    void add(Setmeal setmeal, Integer[] checkgroupIds);

    /**
     * 根据条件查询分页数据
     * @param queryPageBean
     * @return
     */
    PageResult<Setmeal> findPage(QueryPageBean queryPageBean);

    /**
     * 根据ID查询套餐信息
     * @param id
     * @return
     */
    Setmeal findById(Integer id);

    /**
     * 根据套餐ID通过t_setmeal_checkgroup中间表查询所有关联的检查组ID
     * @param id
     * @return
     */
    List<Integer> findCheckGroupIdBySetmealId(Integer id);

    /**
     * 修改套餐信息
     * @param setmeal
     * @param checkgroupIds
     */
    void update(Setmeal setmeal,Integer[] checkgroupIds);

    /**
     * 根据ID删除套餐和套餐相关信息
     * @param id
     */
    void deleteById(Integer id) throws MyException;

    /**
     * 查询套餐的所有图片
     * @return
     */
    List<String> findImgs();
}
