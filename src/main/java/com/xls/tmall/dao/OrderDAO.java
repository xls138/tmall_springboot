
package com.xls.tmall.dao;
 
import java.util.List;

import com.xls.tmall.pojo.Order;
import com.xls.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDAO extends JpaRepository<Order,Integer>{
    public List<Order> findByUserAndStatusNotOrderByIdDesc(User user, String status);
}

