package com.sunxupeng.service;


import com.alibaba.dubbo.config.annotation.Reference;
import com.sunxupeng.pojo.TbSeller;
import com.sunxupeng.sellergoods.service.SellerService;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

class UserDetailsServiceImpl implements UserDetailsService {


    @Reference
    private SellerService sellerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("----经过UserDetailsServiceImpl----");
        // 构建角色列表
        List<GrantedAuthority> grantAuths = new ArrayList<GrantedAuthority>();
        grantAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        // 得到商家对象
        TbSeller seller = sellerService.findOne(username);

        if (seller != null) {
            // 审核通过
            if (seller.getStatus().equals("1")) {
                return new User(username, seller.getPassword(), grantAuths);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}

