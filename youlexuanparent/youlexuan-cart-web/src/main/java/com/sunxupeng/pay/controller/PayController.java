package com.sunxupeng.pay.controller;




import com.alibaba.dubbo.config.annotation.Reference;


import com.sunxupeng.entity.Result;
import com.sunxupeng.order.service.OrderService;
import com.sunxupeng.pay.service.AliPayService;
import com.sunxupeng.pojo.TbPayLog;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private AliPayService aliPayService;

    @Reference
    private OrderService orderService;


    @RequestMapping("/createCode")
    public Map createCode() {

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLog = orderService.searchPayLogFromRedis(userId);

        // 利用String方法，将金额保留为2位小数
        // String.format("%.2f", payLog.getTotalFee());
        if(payLog!=null){
            return aliPayService.createCode(payLog.getOutTradeNo(), String.format("%.2f", payLog.getTotalFee()));
        }else{
            return new HashMap();
        }
    }

    /**
     * 检测支状态
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {

        Result result=null;
        int x=0;
        while(true){
            //调用查询接口
            Map<String,String> map = aliPayService.queryPayStatus(out_trade_no);
            if(map==null){//出错
                result=new  Result(false, "支付出错");
                break;
            }
            if (map.get("trade_status") != null && map.get("trade_status").equals("TRADE_SUCCESS")) {
                result=new  Result(true, "支付成功");
                //修改订单状态
                orderService.updateOrderStatus(out_trade_no, map.get("transaction_id"));
                break;
            }
            try {
                Thread.sleep(3000);//间隔三秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            // 如果变量超过设定值退出循环，超时为3分钟
            x++;
            if (x >= 20) {
                result = new Result(false, "交易超时二维码过期");
                break;
            }
        }
        return result;
    }

}
