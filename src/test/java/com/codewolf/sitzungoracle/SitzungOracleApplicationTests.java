package com.codewolf.sitzungoracle;

import com.codewolf.sitzungoracle.configuration.EthereumConfig;
import com.codewolf.sitzungoracle.oracle.AgendaItem;
import com.codewolf.sitzungoracle.oracle.Sitzung;
import com.codewolf.sitzungoracle.oracle.SitzungOracle;
import com.codewolf.sitzungoracle.oracle.Voter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
class SitzungOracleApplicationTests {
    @Autowired
    SitzungOracle oracle;

    @Autowired
    private EthereumConfig configuration;

    @Test
    void contextLoads() {

    }

    @Test
    void testCreateSitzung() {
        try {
            oracle.createSitzung(getSitzung(), getAgenda(1), getVoters(1));
        } catch(Exception e) {

        }
    }

    @Test
    void testAddSitzung() {
        try {
            Sitzung sitzung = getSitzung();
            List<Voter> voters = getVoters(1);
            List<AgendaItem> agenda = getAgenda(1);
            String tx = oracle.addSitzung(sitzung);
            List<String> txs = oracle.addVoters(sitzung, voters);
            txs = oracle.addAgendaItem(sitzung, agenda);
        } catch (Exception e) {

        }

    }

    private Sitzung getSitzung() {
        Date now = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Sitzung sitzung = new Sitzung("1234", "Gemeinderat", format.format(now), true);
        return sitzung;
    }

    private List<Voter> getVoters(Integer numItems) {
        ArrayList<Voter> result = new ArrayList<>();
        for(int i = 0; i < numItems; i++) {
            result.add(new Voter("0x3b6AA73D8C8c92bf0Ce10ba8895A3ef98237e1D7",
                    "Shawn_" + 1,
                    "Gunthorp_" + 1,
                    "SPO",
                    "d93152a57558494295d6945141e79f3" + i));
        }


        return result;
    }

    private List<AgendaItem> getAgenda(Integer numItems) {
        ArrayList<AgendaItem> result = new ArrayList<>();
        for(int i = 0; i < numItems; i++)
        {
            result.add(new AgendaItem("Item " + i, "Agendaitem " + i, "123455" + 1));
        }

        return result;
    }
}
