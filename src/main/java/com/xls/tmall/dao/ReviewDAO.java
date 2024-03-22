
package com.xls.tmall.dao;

import java.util.List;

import com.xls.tmall.pojo.Product;
import com.xls.tmall.pojo.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewDAO extends JpaRepository<Review, Integer> {

    //查询某个产品的所有评价信息
    List<Review> findByProductOrderByIdDesc(Product product);

    //查询某个产品的评价数量
    int countByProduct(Product product);

}

