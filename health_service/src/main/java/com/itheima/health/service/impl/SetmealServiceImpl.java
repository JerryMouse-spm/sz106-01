package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.dao.CheckGroupDao;
import com.itheima.health.dao.CheckItemDao;
import com.itheima.health.dao.SetmealDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.exception.MyException;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealService;
import org.apache.zookeeper.data.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(interfaceClass = SetmealService.class)
public class SetmealServiceImpl implements SetmealService {
    /**
     * 业务层对象
     */
    @Autowired
    private SetmealDao setmealDao;
    @Autowired
    private CheckGroupDao checkGroupDao;
    @Autowired
    private CheckItemDao checkItemDao;
    /**
     * 添加套餐和其相关信息
     * Transactional注解控制事务
     * @param setmeal
     * @param checkgroupIds
     */
    @Override
    @Transactional
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        /**
         * 先添加套餐
         */
        setmealDao.add(setmeal);
        /**
         * 或者添加套餐后自增的ID
         */
        Integer setmealId = setmeal.getId();
        /**
         * 判断如果检查组ID数组不为空，就遍历逐个跟套餐ID关联
         */
        if (null != checkgroupIds){
            for (Integer checkgroupId : checkgroupIds) {
                setmealDao.addSetmealCheckGroup(setmealId,checkgroupId);
            }
        }
    }

    @Override
    public PageResult<Setmeal> findPage(QueryPageBean queryPageBean) {
        /**
         * 判断传过来的每页条数有没有超过50,如果大于等于50就给它50，如果少于50就使用当前的值
         */
        Integer PageSize = queryPageBean.getPageSize() > 50 ? 50 : queryPageBean.getPageSize();
        /**
         * 然后使用PageHelper的API提供给它查询页码和每页条数，它底层会自动去查询数据的量
         */
        PageHelper.startPage(queryPageBean.getCurrentPage(),PageSize);
        /**
         * 然后判断有没有查询条件
         * 如果有查询条件就把它拼接成sql语句需要的格式，到时候用上了直接调用
         */
        if (queryPageBean.getQueryString() != null){
            queryPageBean.setQueryString("%"+queryPageBean.getQueryString()+"%");
        }
        /**
         * 调用业务层方法按照条件去查询数据
         */
        Page<Setmeal> page = setmealDao.findPageByCondition(queryPageBean.getQueryString());
        /**
         * 因为Controller需要的是PageResult对象，所以把页面需要的数据封装到PageResult并返回
         */
        PageResult<Setmeal> pageResult = new PageResult<>(page.getTotal(),page.getResult());
        return pageResult;
    }

    /**
     * 根据ID查询套餐信息
     * @param id
     * @return
     */
    @Override
    public Setmeal findById(Integer id) {
        /**
         * 调用业务层方法按照条件去查询数据
         */
        return setmealDao.findById(id);
    }

    /**
     * 根据套餐ID通过t_setmeal_checkgroup中间表查询所有关联的检查组ID
     * Transactional注解事务控制
     * @param id
     * @return
     */
    @Override
    @Transactional
    public List<Integer> findCheckGroupIdBySetmealId(Integer id) {
        /**
         * 调用业务层方法按照条件去查询数据
         */
        return setmealDao.findCheckGroupIdBySetmealId(id);
    }

    /**
     * 修改套餐信息
     * @param setmeal
     * @param checkgroupIds
     */
    @Override
    public void update(Setmeal setmeal, Integer[] checkgroupIds) {
        /**
         * 首先更新套餐信息
         */
        setmealDao.update(setmeal);
        /**
         * 然后根据套餐的ID删除之前该套餐关联的所有检查组
         */
        setmealDao.deleteSetmealCheckGroup(setmeal.getId());
        /**
         * 然后判断客户端有没有新的关联检查组，如果有的话就逐一遍历出来，加上套餐的ID一起传过去Dao层添加关联
         */
        if (null != checkgroupIds) {
            for (Integer checkgroupId : checkgroupIds) {
                setmealDao.addSetmealCheckGroup(setmeal.getId(),checkgroupId);
            }
        }
    }

    /**
     * 根据ID删除套餐和套餐相关信息
     * @param id
     * @throws MyException
     */
    @Override
    @Transactional
    public void deleteById(Integer id) throws MyException {
        /**
         * 先去数据库查询有没有关联的订单
         */
        Integer count = setmealDao.findCountBySetmealId(id);
        /**
         * 判断有没有关联的订单
         */
        if (count > 0){
            /**
             * 如果有关联的订单就终止运行，抛出通知不饿能删除
             */
            throw new MyException("该套餐有订单在进行中，请处理后再操作");
        }
        /**
         * 如果没有关联的订单就删除关联该套餐的检查组,然后再删除该套餐
         */
        setmealDao.deleteSetmealCheckGroup(id);
        setmealDao.deleteById(id);
    }

    /**
     * 查询套餐的所有图片
     * @return
     */
    @Override
    public List<String> findImgs() {
        return setmealDao.findImgs();
    }

    @Override
    public List<Setmeal> getSetmeal() {
        /**
         * 调用Dao获取所有套餐信息
         */
        return setmealDao.getSetmeal();
    }

    /**
     * 根据套餐ID查询套餐相关信息
     * @param id
     * @return
     */
    @Override
    public Setmeal findDetailById(Integer id) {
        return setmealDao.findDetailById(id);
    }

    @Override
    public Setmeal findDetailById2(Integer id) {
        return setmealDao.findDetailById2(id);
    }

    @Override
    public Setmeal findDetailById3(Integer id) {
        // 调用业务层根据ID查询到Setmeal对象
        Setmeal setmeal = setmealDao.findById(id);
        // 判断查询出来的setmeal是不是空
        if (setmeal != null){
            // 然后根据Setmeal对象ID查询出所有的检查组CheckGroup
            List<CheckGroup> checkGroupList = checkGroupDao.findCheckGroupListBySetmealId(id);
            // 判断查询出来的checkGroupList不为null且长度大于0
            if (checkGroupList != null && checkGroupList.size() > 0){
                // 遍历出每个检查组，然后根据检查组ID去查询对应的检查项
                // 并把查询到的检查项赋值到CheckGroup的属性
                for (CheckGroup checkGroup : checkGroupList) {
                    List<CheckItem> checkItemList = checkItemDao.findCHeckItemListByCheckGroupId(checkGroup.getId());
                    checkGroup.setCheckItems(checkItemList);
                }
            }
            //把“属性加满”的检查组赋值到setmeal的属性并返回setmeal给Controller
            setmeal.setCheckGroups(checkGroupList);
        }

        return setmeal;
    }


}
