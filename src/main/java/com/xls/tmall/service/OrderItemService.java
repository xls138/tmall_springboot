
package com.xls.tmall.service;

import java.util.List;

import com.xls.tmall.dao.OrderItemDAO;
import com.xls.tmall.pojo.Order;
import com.xls.tmall.pojo.OrderItem;
import com.xls.tmall.pojo.Product;
import com.xls.tmall.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.xls.tmall.util.SpringContextUtil;

@Service
@CacheConfig(cacheNames = "orderItems")
public class OrderItemService {
    @Autowired
    OrderItemDAO orderItemDAO;
    @Autowired
    ProductImageService productImageService;

    //为订单列表填充订单项。对于每个订单，调用 fill(Order order) 方法
    public void fill(List<Order> orders) {
        for (Order order : orders)
            fill(order);
    }

    //(@CacheEvict): 更新订单项，并清除所有相关缓存。使用了 @CacheEvict 注解来确保缓存数据的一致性。
    @CacheEvict(allEntries = true)
    public void update(OrderItem orderItem) {
        orderItemDAO.save(orderItem);
    }

    //为单个订单填充订单项。计算订单的总金额和总数量，并设置产品的首张图片。
    public void fill(Order order) {
        OrderItemService orderItemService = SpringContextUtil.getBean(OrderItemService.class);
        List<OrderItem> orderItems = orderItemService.listByOrder(order);
        float total = 0;
        int totalNumber = 0;
        for (OrderItem oi : orderItems) {
            total += oi.getNumber() * oi.getProduct().getPromotePrice();
            totalNumber += oi.getNumber();
            productImageService.setFirstProductImage(oi.getProduct());
        }
        order.setTotal(total);
        order.setOrderItems(orderItems);
        order.setTotalNumber(totalNumber);
        order.setOrderItems(orderItems);
    }

    // (@CacheEvict): 添加新的订单项，并清除所有相关缓存。
    @CacheEvict(allEntries = true)
    public void add(OrderItem orderItem) {
        orderItemDAO.save(orderItem);
    }

    //根据订单项 ID 获取订单项。使用缓存来提高读取性能，缓存键为 'orderItems-one-'+ #p0。
    @Cacheable(key = "'orderItems-one-'+ #p0")
    public OrderItem get(int id) {
        return orderItemDAO.findOne(id);
    }

    //删除指定 ID 的订单项，并清除所有相关缓存。
    @CacheEvict(allEntries = true)
    public void delete(int id) {
        orderItemDAO.delete(id);
    }


    //根据产品查询销售量
    public int getSaleCount(Product product) {
        //获取OrderItemService
        OrderItemService orderItemService = SpringContextUtil.getBean(OrderItemService.class);
        //根据产品查询订单项
        List<OrderItem> ois = orderItemService.listByProduct(product);
        int result = 0;
        //遍历订单项
        for (OrderItem oi : ois) {
            //如果订单项不为空，且订单项的订单不为空，且订单项的订单的支付时间不为空
            if (null != oi.getOrder())
                if (null != oi.getOrder() && null != oi.getOrder().getPayDate())
                    //销售量加上订单项的数量
                    result += oi.getNumber();
        }
        return result;
    }

    //(@Cacheable): 获取用户未完成的订单项列表，使用缓存来提高读取性能。
    @Cacheable(key = "'orderItems-uid-'+ #p0.id")
    public List<OrderItem> listByUser(User user) {
        return orderItemDAO.findByUserAndOrderIsNull(user);
    }

    // (@Cacheable): 获取产品的订单项列表，使用缓存来提高读取性能。
    @Cacheable(key = "'orderItems-pid-'+ #p0.id")
    public List<OrderItem> listByProduct(Product product) {
        return orderItemDAO.findByProduct(product);
    }

    // (@Cacheable): 获取订单的订单项列表，使用缓存来提高读取性能。
    @Cacheable(key = "'orderItems-oid-'+ #p0.id")
    public List<OrderItem> listByOrder(Order order) {
        return orderItemDAO.findByOrderOrderByIdDesc(order);
    }


}

