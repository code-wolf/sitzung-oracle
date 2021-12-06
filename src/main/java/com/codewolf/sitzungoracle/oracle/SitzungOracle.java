package com.codewolf.sitzungoracle.oracle;

import com.codewolf.sitzungoracle.configuration.EthereumConfig;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class SitzungOracle {
    private static final Logger logger = LoggerFactory.getLogger(SitzungOracle.class);
    private final Web3j web3;
    private EthereumConfig configuration;

    @Autowired
    public SitzungOracle(EthereumConfig configuration) {
        this.configuration = configuration;
        OkHttpClient okHttpClient = HttpService.getOkHttpClientBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        web3 = Web3j.build(new HttpService(this.configuration.getUrl(), okHttpClient));
        logger.info("create web3 at " + this.configuration.getUrl());
    }

    public String createSitzung(Sitzung sitzung, List<AgendaItem> agenda, List<Voter> voters) throws OracleException {
        logger.info("Send Sitzung " + sitzung.getName() + " to chain [oracle_address=" + configuration.getOracle_address() + ", account=" + configuration.getAccount() + ", pk=" + configuration.getPrivate_key() + "]");
        try {
            Credentials credentials = Credentials.create(configuration.getPrivate_key());

            List<Voter> _voters = getVoters();
            List<AgendaItem> _agenda = getAgenda();

            Function function = new Function("createSitzung",
                    Arrays.asList(sitzung, new DynamicArray(Voter.class, voters), new DynamicArray(AgendaItem.class, agenda)),
                    Collections.emptyList());

            String encodedFunction = FunctionEncoder.encode(function);

            String transactionHash = sendTransaction(encodedFunction, credentials);
            logger.info("executed transaction with hash " + transactionHash);
            return transactionHash;
        } catch(Exception e) {
            // maybe incorrect oracle address?
            throw new OracleException(e.getMessage());
        }
    }

    public String addSitzung(Sitzung sitzung) throws OracleException {
        logger.info("Add Sitzung " + sitzung.getName() + " to chain [oracle_address=" + configuration.getOracle_address() + ", account=" + configuration.getAccount() + ", pk=" + configuration.getPrivate_key() + "]");
        try {
            Credentials credentials = Credentials.create(configuration.getPrivate_key());

            Function function = new Function("addSitzung",
                    Arrays.asList(sitzung),
                    Collections.emptyList());

            String encodedFunction = FunctionEncoder.encode(function);

            String transactionHash = sendTransaction(encodedFunction, credentials);
            logger.info("executed transaction with hash " + transactionHash);
            return transactionHash;
        } catch(Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            logger.error("unable to add sitzung: " + e.getMessage());
            // maybe incorrect oracle address?
            throw new OracleException(e.getMessage());
        }
    }

    public List<String> addVoters(Sitzung sitzung, List<Voter> voters) {
        logger.info("Adding " + voters.size() + " voter(s) to chain");

        Credentials credentials = Credentials.create(configuration.getPrivate_key());

        return voters.stream().map(voter -> {
            Function function = new Function("addVoter",
                    Arrays.asList(sitzung.getId(), voter),
                    Collections.emptyList());

            String encodedFunction = FunctionEncoder.encode(function);
            try {
                String transactionHash = sendTransaction(encodedFunction, credentials);
                logger.info("executed voter transaction with hash " + transactionHash);
                return transactionHash;
            } catch(Exception e) {
                logger.error(e.getMessage());
                // maybe incorrect oracle address?
                return null;
            }
        }).collect(Collectors.toList());
    }

    public List<String> addAgendaItem(Sitzung sitzung, List<AgendaItem> items) {
        logger.info("Adding " + items.size() + " agenda item(s) to chain");

        Credentials credentials = Credentials.create(configuration.getPrivate_key());

        return items.stream().map(item -> {
            Function function = new Function("addAgendaItem",
                    Arrays.asList(sitzung.getId(), item),
                    Collections.emptyList());

            String encodedFunction = FunctionEncoder.encode(function);
            try {
                String transactionHash = sendTransaction(encodedFunction, credentials);
                logger.info("executed agenda transaction with hash " + transactionHash);
                return transactionHash;
            } catch(Exception e) {
                // maybe incorrect oracle address?
                logger.error(e.getMessage());
                return null;
            }
        }).collect(Collectors.toList());
    }

    /**
     *
     * @param function An encoded function
     * @return Transaction hash of the transaction
     */
    private String sendTransaction(String function, Credentials credentials) throws Exception {
        //StaticGasProvider gas = getGas(function);
        BigInteger estimatedGas = estimateGas(function);


        BigInteger gasPrice = BigInteger.ZERO;
        BigInteger gasLimit = estimatedGas;

        TransactionManager txManager = new RawTransactionManager(web3, credentials);
        EthSendTransaction tx = txManager.sendTransaction(
                gasPrice,
                gasLimit,
                configuration.getOracle_address(),
                function,
                BigInteger.ZERO);

        if(tx.hasError()) {
            logger.error("unable to send transaction: ", tx.getError().getMessage());
            throw new OracleException(tx, tx.getError().getMessage());
        }

        return tx.getTransactionHash();
    }

    private StaticGasProvider getGas(String function) throws OracleException {
        BigInteger estimatedGas = estimateGas(function);


        BigInteger gasPrice = DefaultGasProvider.GAS_PRICE;
        BigInteger gasLimit = estimatedGas;

        return new StaticGasProvider(gasPrice, gasLimit);
    }

    private BigInteger estimateGas(String encodedFunction) throws OracleException {
        try {
            EthEstimateGas ethEstimateGas = web3.ethEstimateGas(Transaction.createEthCallTransaction(configuration.getAccount(),
                            configuration.getOracle_address(),
                            encodedFunction)).sendAsync().get();
            if(ethEstimateGas.hasError()) {
                throw new OracleException(ethEstimateGas.getError().getMessage());
            }

            BigInteger amount = ethEstimateGas.getAmountUsed();
            return amount;
        } catch (Exception e) {
            throw new OracleException(e.getMessage());
        }
    }

    public static Bytes32 stringToBytes32(String string) {
        byte[] byteValue = string.getBytes();
        byte[] byteValueLen32 = new byte[32];
        System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
        return new Bytes32(byteValueLen32);
    }

    private List<Voter> getVoters() {
        Voter v1 = new Voter("0x3b6AA73D8C8c92bf0Ce10ba8895A3ef98237e1D7",
                "Shawn",
                "Gunthorp",
                "SPO",
                "d93152a57558494295d6945141e79f3e");

        ArrayList<Voter> result = new ArrayList<>();
        result.add(v1);

        return result;
    }

    private List<AgendaItem> getAgenda() {
        AgendaItem item1 = new AgendaItem("Item 1", "Agendaitem 1", "123455");
        ArrayList<AgendaItem> result = new ArrayList<>();
        result.add(item1);

        return result;
    }
}
