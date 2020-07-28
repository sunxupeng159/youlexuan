package com.sunxupeng.cart.service;

import com.sunxupeng.group.Cart;

import java.util.List;

public interface CartService {
    List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);

    public List<Cart> findCartListFromRedis(String username);

    public void saveCartListToRedis(String username,List<Cart> cartList);
}
