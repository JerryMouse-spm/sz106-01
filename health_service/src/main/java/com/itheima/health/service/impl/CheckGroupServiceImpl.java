package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.dao.CheckGroupDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.exception.MyException;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(interfaceClass = CheckGroupService.class)
public class CheckGroupServiceImpl implements CheckGroupService {
    /**
     * Dao层对象
     */
    @Autowired
    private CheckGroupDao checkGroupDao;


    /**
     * 添加检查组
     * Transactional注解添加事务控制
     * @param checkGroup
     * @param checkitemIds
     */
    @Override
    @Transactional
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        /**
         * 先添加检查组
         */
        checkGroupDao.add(checkGroup);
        /**
         * 获取添加检查组自增的ID
         */
        Integer checkGroupId = checkGroup.getId();
        /**
         * 判断客户端有没有选中检查项ID
         */
        if (checkitemIds != null){
            /**
             * 调用Dao层方法添加两个表之间的关系(添加检查组和检查项中间表t_checkgroup_checkitem)
             * 把选中的检查项ID遍历出来，每个ID都和检查组ID一起作为参数传到业务层
             */
            for (Integer checkitemId : checkitemIds) {

                checkGroupDao.addCheckGroupCheckItem(checkGroupId,checkitemId);

            }
        }
    }

    /**
     * 分页查询检查组数据
     * @param queryPageBean
     * @return
     */
    @Override
    public PageResult<CheckGroup> findPage(QueryPageBean queryPageBean) {
        /**
         * 如果每页条数大于等于50,就个它50,如果小于50就取当前的值
         */
        int pageSize = queryPageBean.getPageSize() > 50 ? 50 : queryPageBean.getPageSize();
        /**
         * 然后使用PageHelper的API设置分页数据
         * 它会把这个数据设置到一个ThreadLocal(线程本地存储，存在里面的数据只供当前线程使用)
         */
        PageHelper.startPage(queryPageBean.getCurrentPage(),pageSize);
        /**
         * 判断有没有查询条件，如果有的话就拼接成sql语句需要用到的格式
         */
        if (queryPageBean.getQueryString() != null){
            queryPageBean.setQueryString("%"+queryPageBean.getQueryString()+"%");
        }
        /**
         * 调用持久层方法根据分页条件查询分页数据
         */
        Page<CheckGroup> list = checkGroupDao.findPageByCondition(queryPageBean.getQueryString());
        /**
         * 把查询到的数据总量值(total)和检查组集合返回给Controller
         * 因为Controller需要一个PageResult对象，new一个Result对象，把客户端的需要数据存进去并返回
         */
        PageResult<CheckGroup> pageResult = new PageResult<>(list.getTotal(),list.getResult());
        return pageResult;
    }

    /**
     * 根据ID查询用户
     * @param id
     * @return
     */
    @Override
    public CheckGroup findById(Integer id) {
        /**
         * 调用业务层方法根据ID查询检查组信息并返回Controller
         */
        return checkGroupDao.findById(id);
    }

    /**
     *根据检查组ID查询对应的检查项ID集合
     * @param id
     * @return
     */
    @Override
    public List<Integer> findCheckItemIdByCheckGroupId(Integer id) {
        /**
         * 调用业务层方法根据检查组ID查询检查项ID集合并返回
         */
        return checkGroupDao.findCheckItemIdByCheckGroupId(id);
    }

    /**
     * 修改检查组信息
     * Transactional注解添加事务
     * @param checkGroup
     */
    @Override
    @Transactional
    public void update(CheckGroup checkGroup,Integer[] checkitemIds) {
        /**
         * 首先修改检查组列表
         */
        checkGroupDao.update(checkGroup);
        /**
         * 根据ID删除检查表和检查项原有的关系
         */
        checkGroupDao.deleteCheckItemCheckGroupByCheckGroudId(checkGroup.getId());
        /**
         * 判断客户端有没有选择的检查项
         */
        if (checkitemIds != null){
            /**
             * 然后遍历检查项ID逐个和检查组的ID关联起来
             */
            for (Integer checkitemId : checkitemIds) {
                checkGroupDao.addCheckGroupCheckItem(checkGroup.getId(),checkitemId);
            }
        }
    }

    /**
     * 查询所有检查组
     * @return
     */
    @Override
    public List<CheckGroup> findAll() {
        /**
         * 调用Dao层查询素有直接返回结果给Controller
         */
        return checkGroupDao.findAll();
    }

    /**
     * 根据ID删除检查组
     * @param id
     * @throws MyException
     */
    @Override
    @Transactional
    public void deleteById(Integer id) throws MyException {
        /**
         * 根据检查组ID查询有没有关联的套餐
         */
        Integer count = checkGroupDao.findSetmealCheckGroup(id);
        /**
         * 判断有没有关联的套餐
         */
        if (count > 0){
            throw new MyException("该检查组已被套餐绑定，不可删除");
        }
        /**
         * 如果没有关联的套餐，就去t_checkgroup_checkitem删除和该检查组绑定的检查项
         */
        checkGroupDao.deleteCheckItemCheckGroupByCheckGroudId(id);
        /**
         * 处理好所有关联之后再删除该检查组
         */
        checkGroupDao.deleteById(id);
    }
}

