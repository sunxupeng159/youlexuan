package com.sunxupeng.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.sunxupeng.cart.service.CartService;
import com.sunxupeng.group.Cart;
import com.sunxupeng.mapper.TbItemMapper;
import com.sunxupeng.pojo.TbItem;
import com.sunxupeng.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据商品SKU ID查询SKU商品信息
        //2.获取商家ID（页面根据id进行商家分组展示）
        //3.根据商家ID判断购物车列表中是否存在该商家的购物车
        //4.如果购物车列表中不存在该商家的购物车
        //4.1 新建购物车对象
        //4.2 将新建的购物车对象添加到购物车列表
        //5.如果购物车列表中存在该商家的购物车
        // 查询购物车明细列表中是否存在该商品
        //5.1. 如果没有，新增购物车明细
        //5.2. 如果有，在原购物车明细上添加数量，更改金额
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        if(tbItem==null){
            throw new RuntimeException("商品不存在");
        }
        if(!tbItem.getStatus().equals("1")){
            throw new RuntimeException("商品状态无效");
        }
        String sellerId = tbItem.getSellerId();
        Cart cart =searchCartBySellerId(cartList,sellerId);
        if(cart==null){
            cart =new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(tbItem.getSeller());
            TbOrderItem orderItem=createOrderItem(tbItem,num);
            List orderItemList=new ArrayList();
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);

            cartList.add(cart);

        }else{
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            if(orderItem==null){
                orderItem=createOrderItem(tbItem,num);
                cart.getOrderItemList().add(orderItem);
            }else {
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()));
                if(orderItem.getNum()<1){
                    cart.getOrderItemList().remove(orderItem);
                }if(cart.getOrderItemList().size()==0){
                    cartList.remove(cart);
                }
            }

        }
        return cartList;
    }


        @Override
        public List<Cart> findCartListFromRedis(String username) {
            System.out.println("从redis中提取购物车数据：" + username);
            List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
            if (cartList == null) {
                cartList = new ArrayList();
            }
            return cartList;
        }

        @Override
        public void saveCartListToRedis(String username, List<Cart> cartList) {
            System.out.println("向redis存入购物车数据：" + username);
            redisTemplate.boundHashOps("cartList").put(username, cartList);
        }





    private  TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList,Long itemID){
        for (TbOrderItem orderItem : orderItemList) {
            if(orderItem.getItemId().equals(itemID)){
                return orderItem;
            }
        }
        return null;
    }
    private TbOrderItem createOrderItem(TbItem item,Integer num){
        if(num<1){
            throw new RuntimeException("数量不能小于1");
        }
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setItemId(item.getId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setNum(num);
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        orderItem.setPicPath(item.getImage());
        orderItem.setSellerId(item.getSellerId());
        return orderItem;
    }
    private  Cart searchCartBySellerId(List<Cart> cartList,String sellerId){
        for (Cart cart : cartList) {
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }
}
