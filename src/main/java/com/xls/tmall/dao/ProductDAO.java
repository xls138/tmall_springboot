package com.xls.tmall.dao;

import com.xls.tmall.pojo.Category;
import com.xls.tmall.pojo.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
//这一行定义了一个名为 ProductDAO 的接口，该接口扩展了 JpaRepository。
//在 Spring Data JPA 中，JpaRepository 是 Repository 接口的一个扩展，提供了一套针对 JPA 的数据库操作方法。
// 这里的 Product 表示该仓库管理的实体类型，Integer 指定了实体的主键类型。
public interface ProductDAO extends JpaRepository<Product, Integer> {
    //这个方法用于根据 Category（类别）来查找 Product（产品），并支持分页。
    //返回类型 Page<Product> 表示结果将以分页的形式返回。
    //Pageable 是 Spring Data 提供的一个分页抽象，封装了分页信息，如当前页码、每页大小以及排序信息。
    Page<Product> findByCategory(Category category, Pageable pageable);

    List<Product> findByCategoryOrderById(Category category);

    List<Product> findByNameLike(String keyword, Pageable pageable);

}

