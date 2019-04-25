package org.japi.core.base.entity;

import lombok.Getter;
import lombok.Setter;


/**
 * 字典的基础表的实体
 */
@Setter
@Getter
public class BaseDictEntity<T extends BaseEntity> extends BaseEntity<T> {
    private static final long serialVersionUID = 1L;

    /**
     * 字典名称
     */
    private String name;

    /**
     * 字典值
     */
    private String value;

    /**
     * 字典码
     */
    private String code;

    /**
     * 字典类型
     */
    private String type;

    private Integer orderNum;    //不要用基本类型，否则会自动作为条件带上

    private String remark;

    /**
     * 删除标记 -1：已删除，0：正常
     */
    private Integer delFlag;
}
