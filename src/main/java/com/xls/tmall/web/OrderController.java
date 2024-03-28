
package com.xls.tmall.web;

import com.xls.tmall.pojo.Order;
import com.xls.tmall.service.OrderItemService;
import com.xls.tmall.service.OrderService;
import com.xls.tmall.util.Page4Navigator;
import com.xls.tmall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;

@RestController
public class OrderController {
	@Autowired OrderService orderService;
	@Autowired OrderItemService orderItemService;

	//1. admin_order_list 导致 listOrder.html 返回
    //2. listOrder.html 通过 axios.js 访问 orders 路径
    //3. OrderController 的list 方法响应 orders 路径
    //4. list方法调用 OrderService的分页方法 list,并返回集合， 这个集合被 restful 注解转换为 json数组
    //5. html 拿到这个 数组后，进行二次遍历。 第一次遍历展示 order 信息，第二次遍历展示当前order对应的 orderItems信息。
    //6. 遍历出来的orderItems信息默认是隐藏的，可以通过 查看详情 按钮点击之后调用的 showOrderItems 展示出来。
    @GetMapping("/orders")
    public Page4Navigator<Order> list(@RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
    	start = start<0?0:start;
    	Page4Navigator<Order> page =orderService.list(start, size, 5); 
    	orderItemService.fill(page.getContent());
        orderService.removeOrderFromOrderItem(page.getContent());
        return page;
    }

    //1. 当订单状态是 waitDelivery的时候，就会出现发货按钮。
    //2. 点击发货按钮，导致deliveryOrder 函数被调用，会送异步请求到 deliveryOrder/id 这个地址去。 同时，隐藏这个按钮。其中e.target就是这个按钮。
    //3. OrderController 的 deliveryOrder 方法响应这个请求，修改状态并更新。
    //4. 然后返回一个 Result.success()，用于表明成功了。
    @PutMapping("deliveryOrder/{oid}")
    public Object deliveryOrder(@PathVariable int oid) throws IOException {
        Order o = orderService.get(oid);
        o.setDeliveryDate(new Date());
        o.setStatus(OrderService.waitConfirm);
        orderService.update(o);
        return Result.success();
    }
}


