package com.codewolf.sitzungoracle.oracle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class SitzungOracle {
    private static final Logger logger = LoggerFactory.getLogger(SitzungOracle.class);
    private static final String ONCHAIN_ORACLE_ADDRESS = "0xe4abD1590E4f67441586626739b9aaebe64f8E92";
    private static final String ACCOUNT = "0xE3c0335ABb6ec86DD13BB01Ff63762F00aec31c7";
    Web3j web3;
    public SitzungOracle() {
        web3 = Web3j.build(new HttpService("http://localhost:7545"));
    }

    public void createSitzung() throws Exception {
        try {
            // Address: 0x130087C2Ede0c5E62Eb80414DEa19Bf7A3087EE3
            //Credentials creds = Credentials.create("de941bace8537cbc11f20407e0aab989db7da2109edd0792e66cde5916b84201");

            //0xE3c0335ABb6ec86DD13BB01Ff63762F00aec31c7
            Credentials creds = Credentials.create("b6e5f67e5474372d16e90ad72392383ceee0e6f9c47f288ed6288cac445ce439");

            String id = "1234";

            List<Voter> voters = getVoters();


            Sitzung sitzung = new Sitzung("1234", "Gemeinderat");

            /*
            Function function = new Function("createSitzung",
                    Arrays.asList(sitzung, new DynamicArray(Voter.class, voters)),
                    Collections.emptyList());
*/
            Function function = new Function("testSitzung",
                    Collections.emptyList(),
                    Collections.emptyList());

            String encodedFunction = FunctionEncoder.encode(function);

            BigInteger estimatedGas = estimateGas(encodedFunction);

            BigInteger nonce = getNonce();
            //BigInteger gasPrice = DefaultGasProvider.GAS_PRICE;
            //BigInteger gasLimit = DefaultGasProvider.GAS_LIMIT;
            //BigInteger gasLimit = BigInteger.valueOf(900000);
            BigInteger gasPrice = BigInteger.ZERO;
            BigInteger gasLimit = estimatedGas;

            /*
            TransactionManager txManager = new RawTransactionManager(web3, creds);
            EthSendTransaction tx = txManager.sendTransaction(
                    gasPrice,
                    gasLimit,
                    ONCHAIN_ORACLE_ADDRESS,
                    encodedFunction,
                    BigInteger.ZERO);
            String txHash = tx.getTransactionHash();
               */

            Transaction transaction = Transaction.createFunctionCallTransaction(
                    ACCOUNT,
                    nonce,
                    gasPrice,
                    gasLimit,
                    ONCHAIN_ORACLE_ADDRESS,
                    BigInteger.valueOf(0),
                    encodedFunction);

            EthSendTransaction tx = web3.ethSendTransaction(transaction).sendAsync().get();

            if(tx.hasError()) {
                throw new Exception(tx.getError().getMessage());
            }
            String transactionHash = tx.getTransactionHash();
            logger.info("executed transaction with hash " + transactionHash);
        } catch(Exception e) {
            throw e;
        }
    }

    private BigInteger getNonce() throws Exception {
        EthGetTransactionCount count = web3.ethGetTransactionCount(ACCOUNT, DefaultBlockParameterName.LATEST)
                .sendAsync()
                .get();
        BigInteger nonce = count.getTransactionCount();
        return nonce;
    }

    private BigInteger estimateGas(String encodedFunction) throws Exception {
        EthEstimateGas ethEstimateGas = web3.ethEstimateGas(
                        Transaction.createEthCallTransaction(ACCOUNT, ONCHAIN_ORACLE_ADDRESS, encodedFunction))
                .sendAsync().get();

        // this was coming back as 50,000,000 which is > the block gas limit of 4,712,388
        // see eth.getBlock("latest")
        BigInteger amount = ethEstimateGas.getAmountUsed();
        return amount;
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
}
