package com.codewolf.sitzungoracle.kafka.consumer;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SitzungConsumer {

    @PostConstruct
    public void start() {
        System.out.println("start client");
    }
}
