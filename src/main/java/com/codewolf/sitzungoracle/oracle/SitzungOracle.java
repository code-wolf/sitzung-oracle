package com.codewolf.sitzungoracle.oracle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
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
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

@Component
public class SitzungOracle {
    private static final Logger logger = LoggerFactory.getLogger(SitzungOracle.class);
    private static final String ONCHAIN_ORACLE_ADDRESS = "0x1270a09197095FE0b021F35320d96261fb6956B2";
    private static final String ACCOUNT = "0x130087C2Ede0c5E62Eb80414DEa19Bf7A3087EE3";
    Web3j web3;
    public SitzungOracle() {
        web3 = Web3j.build(new HttpService());
    }

    public void createSitzung() throws Exception {
        try {
            Credentials creds = Credentials.create("de941bace8537cbc11f20407e0aab989db7da2109edd0792e66cde5916b84201");

            Function function = new Function("createSitzung",
                    Arrays.asList(new Utf8String("Oracle")),
                    Collections.emptyList());
            String encodedFunction = FunctionEncoder.encode(function);

            //BigInteger estimatedGas = estimateGas(encodedFunction);

            BigInteger nonce = getNonce();
            BigInteger gasPrice = DefaultGasProvider.GAS_PRICE;
            //BigInteger gasLimit = DefaultGasProvider.GAS_LIMIT;
            BigInteger gasLimit = BigInteger.valueOf(900000);


            TransactionManager txManager = new FastRawTransactionManager(web3, creds);
            EthSendTransaction tx = txManager.sendTransaction(gasPrice, gasLimit, ONCHAIN_ORACLE_ADDRESS, encodedFunction, BigInteger.ZERO);
            String txHash = tx.getTransactionHash();


            /*
            Transaction transaction = Transaction.createFunctionCallTransaction(
                    ACCOUNT,
                    nonce,
                    gasPrice,
                    gasLimit,
                    ONCHAIN_ORACLE_ADDRESS,
                    BigInteger.valueOf(0),
                    encodedFunction);

            EthSendTransaction tx = web3.ethSendTransaction(transaction).sendAsync().get();
            */

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
                        Transaction.createEthCallTransaction(ACCOUNT, null, encodedFunction))
                .sendAsync().get();

        // this was coming back as 50,000,000 which is > the block gas limit of 4,712,388
        // see eth.getBlock("latest")
        return ethEstimateGas.getAmountUsed().divide(BigInteger.valueOf(100));
    }
}
