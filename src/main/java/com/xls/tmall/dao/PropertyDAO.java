
package com.xls.tmall.dao;

import java.util.List;

import com.xls.tmall.pojo.Category;
import com.xls.tmall.pojo.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyDAO extends JpaRepository<Property, Integer> {
    //这个方法是一个自定义查询方法，它的名字遵循Spring Data JPA的命名规则。
    // 这个方法的作用是根据传入的Category对象来查找所有对应的Property对象，并且返回一个Page<Property>对象。
    // Page是Spring Data提供的一个分页抽象，它包含了查询结果的一部分数据以及分页信息。
    // Pageable参数是一个分页参数，它包含了分页的信息，比如当前页码、每页大小等。
    Page<Property> findByCategory(Category category, Pageable pageable);
    //不分页的查询方法
    List<Property> findByCategory(Category category);

}

