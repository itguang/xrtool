package com.xingren.excel.converter.read;

import com.xingren.excel.pojo.ExcelColumnAnnoEntity;

/**
 * @author guang
 * @since 2020/1/18 9:50 上午
 */
public class DefaultReadConverter implements IReadConverter {

    @Override
    public Object convert(ExcelColumnAnnoEntity entity, Class<?> clazz, Object cellValue) {
        throw new UnsupportedOperationException("该类属于注解默认参数,不可实例化");
    }
}