package com.xingren.excel.converter;

import com.xingren.excel.converter.write.IWriteConverter;
import com.xingren.excel.entity.Product;
import com.xingren.excel.pojo.ExcelColumnAnnoEntity;

/**
 * @author guang
 * @since 2020/2/11 4:24 下午
 */
public class MyConverter implements IWriteConverter<Product, String> {

    @Override
    public String convert(ExcelColumnAnnoEntity entity, Product rowData) {
        Product product = (Product) rowData;
        return product.getName() + "--" + product.getOther();
    }
}
