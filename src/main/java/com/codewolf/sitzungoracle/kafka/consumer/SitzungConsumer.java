package com.codewolf.sitzungoracle.kafka.consumer;

import com.codewolf.sitzungoracle.SitzungOracleApplication;
import com.codewolf.sitzungoracle.configuration.KafkaConfig;
import com.codewolf.sitzungoracle.kafka.consumer.pojo.AgendaItemPojo;
import com.codewolf.sitzungoracle.kafka.consumer.pojo.SitzungPojo;
import com.codewolf.sitzungoracle.kafka.consumer.serde.JsonPOJODeserializer;
import com.codewolf.sitzungoracle.kafka.consumer.serde.JsonPOJOSerializer;
import com.codewolf.sitzungoracle.oracle.*;
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
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Component
public class SitzungConsumer {
    private static Logger logger = LoggerFactory.getLogger(SitzungConsumer.class);
    private static final String STREAM_NAME_SITZUNGEN = "sitzungen";
    private static final String STREAM_NAME_LOG = "voting-log";
    private static final String STREAM_KEY_SITZUNG = "sitzung";
    private static final String STREAM_KEY_AGENDA = "agenda";
    private static final String STREAM_KEY_VOTERS = "voters";
    private static final String STREAM_KEY_ERROR = "sitzung";

    private KafkaStreams streams;

    private KafkaConfig configuration;

    @Autowired
    private SitzungOracle oracle;

    @Autowired
    public SitzungConsumer(KafkaConfig configuration) {
        this.configuration = configuration;
    }

    @PostConstruct
    public void start() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, this.configuration.getApplication_id());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, this.configuration.getBootstrap_server());

        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

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

        // extract sitzung, agenda and voters and send it to the blockchain
        KStream<String, String> resultStream = sitzungPojoStream
                .map((key, sitzungPojo) -> {
                    Sitzung sitzung = new Sitzung(sitzungPojo.getKey(), sitzungPojo.getName(), sitzungPojo.getStartDate(), sitzungPojo.getIsActive());
                    List<AgendaItem> agenda = sitzungPojo.getAgenda()
                            .stream()
                            .map(x -> new AgendaItem(x.get_id(), x.getAktBetreff(), x.getAktZahl()))
                            .collect(Collectors.toList());
                    List<Voter> voters = sitzungPojo.getPersons()
                            .stream()
                            .map(x -> new Voter(x.getEthereumAddress(), x.getFirstName(), x.getLastName(), x.getParty(), x.getKey()))
                            .collect(Collectors.toList());


                    try {
                        String sitzungTxHash = oracle.addSitzung(sitzung);
                        List<String> votersTxHash = oracle.addVoters(sitzung, voters);
                        List<String> agendaTxHash = oracle.addAgendaItem(sitzung, agenda);
                        return KeyValue.pair(key, sitzungTxHash);
                    } catch(OracleException e) {
                        return KeyValue.pair(STREAM_KEY_ERROR, e.getMessage());
                    }
                });

        // send the result to the logs
        resultStream.to(STREAM_NAME_LOG);
        streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }

    private List<KeyValue<String, Object>> splitSitzung(String key, SitzungPojo sitzungPojo) {
        List<KeyValue<String, Object>> result = new ArrayList<>();
        Sitzung sitzung = new Sitzung(sitzungPojo.getKey(), sitzungPojo.getName(), sitzungPojo.getStartDate(), sitzungPojo.getIsActive());
        List<AgendaItem> agenda = sitzungPojo.getAgenda()
                .stream()
                .map(x -> new AgendaItem(x.get_id(), x.getAktBetreff(), x.getAktZahl()))
                .collect(Collectors.toList());
        List<Voter> voters = sitzungPojo.getPersons()
                .stream()
                .map(x -> new Voter(x.getEthereumAddress(), x.getFirstName(), x.getLastName(), x.getParty(), x.getKey()))
                .collect(Collectors.toList());

        result.add(KeyValue.pair(STREAM_KEY_SITZUNG, sitzung));
        result.add(KeyValue.pair(STREAM_KEY_AGENDA, agenda));
        result.add(KeyValue.pair(STREAM_KEY_VOTERS, voters));

        return result;
    }

    @PreDestroy
    public void stop() {
        streams.close();
    }
}
