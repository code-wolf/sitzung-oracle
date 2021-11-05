package com.codewolf.sitzungoracle.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("kafka")
public class KafkaConfig {
    private String bootstrap_server;
    private String application_id;

    public KafkaConfig() { }

    public String getBootstrap_server() {
        return bootstrap_server;
    }

    public void setBootstrap_server(String bootstrap_server) {
        this.bootstrap_server = bootstrap_server;
    }

    public String getApplication_id() {
        return application_id;
    }

    public void setApplication_id(String application_id) {
        this.application_id = application_id;
    }
}
