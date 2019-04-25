package org.japi.core.base.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * Description:服务层接口的父接口，继承此接口默认下面的这些方法要实现的，采用泛型的写法
 * T表示泛型
 *
 * @author dbdu
 * @date 18-6-25 下午3:20
 */
public interface IBaseService<T> extends IService<T> {

    /**
     * Description: 高级搜索＋分页处理
     *
     * @param [page, entity]
     * @return com.baomidou.mybatisplus.core.metadata.IPage<T>
     * @author dbdu
     * @date 19-3-12 上午10:55
     */
    IPage<T> advanceSearch(IPage<T> page, T entity);


    /**
     * Description: 高级搜索不分页，最多显示300条记录,防止内存占用过高！
     *
     * @param [entity]
     * @return java.util.List<T>
     * @author dbdu
     * @date 19-3-12 上午10:56
     */
    List<T> advanceSearch(T entity);

}
