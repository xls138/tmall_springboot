
package com.xls.tmall.dao;

import java.util.List;

import com.xls.tmall.pojo.Category;
import com.xls.tmall.pojo.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyDAO extends JpaRepository<Property, Integer> {
    Page<Property> findByCategory(Category category, Pageable pageable);

    List<Property> findByCategory(Category category);

}

