package com.xuecheng.learning.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.exception.XueChengPlusException;
import com.xuecheng.learning.config.PayNotifyConfig;
import com.xuecheng.learning.service.MyCourseTablesService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.channels.Channel;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.learning.service.impl
 * @className: RecivePayNotifyService
 * @author: Caixi
 * @description: TODO
 * @date: 2023/4/9 22:45
 * @version: 1.0
 */

@Service
@Slf4j
public class ReceivePayNotifyService {

    @Autowired
    MyCourseTablesService myCourseTablesService;


    @RabbitListener(queues = PayNotifyConfig.PAYNOTIFY_QUEUE)
    public void receive(Message message){


        // 根据消息更新选课记录表和我的课程表
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        byte[] body = message.getBody();
        String jsonString = new String(body);
        MqMessage mqMessage = JSON.parseObject(jsonString, MqMessage.class);

        String chooseCourseId = mqMessage.getBusinessKey1();
        String orderType = mqMessage.getBusinessKey2();

        if("60201".equals(orderType)){
            boolean b = myCourseTablesService.saveChooseCourseSuccess(chooseCourseId);
            if(!b){
                throw new XueChengPlusException("保存选课记录状态失败");
            }
        }


    }
}
