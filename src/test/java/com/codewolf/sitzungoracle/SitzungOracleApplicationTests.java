package com.codewolf.sitzungoracle;

import com.codewolf.sitzungoracle.oracle.SitzungOracle;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

@SpringBootTest
class SitzungOracleApplicationTests {
    @Autowired
    SitzungOracle oracle;

    @Test
    void contextLoads() {

    }

    @Test
    void testClientVersion() {
        try {
            oracle.createSitzung();
        } catch(Exception e) {

        }
    }
}
