package com.sunxupeng;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class smsController {



        @Autowired
        private JmsMessagingTemplate jmsMessagingTemplate;

        @RequestMapping("/sendsms")
        public void sendSms() {
            Map<String, String> map = new HashMap<>();
            map.put("mobile", "15939901316");
            map.put("sign_name", "萍萍");
            map.put("template_code", "SMS_173471056");
            map.put("param", "{\"code\":\"520111\"}");

            jmsMessagingTemplate.convertAndSend("sms", map);
        }

    }