package com.codewolf.sitzungoracle.kafka.consumer;

import com.codewolf.sitzungoracle.SitzungOracleApplication;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Component
public class SitzungConsumer {
    private static Logger logger = LoggerFactory.getLogger(SitzungConsumer.class);

    @PostConstruct
    public void start() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-sitzungen");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        logger.info("start client");
        final KafkaStreams streams;
    }
}
