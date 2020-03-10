package com.xxc.dao.base;

import cn.hutool.log.StaticLog;
import com.xxc.entity.annotation.MultiTable;
import tk.mybatis.mapper.entity.IDynamicTableName;

import javax.persistence.Table;
import java.beans.Transient;
import java.lang.reflect.Field;

/**
 * IDynamicTableName接口实现动态表名;
 * 之后所有与数据库表对应的实体类都要继承该抽象类
 *
 * @author xixincan
 * 2020-03-10
 * @version 1.0.0
 */
public class DynamicTableNameBean implements IDynamicTableName {

    /**
     * 获取动态表名 - 只要有返回值，不是null和''，就会用返回值作为表名
     */
    @Override
    @Transient
    public String getDynamicTableName() {
        StringBuilder tableName = new StringBuilder(this.getClass().getAnnotation(Table.class).name());
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field item : fields) {
            if (item.isAnnotationPresent(MultiTable.class) && item.getAnnotation(MultiTable.class).value() > 1) {
                try {
                    item.setAccessible(true);
                    Object object = item.get(this);
                    int order;
                    if (object instanceof Number) {
                        Number number = (Number) object;
                        order = Math.abs(number.intValue()) % item.getAnnotation(MultiTable.class).value();
                    } else {
                        order = Math.abs(object.hashCode()) % item.getAnnotation(MultiTable.class).value();
                    }

                    return tableName.append("_").append(order).toString();
                } catch (IllegalAccessException e) {
                    StaticLog.error("动态找分表映射发生异常", e);
                }
            }
        }
        return tableName.toString();
    }

}
