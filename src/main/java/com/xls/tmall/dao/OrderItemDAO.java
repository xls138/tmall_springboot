
package com.xls.tmall.dao;

import java.util.List;

import com.xls.tmall.pojo.Order;
import com.xls.tmall.pojo.OrderItem;
import com.xls.tmall.pojo.Product;
import com.xls.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemDAO extends JpaRepository<OrderItem, Integer> {
    //根据订单查询订单项
    List<OrderItem> findByOrderOrderByIdDesc(Order order);

    //根据产品查询订单项
    List<OrderItem> findByProduct(Product product);

    //根据用户和订单为空查询订单项
    List<OrderItem> findByUserAndOrderIsNull(User user);
}

