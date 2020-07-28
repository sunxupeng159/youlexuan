package com.sunxupeng.listener;

import com.sunxupeng.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class PageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage obj = (ObjectMessage)message;
            Long[] ids = (Long[])obj.getObject();

            for (Long id : ids) {
                itemPageService.genItemHtml(id);
            }

            System.out.println(">>>>>receive create page msg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }

