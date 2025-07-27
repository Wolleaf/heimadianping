package com.hmdp.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//@Component
public class KafkaConsumerServiceTest {

    @KafkaListener(topics = "hmdp", groupId = "hmdp-group")
    public void onMessage(String message) {
        System.out.println("接收到消息：" + message);
    }

    @KafkaListener(topics = "binlog-blog", groupId = "hmdp-group")
    public void onCdcMessage(String message) {
        System.out.println("接收到消息：" + message);
    }
}
