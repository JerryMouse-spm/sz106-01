package com.itheima.health.dao;

import com.github.pagehelper.Page;
import com.itheima.health.pojo.CheckGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CheckGroupDao {
    /**
     * 添加检查组
     * @param checkGroup
     */
    void add(CheckGroup checkGroup);

    /**
     * 添加检查组和检查项的关系
     * (就是检查组和检查项的中间表t_checkgroud_checkitem,里面只有两个字段就是检查组ID和检查项ID)
     * @param checkGroupId
     * @param checkItemId
     */
    void addCheckGroupCheckItem(@Param("checkgroupId") Integer checkGroupId, @Param("checkitemId") Integer checkItemId);

    /**
     * 根据分页条件查询分页数据
     * @param queryString
     * @return
     */
    Page<CheckGroup> findPageByCondition(String queryString);

    /**
     * 根据ID查询用户
     * @param id
     * @return
     */
    CheckGroup findById(Integer id);

    /**
     * 根据检查组ID查询对应得检查项ID集合
     * @param id
     * @return
     */
    List<Integer> findCheckItemIdByCheckGroupId(Integer id);

    /**
     * 根据检查组ID 删除检查项和检查组的关系
     * @param checkgroupId
     */
    void deleteCheckItemCheckGroupByCheckGroudId(Integer checkgroupId);

    /**
     * 修改检查组信息
     * @param checkGroup
     */
    void update(CheckGroup checkGroup);

    /**
     * 查询所有检查组
     * @return
     */
    List<CheckGroup> findAll();

    /**
     * 根据ID删除检查组
     * @param id
     */
    void deleteById(Integer id);

    /**
     * 根据检查组ID查询有没有关联的套餐
     * @param id
     * @return
     */
    Integer findSetmealCheckGroup(Integer id);

    /**
     * 根据套餐ID查询相应的所有检查组
     * @param id
     * @return
     */
    List<CheckGroup> findCheckGroupBySetmealId(Integer id);

    /**
     * 根据套餐ID查询相应的所有检查组2
     */
    List<CheckGroup> findCheckGroupListBySetmealId(Integer id);
}
