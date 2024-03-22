
package com.xls.tmall.dao;

import java.util.List;

import com.xls.tmall.pojo.Product;
import com.xls.tmall.pojo.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageDAO extends JpaRepository<ProductImage, Integer> {
    public List<ProductImage> findByProductAndTypeOrderByIdDesc(Product product, String type);

}

