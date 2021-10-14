package com.codewolf.sitzungoracle.kafka.consumer;

import com.codewolf.sitzungoracle.SitzungOracleApplication;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

@Component
public class SitzungConsumer {
    private static Logger logger = LoggerFactory.getLogger(SitzungConsumer.class);
    private static final String STREAM_NAME_SITZUNGEN = "sitzungen";
    private static final String STREAM_NAME_LOG = "voting-log";
    private KafkaStreams streams;

    @PostConstruct
    public void start() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-sitzungen");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> sitzungStream = builder.stream(STREAM_NAME_SITZUNGEN);
        KStream<String, String> logStream = builder.stream(STREAM_NAME_LOG);
        sitzungStream.to(STREAM_NAME_LOG);

        streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }

    public void stop() {
        streams.close();
    }
}
