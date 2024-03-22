
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
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        HttpSession session = httpServletRequest.getSession();
        String contextPath = session.getServletContext().getContextPath();
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


        String uri = httpServletRequest.getRequestURI();

        uri = StringUtils.remove(uri, contextPath + "/");
        String page = uri;


        if (begingWith(page, requireAuthPages)) {
            Subject subject = SecurityUtils.getSubject();
            if (!subject.isAuthenticated()) {
                httpServletResponse.sendRedirect("login");
                return false;
            }
        }
        return true;
    }

    //用于检查给定的页面名是否以数组 requiredAuthPages 中的任一字符串开始。
    //如果页面名以数组中的任一字符串开始，返回 true；否则返回 false
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
