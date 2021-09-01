package com.yangtzelsl.tmall.logger.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LoggerController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Value("${kafka.topic}")
    private String kafkaTopic;

    @RequestMapping("/applog")
    public String logger(String param) {
        //打印日志
        log.info(param);
        //发送至kafka
        kafkaTemplate.send(kafkaTopic, param);
        return "success";
    }

}

