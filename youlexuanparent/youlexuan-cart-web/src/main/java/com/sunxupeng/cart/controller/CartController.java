package com.sunxupeng.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.sunxupeng.cart.service.CartService;
import com.sunxupeng.entity.Result;
import com.sunxupeng.group.Cart;
import com.sunxupeng.pojo.TbOrderItem;
import com.sunxupeng.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {

        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("loginName: " + loginName);

        // 不管登录与否，都需要取cookie的购物车列表
        // 未登录：页面需要cookie的购物车列表
        // 已登录：往redis同步需要cookie的购物车列表
        String cookieStr = CookieUtil.getCookieValue(request, "cartList", "utf-8");
        if(cookieStr == null || "".equals(cookieStr)) {
            cookieStr = "[]";
        }
        List<Cart> cookie_cartList = JSON.parseArray(cookieStr, Cart.class);

        // 未登陆
        if("anonymousUser".equals(loginName)) {
            return cookie_cartList;
        }
        // 已登录
        else {
            List<Cart> redis_cartList = cartService.findCartListFromRedis(loginName);

            // 将cookie的购物车列表同步到redis中
            if(cookie_cartList.size() > 0) {
                for (Cart cart : cookie_cartList) {
                    for (TbOrderItem orderItem : cart.getOrderItemList()) {
                        redis_cartList = cartService.addGoodsToCartList(redis_cartList, orderItem.getItemId(), orderItem.getNum());
                    }
                }
                System.out.println("cookie_cartList he redis_cartList he bing le");
            }

            // 删除本地cookie的购物车信息
            CookieUtil.deleteCookie(request, response, "cartList");

            // 将改变的redis_cartList的值重新存回redis
            cartService.saveCartListToRedis(loginName, redis_cartList);

            return redis_cartList;
        }
    }

    @RequestMapping("/addCart")
    public Result addCart(Long skuId, Integer num) {
        try {

            response.setHeader("Access-Control-Allow-Origin", "http://localhost:9009");
            response.setHeader("Access-Control-Allow-Credentials", "true");

            List<Cart> oldCartList = findCartList();
            List<Cart> newCartList = cartService.addGoodsToCartList(oldCartList, skuId, num);

            String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
            // 未登陆
            if("anonymousUser".equals(loginName)) {
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(newCartList), 2 * 365 * 3600 * 24, "utf-8");
                System.out.println("save cartList To cookie");
            }
            // 已登陆
            else {
                cartService.saveCartListToRedis(loginName, newCartList);
                System.out.println("save cartList To redis");
            }

            return new Result(true, "添加购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        }
    }

}
