package com.sunxupeng.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeCancelRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.sunxupeng.pay.service.AliPayService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
@Service
public class AliPayServiceImpl implements AliPayService {

        @Autowired
        private AlipayClient alipayClient;

    @Override
    public Map createCode(String out_trade_no, String total_fee) {
            Map<String, String> map = new HashMap<String, String>();
            // 创建预下单请求对象
            AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
            // 设置业务参数
            request.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                    + "\"total_amount\":\"" + total_fee + "\","
                    + "\"subject\":\"萍萍的小店\","
                    + "\"timeout_express\":\"90m\"}");
            // 发出预下单业务请求
            try {
                AlipayTradePrecreateResponse response = alipayClient.execute(request);
                // 从相应对象读取相应结果
                String code = response.getCode();
                System.out.println("支付宝接口响应码:" + code);
                // 全部的响应结果
                String body = response.getBody();
                System.out.println("支付宝返回结果:" + body);

                if (code.equals("10000")) {
                    map.put("qrcode", response.getQrCode());
                    map.put("out_trade_no", response.getOutTradeNo());
                    map.put("total_fee", total_fee);
                    System.out.println("返回qrcode:" + response.getQrCode());
                    System.out.println("返回out_trade_no:" + response.getOutTradeNo());
                    System.out.println("返回total_fee:" + total_fee);
                } else {
                    System.out.println("预下单接口调用失败:" + body);
                }
            } catch (AlipayApiException e) {
                e.printStackTrace();
            }

            return map;
        }

    @Override
    public Map<String, String> queryPayStatus(String out_trade_no) {

        Map<String, String> map = new HashMap<String, String>();

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();

        // 设置业务参数
        request.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\"}");

        // 发出请求
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);

            String code = response.getCode();

            System.out.println("query pay status return1:" + code);
            System.out.println("query pay status return2:" + response.getBody());

            if (code.equals("10000")) {
                map.put("out_trade_no", out_trade_no);
                map.put("trade_status", response.getTradeStatus());
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return map;

    }

    @Override
    public Map closePay(String out_trade_no) {
        Map<String, String> map = new HashMap<String, String>();
        // 撤销交易请求对象
        AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
        // 设置业务参数
        request.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"trade_no\":\"\"}");

        try {
            AlipayTradeCancelResponse response = alipayClient.execute(request);
            String code = response.getCode();
            System.out.println("关闭支付宝订单，返回值1：" + code);
            System.out.println("关闭支付宝订单，返回值2：" + response.getBody());

            if (code.equals("10000")) {
                map.put("out_trade_no", out_trade_no);
                map.put("code", code);
                return map;
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return null;
    }
}
