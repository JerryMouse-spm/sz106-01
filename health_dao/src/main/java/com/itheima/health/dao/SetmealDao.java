package com.itheima.health.dao;

import com.github.pagehelper.Page;
import com.itheima.health.pojo.Setmeal;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SetmealDao {
    /**
     * 添加套餐
     * @param setmeal
     */
    void add(Setmeal setmeal);

    /**
     * 添加套餐和检查组的关系
     * @param setmealId
     * @param CheckGroupId
     */
    void addSetmealCheckGroup(@Param("setmealId") Integer setmealId, @Param("CheckGroupId") Integer CheckGroupId);

    /**
     * 根据条件查询分页数据
     * @param queryString
     * @return
     */
    Page<Setmeal> findPageByCondition(String queryString);

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
     * 根据套餐ID删除套餐跟检查项的关系
     * @param id
     */
    void deleteSetmealCheckGroup(Integer id);

    /**
     * 修改更新套餐信息
     * @param setmeal
     */
    void update(Setmeal setmeal);

    /**
     * 根据套餐ID去订单表查询有没有关联的订单
     * @param id
     * @return
     */
    Integer findCountBySetmealId(Integer id);

    /**
     * 根据ID删除套餐
     */
    void deleteById(Integer id);

    /**
     * 查询套餐的所有图片
     * @return
     */
    List<String> findImgs();

    /**
     * 查询所有套餐列表
     * @return
     */
    List<Setmeal> getSetmeal();

    /**
     * 根据ID查询套餐相关信息
     * @param id
     * @return
     */
    Setmeal findDetailById(Integer id);

    Setmeal findDetailById2(Integer id);
}
