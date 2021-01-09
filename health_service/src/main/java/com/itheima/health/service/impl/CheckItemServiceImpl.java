package com.itheima.health.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.dao.CheckItemDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.exception.MyException;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.service.CheckItemService;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 检查项服务
 */
@Service(interfaceClass = CheckItemService.class)
public class CheckItemServiceImpl implements CheckItemService {

    /**
     * 创建业务层对象
     * @return
     */
    @Autowired
    private CheckItemDao checkItemDao;

    /**
     * 查询所有检查项
     * @return
     */
    @Override
    public List<CheckItem> findAll() {

        return checkItemDao.findAll();
    }

    /**
     * 添加检查项
     * @param checkItem
     */
    @Override
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    /**
     * 分页查询检查项
     * @param queryPageBean
     * @return
     */
    @Override
    public PageResult<CheckItem> findPage(QueryPageBean queryPageBean) {
        /**
         * 先把每页大小取出来，限制每页不能超过50条
         * 如果传过来的每页大小超过50条，就直接给个最大值50
         */
        Integer pageSize = queryPageBean.getPageSize()>=50?50:queryPageBean.getPageSize();
        /**
         * 使用分页插件，设置分页数据，调用PageHelper的静态方法，把当前页数和每页条数作为参数传进去
         */
        PageHelper.startPage(queryPageBean.getCurrentPage(),pageSize);
        /**
         * 使用StringUtil的方法判断查询条件为不为空，空为false,有值为true,就是判断有没有查询条件
         */
        if (StringUtils.isNotEmpty(queryPageBean.getQueryString())){
            /**
             * 有查询条件的话就为它设置上sql语句需要用的符号，进行模糊查询
             */
            queryPageBean.setQueryString("%"+queryPageBean.getQueryString()+"%");
        }
        /**
         * 调用业务层的方法去查询分页数据，就是当前页检查项的一个集合
         * 可以使用Page来接收，因为Page继承了Arraylist
         */
        Page<CheckItem> checkItems = checkItemDao.findByCondtion(queryPageBean.getQueryString());
        /**
         * 因为需要返回给Controller一个PageResult,所以把查询到的总页数和查询到的检查项集合封装到PageResult并返回
         */
        PageResult<CheckItem> pageResult = new PageResult<>(checkItems.getTotal(),checkItems.getResult());
        return pageResult;
    }

    /**
     * 根据ID查询检查项信息
     * @param id
     * @return
     */
    @Override
    public CheckItem findById(Integer id) {
        /**
         * 调用业务层方法得到查询结果直接返回
         */
        return checkItemDao.findById(id);
    }

    /**
     * 修改检查项信息
     * @param checkItem
     */
    @Override
    public void update(CheckItem checkItem) {
        /**
         * 调用持久层方法修改检查项
         */
        checkItemDao.update(checkItem);
    }

    /**
     * 根据ID删除检查项
     * @param id
     */
    @Override
    public void deleteById(Integer id){
        /**
         * 首先判断这个检查项ID有没有关联的检查组
         */
        Integer count = checkItemDao.findCountByCheckItemId(id);
        if (count > 0){
            /**
             * 如果大于0,就证明是有关联的检查组，抛出异常终止执行
             */
            throw new MyException("该检查项有关联的检查组，不能删除");
        }
        /**
         * 如果没有关联的检查组，就根据ID删除检查项
         */
        checkItemDao.deleteById(id);
    }
}
