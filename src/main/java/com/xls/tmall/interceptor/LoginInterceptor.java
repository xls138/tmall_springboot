
package com.xls.tmall.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LoginInterceptor implements HandlerInterceptor {
    //这是HandlerInterceptor接口中的方法，会在请求处理之前被调用。此方法返回true时，请求将继续进行；返回false时，请求处理将停止。
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        //获取HTTP会话和请求上下文路径。
        HttpSession session = httpServletRequest.getSession();
        String contextPath = session.getServletContext().getContextPath();
        //定义一个字符串数组requireAuthPages，包含需要认证（即用户必须登录）才能访问的页面的部分路径。
        String[] requireAuthPages = new String[]{
                "buy",
                "alipay",
                "payed",
                "cart",
                "bought",
                "confirmPay",
                "orderConfirmed",

                "forebuyone",
                "forebuy",
                "foreaddCart",
                "forecart",
                "forechangeOrderItem",
                "foredeleteOrderItem",
                "forecreateOrder",
                "forepayed",
                "forebought",
                "foreconfirmPay",
                "foreorderConfirmed",
                "foredeleteOrder",
                "forereview",
                "foredoreview"

        };


        //方法获取当前请求的URI(getRequestURI())，然后从URI中移除上下文路径部分，以获取实际的页面路径page。
        String uri = httpServletRequest.getRequestURI();

        uri = StringUtils.remove(uri, contextPath + "/");
        String page = uri;


        //通过调用begingWith方法检查当前页面路径是否匹配requireAuthPages数组中的任何一个元素。如果匹配，说明当前请求的页面需要用户登录后才能访问。
        if (begingWith(page, requireAuthPages)) {
            //如果当前页面需要认证，方法使用Apache Shiro的SecurityUtils.getSubject()获取当前的Subject（在Shiro中，Subject代表了当前的用户）。
            Subject subject = SecurityUtils.getSubject();
            //然后检查这个Subject是否已经通过了身份验证（isAuthenticated()）。
            //如果用户未通过身份验证（即未登录），则通过HttpServletResponse将用户重定向到登录页面，并返回false以停止请求的进一步处理。
            if (!subject.isAuthenticated()) {
                httpServletResponse.sendRedirect("login");
                return false;
            }
        }
        return true;
    }

    //这是一个私有方法，用于检查给定的页面名（page）是否以requireAuthPages数组中的任一字符串开始。
    //这是通过遍历数组并使用StringUtils.startsWith方法进行字符串比对来实现的。
    //如果页面名以数组中的任何一个字符串开始，则方法返回true，否则返回false。
    private boolean begingWith(String page, String[] requiredAuthPages) {
        boolean result = false;
        for (String requiredAuthPage : requiredAuthPages) {
            if (StringUtils.startsWith(page, requiredAuthPage)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }
}

//        a. 不需要登录也可以访问的
//        如：注册，登录，产品，首页，分类，查询等等
//        b. 需要登录才能够访问的
//        如：购买行为，加入购物车行为，查看购物车，查看我的订单等等
