package com.codewolf.sitzungoracle.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties("ethereum")
public class EthereumConfig {
    private String oracle_address;
    private String private_key;
    private String account;
    private String url;


    public EthereumConfig() {
        System.out.println("config");
    }

    public String getOracle_address() {
        return oracle_address;
    }

    public void setOracle_address(String oracle_address) {
        this.oracle_address = oracle_address;
    }

    public String getPrivate_key() {
        return private_key;
    }

    public void setPrivate_key(String private_key) {
        this.private_key = private_key;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
