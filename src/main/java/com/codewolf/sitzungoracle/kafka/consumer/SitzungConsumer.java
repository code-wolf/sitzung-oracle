package com.codewolf.sitzungoracle.kafka.consumer;

import com.codewolf.sitzungoracle.SitzungOracleApplication;
import com.codewolf.sitzungoracle.kafka.consumer.pojo.SitzungPojo;
import com.codewolf.sitzungoracle.kafka.consumer.serde.JsonPOJODeserializer;
import com.codewolf.sitzungoracle.kafka.consumer.serde.JsonPOJOSerializer;
import com.codewolf.sitzungoracle.oracle.OracleException;
import com.codewolf.sitzungoracle.oracle.Sitzung;
import com.codewolf.sitzungoracle.oracle.SitzungOracle;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

@Component
public class SitzungConsumer {
    private static Logger logger = LoggerFactory.getLogger(SitzungConsumer.class);
    private static final String STREAM_NAME_SITZUNGEN = "sitzungen";
    private static final String STREAM_NAME_LOG = "voting-log";
    private static final String STREAM_KEY_SITZUNG = "sitzung";
    private static final String STREAM_KEY_ERROR = "sitzung";

    private KafkaStreams streams;

    @Autowired
    private SitzungOracle oracle;

    @PostConstruct
    public void start() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-sitzungen");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        /*
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        */

        Map<String, Object> serdeProps = new HashMap<>();

        final Serializer<SitzungPojo> sitzungSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", SitzungPojo.class);
        sitzungSerializer.configure(serdeProps, false);

        final Deserializer<SitzungPojo> sitzungDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", SitzungPojo.class);
        sitzungDeserializer.configure(serdeProps, false);

        final Serde<SitzungPojo> sitzungSerde = Serdes.serdeFrom(sitzungSerializer, sitzungDeserializer);

        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, SitzungPojo> sitzungPojoStream = builder
                .stream(STREAM_NAME_SITZUNGEN, Consumed.with(Serdes.String(), sitzungSerde));

        sitzungPojoStream.to(STREAM_NAME_LOG);


        KStream<String, String> resultStream = sitzungPojoStream
                .map(this::mapSitzung)
                .map((key, value) -> {
                    try {
                        String txHash = oracle.createSitzung(value);
                    } catch (OracleException e) {
                        e.printStackTrace();
                    }

                    return KeyValue.pair(key, "result");
                });

        streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }

    @PreDestroy
    public void stop() {
        streams.close();
    }

    private KeyValue<String, Sitzung> mapSitzung(String key, SitzungPojo pojo) {
        Sitzung sitzung = new Sitzung(pojo.getKey(), pojo.getName());
        return KeyValue.pair(key, sitzung);
    }
}
