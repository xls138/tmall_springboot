
package com.xls.tmall.dao;


import java.util.List;

import com.xls.tmall.pojo.Product;
import com.xls.tmall.pojo.Property;
import com.xls.tmall.pojo.PropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyValueDAO extends JpaRepository<PropertyValue, Integer> {

    List<PropertyValue> findByProductOrderByIdDesc(Product product);

    PropertyValue getByPropertyAndProduct(Property property, Product product);

}

