package org.japi.core.base.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.japi.core.base.entity.BaseEntity;
import org.japi.core.base.entity.BaseSearchField;
import org.japi.core.base.enums.SearchRelationEnum;
import org.japi.core.base.enums.SearchTypeEnum;
import org.japi.core.base.service.IBaseService;
import org.japi.core.util.CommUtil;
import org.japi.core.util.MapUtil;
import org.japi.core.util.SQLFilter;

import java.util.*;

/**
 * Description:这个类是BaseServcie的实现类，组件的实现类可以继承这个类来利用可以用的方法
 * 当然，也可以在这里添加调用前的检查逻辑
 * Created at:2018-06-25 16:10,
 * by dbdu
 */
@Slf4j
public class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseEntity> extends ServiceImpl<M, T> implements IBaseService<T> {

    @Override
    public IPage<T> advanceSearch(IPage<T> page, T entity) {
        // 构建查询条件
        QueryWrapper<T> queryWrapper = getQueryWrapper(entity);
        // 执行查询
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<T> advanceSearch(T entity) {
        // 构建查询条件
        QueryWrapper<T> queryWrapper = getQueryWrapper(entity);
        // 执行查询
        queryWrapper.last("limit 300");    //最多返回300条记录
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * Description: 利用实体来构建查询条件
     *
     * @param [entity]
     * @return com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<T>
     * @author dbdu
     * @date 19-4-18 下午3:21
     */
    private QueryWrapper<T> getQueryWrapper(T entity) {
        Set<BaseSearchField> conditions = entity.getConditions();   //查询条件集合
        int searchRelation = entity.getRelationType() != null ? entity.getRelationType() : 0;       // 与或非,默认或的关系
        // 检查属性名和属性值的合法性
        checkPropertyAndValueＶalidity(entity);

        // 属性名==>字段名,驼峰转下划线
        property2Column(conditions);

        // 处理OR 或者AND的逻辑
        QueryWrapper<T> queryWrapper;
        if (searchRelation == SearchRelationEnum.OR.getValue()) {
            queryWrapper = getOrQueryWrapper(conditions);
        } else {
            queryWrapper = getAndQueryWrapper(conditions);
        }
        return queryWrapper;
    }

    /**
     * Description:检查属性名和属性值的合法性,不合法的属性和值都会被移除
     *
     * @param [conditions]
     * @return void
     * @author dbdu
     * @date 19-3-11 下午6:14
     */
    private void checkPropertyAndValueＶalidity(T entity) {
        Set<BaseSearchField> conditions = entity.getConditions();
        if (conditions == null || conditions.size() == 0) {
            return;
        }

        // 检查属性名是否合法 非法
        Set<BaseSearchField> illegalConditions = new HashSet<>();        //存放非法的查询条件
        Map<String, String> properties = (Map<String, String>) MapUtil.objectToMap1(entity);
        Set<String> keys = properties.keySet();
        // 如果条件的字段名称与属性名不符，则移除，不作为选择条件；
        conditions.forEach(condition -> {
            if (!keys.contains(condition.getName())) {
                illegalConditions.add(condition);
            }
        });
        // 移除非法的条件
        conditions.removeAll(illegalConditions);

        //继续检查条件的值是否有非法敏感的关键字
        conditions.forEach(condition -> {
            String value1 = condition.getVal();
            if (SQLFilter.sqlInject(value1)) {
                illegalConditions.add(condition);
            }

            // 如果是范围需要检查两个值是否合法
            if (condition.getSearchType() == SearchTypeEnum.RANGE.getValue()) {
                String value2 = condition.getVal2();
                if (SQLFilter.sqlInject(value2)) {
                    illegalConditions.add(condition);
                }
            }
        });

        // 移除非法条件
        conditions.removeAll(illegalConditions);
    }


    /**
     * Description: 对象的属性名转换为数据表的字段名
     *
     * @param [conditions]
     * @return void
     * @author dbdu
     * @date 19-3-11 下午6:07
     */
    private void property2Column(Set<BaseSearchField> conditions) {
        if (conditions != null && conditions.size() > 0) {
            conditions.forEach(condition -> {
                condition.setName(CommUtil.camelCase2Underscore(condition.getName()));
            });
        }

    }


    /**
     * Description: --OR关系 条件构造,所有的条件满足一个就会选中
     *
     * @param [conditions]
     * @return com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<T>
     * @author dbdu
     * @date 19-3-11 下午4:33
     */
    private QueryWrapper<T> getOrQueryWrapper(Set<BaseSearchField> conditions) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (conditions != null && conditions.size() > 0) {
            Iterator iterator = conditions.iterator();
            queryWrapper.and(wrapper -> {
                while (iterator.hasNext()) {
                    BaseSearchField condition = (BaseSearchField) iterator.next();

                    //　根据搜索条件不同构造不同的查询语句
                    createFieldCondition(wrapper, condition);
                    // 如果还有下一个就加上or的条件
                    if (iterator.hasNext()) {
                        wrapper.or();
                    }

                }
                return wrapper;
            });
        }
        return queryWrapper;
    }

    /**
     * Description:---与的关系,所有的条件全部满足才会选中
     *
     * @param [conditions]
     * @return com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<T>
     * @author dbdu
     * @date 19-3-11 下午4:34
     */
    private QueryWrapper<T> getAndQueryWrapper(Set<BaseSearchField> conditions) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (conditions != null && conditions.size() > 0) {
            Iterator iterator = conditions.iterator();
            queryWrapper.and(wrapper -> {
                while (iterator.hasNext()) {
                    BaseSearchField condition = (BaseSearchField) iterator.next();
                    //　根据搜索条件不同构造不同的查询语句
                    createFieldCondition(wrapper, condition);
                }
                return wrapper;
            });
        }
        return queryWrapper;
    }

    /**
     * Description: 每个条件构造SQL语句
     *
     * @param [wrapper, condition]
     * @return void
     * @author dbdu
     * @date 19-3-11 下午4:47
     */
    private void createFieldCondition(QueryWrapper<T> wrapper, BaseSearchField condition) {
        switch (SearchTypeEnum.getByValue(condition.getSearchType())) {
            case EQ:         //　等于条件
                wrapper.eq(condition.getName(), condition.getVal());
                break;
            case FUZZY:         //  模糊条件
                wrapper.like(condition.getName(), condition.getVal());
                break;
            case RANGE:         //  范围条件
                wrapper.between(condition.getName(), condition.getVal(), condition.getVal2());
                break;
            case NE:            //  不等于条件
                wrapper.ne(condition.getName(), condition.getVal());
                break;
            case GT:            //  大于条件
                wrapper.gt(condition.getName(), condition.getVal());
                break;
            case GE:            //  大于等于
                wrapper.ge(condition.getName(), condition.getVal());
                break;
            case LT:            //  小于
                wrapper.lt(condition.getName(), condition.getVal());
                break;
            case LE:            //  小于等于
                wrapper.le(condition.getName(), condition.getVal());
                break;
            default:
                log.warn("未知的搜索条件！");
                break;
        }
    }

}
