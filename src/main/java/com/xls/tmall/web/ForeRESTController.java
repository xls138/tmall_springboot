
package com.xls.tmall.web;

import com.xls.tmall.comparator.*;
import com.xls.tmall.pojo.*;
import com.xls.tmall.service.*;
import com.xls.tmall.comparator.*;
import com.xls.tmall.service.*;
import com.xls.tmall.util.Result;
import com.xls.tmall.pojo.*;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class ForeRESTController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    OrderService orderService;


    @GetMapping("/forehome")
    public Object home() {
        //查询所有分类
        List<Category> cs = categoryService.list();
        //为这些分类填充产品集合
        productService.fill(cs);
        //为这些分类填充推荐产品集合
        productService.fillByRow(cs);
        //移除产品里的分类信息，以免出现重复递归
        categoryService.removeCategoryFromProduct(cs);

        return cs;
    }

    //注册
    @PostMapping("/foreregister")
    public Object register(@RequestBody User user) {
        //通过参数User获取浏览器提交的账号密码
        String name = user.getName();
        String password = user.getPassword();
        //通过HtmlUtils.htmlEscape(name);把账号里的特殊符号进行转义
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);

        boolean exist = userService.isExist(name);

        //如果已经存在，就返回Result.fail,并带上 错误信息
        if (exist) {
            String message = "用户名已经被使用,不能使用";
            return Result.fail(message);
        }

        //会通过随机方式创建盐， 并且加密算法采用 "md5", 除此之外还会进行 2次加密。
        //这个盐，如果丢失了，就无法验证密码是否正确了，所以会数据库里保存起来
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2;
        String algorithmName = "md5";

        String encodedPassword = new SimpleHash(algorithmName, password, salt, times).toString();

        user.setSalt(salt);
        user.setPassword(encodedPassword);

        //如果不存在，则加入到数据库中，并返回 Result.success()
        userService.add(user);

        return Result.success();
    }

    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session) {
        String name = userParam.getName();
        name = HtmlUtils.htmlEscape(name);

        //获取当前用户的 Subject 对象（SecurityUtils.getSubject()）。在 Apache Shiro 中，Subject 代表了当前用户
        Subject subject = SecurityUtils.getSubject();
        //创建一个 UsernamePasswordToken，其中包含用户名和密码，用于登录。
        UsernamePasswordToken token = new UsernamePasswordToken(name, userParam.getPassword());
        try {
            //尝试登录（subject.login(token)）。这一步将验证用户名和密码是否与存储在系统中的凭证匹配
            subject.login(token);
            User user = userService.getByName(name);
            session.setAttribute("user", user);
            return Result.success();
        } catch (AuthenticationException e) {
            String message = "账号密码错误";
            return Result.fail(message);
        }

    }

    @GetMapping("/foreproduct/{pid}")
    public Object product(@PathVariable("pid") int pid) {
        //根据pid获取Product
        Product product = productService.get(pid);
        //根据product获取单个图片集合
        List<ProductImage> productSingleImages = productImageService.listSingleProductImages(product);
        //根据product获取详情图片集合
        List<ProductImage> productDetailImages = productImageService.listDetailProductImages(product);
        //设置产品的单个图片集合
        product.setProductSingleImages(productSingleImages);
        //设置产品的详情图片集合
        product.setProductDetailImages(productDetailImages);
        //获取产品的所有属性值
        List<PropertyValue> pvs = propertyValueService.list(product);
        //获取产品的所有评价
        List<Review> reviews = reviewService.list(product);
        //设置产品的销量和评价数量
        productService.setSaleAndReviewNumber(product);
        //设置产品的首张图片
        productImageService.setFirstProductImage(product);

        //将产品信息、属性值列表和评论列表放入此 Map 中。
        Map<String, Object> map = new HashMap<>();
        map.put("product", product);
        map.put("pvs", pvs);
        map.put("reviews", reviews);

        // 返回成功的结果，其中包含了产品的详细信息、属性值和评论。
        return Result.success(map);
    }

    @GetMapping("forecheckLogin")
    public Object checkLogin() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated())
            return Result.success();
        else
            return Result.fail("未登录");
    }

    @GetMapping("forecategory/{cid}")
    public Object category(@PathVariable int cid, String sort) {
        //根据cid获取分类Category对象
        Category c = categoryService.get(cid);
        //为分类对象填充产品集合
        productService.fill(c);
        //为产品集合填充销量和评价数据
        productService.setSaleAndReviewNumber(c.getProducts());
        //移除产品里的分类信息，以免出现重复递归
        categoryService.removeCategoryFromProduct(c);

        //则根据sort的值，从5个Comparator比较器中选择一个对应的排序器进行排序
        if (null != sort) {
            switch (sort) {
                case "review":
                    Collections.sort(c.getProducts(), new ProductReviewComparator());
                    break;
                case "date":
                    Collections.sort(c.getProducts(), new ProductDateComparator());
                    break;

                case "saleCount":
                    Collections.sort(c.getProducts(), new ProductSaleCountComparator());
                    break;

                case "price":
                    Collections.sort(c.getProducts(), new ProductPriceComparator());
                    break;

                case "all":
                    Collections.sort(c.getProducts(), new ProductAllComparator());
                    break;
            }
        }

        return c;
    }

    @PostMapping("foresearch")
    public Object search(String keyword) {
        if (null == keyword)
            keyword = "";
        //根据keyword进行模糊查询，获取满足条件的前20个产品
        List<Product> ps = productService.search(keyword, 0, 20);
        productImageService.setFirstProductImages(ps);
        //为这些产品设置销量和评价数量
        productService.setSaleAndReviewNumber(ps);
        //返回这个产品集合
        return ps;
    }

    @GetMapping("forebuyone")
    public Object buyone(int pid, int num, HttpSession session) {
        return buyoneAndAddCart(pid, num, session);
    }

    private int buyoneAndAddCart(int pid, int num, HttpSession session) {
        //根据pid获取产品对象product
        Product product = productService.get(pid);
        int oiid = 0;

        //从session中获取用户对象user
        User user = (User) session.getAttribute("user");
        boolean found = false;
        //查询出当前用户所有的未生成订单的OrderItem订单项集合
        List<OrderItem> ois = orderItemService.listByUser(user);
        for (OrderItem oi : ois) {
            //如果产品是一样的话，就进行数量追加
            if (oi.getProduct().getId() == product.getId()) {
                oi.setNumber(oi.getNumber() + num);
                //更新这个订单项的数量
                orderItemService.update(oi);
                found = true;
                //获取这个订单项的 id
                oiid = oi.getId();
                break;
            }
        }

        if (!found) {
            OrderItem oi = new OrderItem();
            oi.setUser(user);
            oi.setProduct(product);
            oi.setNumber(num);
            orderItemService.add(oi);
            oiid = oi.getId();
        }
        return oiid;
    }

    @GetMapping("forebuy")
    public Object buy(String[] oiid, HttpSession session) {
        //创建一个OrderItem对象的列表，用于存储用户购买的商品
        List<OrderItem> orderItems = new ArrayList<>();
        float total = 0;

        for (String strid : oiid) {
            //获取订单项的id
            int id = Integer.parseInt(strid);
            //根据id获取订单项OrderItem对象
            OrderItem oi = orderItemService.get(id);
            //累计这些产品的价格总数
            total += oi.getProduct().getPromotePrice() * oi.getNumber();
            //把订单项放在列表中
            orderItems.add(oi);
        }

        productImageService.setFirstProductImagesOnOrderItems(orderItems);

        //将订单项列表orderItems存储在用户会话中，以便后续操作（如结账）可以使用
        session.setAttribute("ois", orderItems);

        Map<String, Object> map = new HashMap<>();
        map.put("orderItems", orderItems);
        map.put("total", total);
        //返回一个成功的响应，其中包含了订单项和总价信息
        return Result.success(map);
    }

    @GetMapping("foreaddCart")
    public Object addCart(int pid, int num, HttpSession session) {
        buyoneAndAddCart(pid, num, session);
        return Result.success();
    }

    @GetMapping("forecart")
    public Object cart(HttpSession session) {
        //从session中获取用户对象user
        User user = (User) session.getAttribute("user");
        //获取为这个用户关联的订单项集合 ois
        List<OrderItem> ois = orderItemService.listByUser(user);
        //为这些订单项设置对应的产品图片
        productImageService.setFirstProductImagesOnOrderItems(ois);
        return ois;
    }

    @GetMapping("forechangeOrderItem")
    public Object changeOrderItem(HttpSession session, int pid, int num) {
        //判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (null == user)
            return Result.fail("未登录");

        //遍历出用户当前所有的未生成订单的OrderItem
        List<OrderItem> ois = orderItemService.listByUser(user);
        for (OrderItem oi : ois) {
            //根据pid找到匹配的OrderItem，并修改数量后更新到数据库
            if (oi.getProduct().getId() == pid) {
                oi.setNumber(num);
                orderItemService.update(oi);
                break;
            }
        }
        return Result.success();
    }

    @GetMapping("foredeleteOrderItem")
    public Object deleteOrderItem(HttpSession session, int oiid) {
        User user = (User) session.getAttribute("user");
        if (null == user)
            return Result.fail("未登录");
        orderItemService.delete(oiid);
        return Result.success();
    }


    @PostMapping("forecreateOrder")
    public Object createOrder(@RequestBody Order order, HttpSession session) {
        //从session中获取用户对象user
        User user = (User) session.getAttribute("user");
        if (null == user)
            return Result.fail("未登录");
        //根据当前时间加上一个4位随机数生成订单号
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUser(user);
        //设置订单状态为等待支付
        order.setStatus(OrderService.waitPay);
        //从session中获取订单项集合
        List<OrderItem> ois = (List<OrderItem>) session.getAttribute("ois");
        //把订单加入到数据库，并且遍历订单项集合，设置每个订单项的order，更新到数据库,并且计算出本订单总金额
        float total = orderService.add(order, ois);

        Map<String, Object> map = new HashMap<>();
        map.put("oid", order.getId());
        map.put("total", total);

        return Result.success(map);
    }



    @GetMapping("forebought")
    public Object bought(HttpSession session) {
        //从session中获取用户对象user
        User user = (User) session.getAttribute("user");
        if (null == user)
            return Result.fail("未登录");
        //查询user所有的状态不是"delete" 的订单集合os
        List<Order> os = orderService.listByUserWithoutDelete(user);
        //为这些订单填充订单项
        orderService.removeOrderFromOrderItem(os);
        return os;
    }
    @GetMapping("forepayed")
    public Object payed(int oid) {
        //根据oid获取到订单对象，并更新其状态和支付时间
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        //更新这个订单对象到数据库
        orderService.update(order);
        return order;
    }
    @GetMapping("foreconfirmPay")
    //确认收货
    public Object confirmPay(int oid) {
        //根据oid获取到订单对象order
        Order o = orderService.get(oid);
        //为订单对象填充订单项
        orderItemService.fill(o);
        orderService.cacl(o);
        //把订单项从订单中移除，以免出现重复
        orderService.removeOrderFromOrderItem(o);
        return o;
    }

    @GetMapping("foreorderConfirmed")
    public Object orderConfirmed(int oid) {
        //根据oid获取订单对象
        Order o = orderService.get(oid);
        //修改订单对象的状态和确认支付时间
        o.setStatus(OrderService.waitReview);
        o.setConfirmDate(new Date());
        //更新订单对象到数据库
        orderService.update(o);
        return Result.success();
    }

    @PutMapping("foredeleteOrder")
    public Object deleteOrder(int oid) {
        //根据oid获取订单对象
        Order o = orderService.get(oid);
        //修改状态
        o.setStatus(OrderService.delete);
        //更新到数据库
        orderService.update(o);
        return Result.success();
    }

    @GetMapping("forereview")
    public Object review(int oid) {
        //根据oid获取订单对象o
        Order o = orderService.get(oid);
        //为订单对象填充订单项
        orderItemService.fill(o);
        //把订单项从订单中移除，以免出现重复
        orderService.removeOrderFromOrderItem(o);
        //获取产品对象
        Product p = o.getOrderItems().get(0).getProduct();
        //获取这个产品的评价集合
        List<Review> reviews = reviewService.list(p);
        //为产品设置评价数量和销量
        productService.setSaleAndReviewNumber(p);
        //把产品，订单和评价集合放在map上
        Map<String, Object> map = new HashMap<>();
        map.put("p", p);
        map.put("o", o);
        map.put("reviews", reviews);

        return Result.success(map);
    }

    @PostMapping("foredoreview")
    public Object doreview(HttpSession session, int oid, int pid, String content) {
        //根据oid获取订单对象o
        Order o = orderService.get(oid);
        //修改订单对象状态
        o.setStatus(OrderService.finish);
        //更新订单对象到数据库
        orderService.update(o);
        //获取产品对象
        Product p = productService.get(pid);
        //对评价信息进行转义，道理同注册ForeController.register()
        content = HtmlUtils.htmlEscape(content);
        //从session中获取当前用户
        User user = (User) session.getAttribute("user");
        //创建评价对象review
        Review review = new Review();
        //为评价对象review设置 评价内容，产品，时间，用户，订单
        review.setContent(content);
        review.setProduct(p);
        review.setCreateDate(new Date());
        review.setUser(user);
        //增加到数据库
        reviewService.add(review);
        return Result.success();
    }
}


