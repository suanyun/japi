package org.japi.core.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.japi.core.base.entity.BaseEntity;
import org.japi.core.base.entity.BaseSearchField;
import org.japi.core.base.service.IBaseService;
import org.japi.core.util.CommUtil;
import org.japi.core.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashSet;
import java.util.Set;

/**
 * Description:这是一个基础的控制器基类，包含常见的基本的CRUD的请求的处理以及高级搜索的处理，其他特殊的方法通过子类继承实现。
 * T1,是操作的实体类，T2是对应的service接口类继承IBaseService,
 *
 * @author dbdu
 */
@Slf4j
@SuppressWarnings("unchecked")
public class BaseController<T1 extends BaseEntity, T2 extends IBaseService> {

    @Autowired
    protected T2 entityService;

    /**
     * Description: 高级搜索查询,返回最多300记录的列表,不分页
     *
     * @author dbdu
     */
    public R getList(@RequestBody T1 entity) {
        // 高级搜索查询,返回最多300记录的列表
        return new R<>(entityService.advanceSearch(entity));
    }

    /**
     * Description:所有实体的分页列表,
     * 如果有高级查询条件,优先使用高级查询
     *
     * @author dbdu
     */
    public R getByPage(Page page, T1 entity) {
        // 过滤掉值为null或空串的无效高级搜索条件
        if (entity.getConditions() != null && entity.getConditions().size() > 0) {
            Set<Object> removeConditions = new HashSet<>();
            for (Object searchField : entity.getConditions()) {
                // 当搜索值为null或者空串时,这是一条无效的搜索条件,需要移除
                if (CommUtil.isEmptyString(((BaseSearchField) searchField).getVal())) {
                    removeConditions.add(searchField);
                }
            }
            entity.getConditions().removeAll(removeConditions);
        }

        if (entity.getConditions() == null || entity.getConditions().size() == 0) {
            // 默认的分页功能
            QueryWrapper<T1> wrapper = new QueryWrapper<>(entity)
                    .orderByDesc("create_time"); // 默认为创建时间降序降序
            return new R<>(entityService.page(page, wrapper));
        } else {
            // 高级搜索查询,支持分页
            return new R<>(entityService.advanceSearch(page, entity));
        }

    }

    /**
     * Description:依据实体的id主键，来查询单个实体的信息
     *
     * @param [entityId]
     * @return com.pig4cloud.pigx.common.core.util.R
     * @author dbdu
     * @date 19-4-20 下午3:02
     */
    public R getById(Long entityId) {
        return new R<>(entityService.getById(entityId));
    }

    /**
     * Description:保存单个实体的信息
     *
     * @param [entity]
     * @return com.pig4cloud.pigx.common.core.util.R
     * @author dbdu
     * @date 19-4-20 下午3:02
     */
    public R save(T1 entity) {
        entityService.save(entity);
        return new R<>(entity);
    }

    /**
     * Description:更新实体的信息
     *
     * @param [entity]
     * @return com.pig4cloud.pigx.common.core.util.R
     * @author dbdu
     * @date 19-4-20 下午3:03
     */
    public R updateById(T1 entity) {
        entity.setUpdateTime(null);    // 让数据库自己更新此字段
        return new R<>(entityService.updateById(entity));
    }

    /**
     * Description:使用entityId删除单个实体,
     *
     * @param [entityId]
     * @return com.pig4cloud.pigx.common.core.util.R
     * @author dbdu
     * @date 19-4-20 下午3:03
     */
    public R deleteById(Long entityId) {

        return new R<>(entityService.removeById(entityId));
    }

}


