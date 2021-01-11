package com.itheima.health.dao;

import com.github.pagehelper.Page;
import com.itheima.health.pojo.CheckItem;

import java.util.List;

public interface CheckItemDao {
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
     * 查询分页检查项数据
     * @param queryString
     * @return
     */
    Page<CheckItem> findByCondtion(String queryString);

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
     * 根据检查项ID查询有多少个关联的检查组
     * @param id
     * @return
     */
    Integer findCountByCheckItemId(Integer id);

    /**
     * 根据ID删除检查项
     * @param id
     */
    void deleteById(Integer id);

    /**
     * 根据检查组ID查询对应的所有检查项
     * @param id
     * @return
     */
    List<CheckItem> findCheckItemByCheckGroupId(Integer id);

    /**
     * 根据检查组ID查询对应的所有检查项
     * @param id
     * @return
     */
    List<CheckItem> findCHeckItemListByCheckGroupId(Integer id);
}
