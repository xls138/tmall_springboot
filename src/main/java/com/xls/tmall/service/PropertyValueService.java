
package com.xls.tmall.service;

import java.util.List;

import com.xls.tmall.dao.PropertyValueDAO;
import com.xls.tmall.pojo.Product;
import com.xls.tmall.pojo.Property;
import com.xls.tmall.pojo.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.xls.tmall.util.SpringContextUtil;

@Service
@CacheConfig(cacheNames = "propertyValues")
public class PropertyValueService {

    @Autowired
    PropertyValueDAO propertyValueDAO;
    @Autowired
    PropertyService propertyService;

    //更新或保存一个 PropertyValue 实例到数据库。使用 @CacheEvict(allEntries = true) 注解清除缓存中所有条目，确保缓存数据与数据库保持一致。
    @CacheEvict(allEntries = true)
    public void update(PropertyValue bean) {
        propertyValueDAO.save(bean);
    }

    //初始化给定产品的所有属性值。这个方法首先获取产品所属类别的所有属性，然后为每个属性检查是否已存在属性值。
    //如果不存在，则创建一个新的 PropertyValue 实例并保存。
    //这个方法用于确保每个产品对应每个属性都有一个属性值记录。
    public void init(Product product) {
        //获取属性值服务实例
        PropertyValueService propertyValueService = SpringContextUtil.getBean(PropertyValueService.class);

        //获取产品所属类别的所有属性
        List<Property> propertys = propertyService.listByCategory(product.getCategory());
        for (Property property : propertys) {
            //根据产品和属性获取一个具体的 PropertyValue 实例
            PropertyValue propertyValue = propertyValueService.getByPropertyAndProduct(product, property);
            if (null == propertyValue) {
                propertyValue = new PropertyValue();
                propertyValue.setProduct(product);
                propertyValue.setProperty(property);
                propertyValueDAO.save(propertyValue);
            }
        }
    }

    //根据产品和属性获取一个具体的 PropertyValue 实例。这个方法使用 @Cacheable 注解进行缓存，提高查询效率
    @Cacheable(key = "'propertyValues-one-pid-'+#p0.id+ '-ptid-' + #p1.id")
    public PropertyValue getByPropertyAndProduct(Product product, Property property) {
        return propertyValueDAO.getByPropertyAndProduct(property, product);
    }

    //根据产品获取其所有属性值的列表，结果按照ID降序排序。这个方法也使用 @Cacheable 注解进行缓存。
    @Cacheable(key = "'propertyValues-pid-'+ #p0.id")
    public List<PropertyValue> list(Product product) {
        return propertyValueDAO.findByProductOrderByIdDesc(product);
    }


}

