
package com.xls.tmall.dao;

import com.xls.tmall.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;
//继承JpaRepository，就提供了CRUD和分页 的各种常见功能,无需编写SQL语句
public interface CategoryDAO extends JpaRepository<Category, Integer> {

}

