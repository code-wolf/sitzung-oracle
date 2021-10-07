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
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
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

    public void createSitzung() {
        try {
            Function function = new Function("createSitzung",
                    Arrays.asList(new Utf8String("Oracle")),
                    Collections.<TypeReference<?>>emptyList());
            String encodedFunction = FunctionEncoder.encode(function);
            BigInteger nonce = getNonce();
            BigInteger gasPrice = DefaultGasProvider.GAS_PRICE;
            BigInteger gasLimit = DefaultGasProvider.GAS_LIMIT;

            Transaction transaction = Transaction.createFunctionCallTransaction(
                    ACCOUNT,
                    nonce,
                    gasPrice,
                    gasLimit,
                    ONCHAIN_ORACLE_ADDRESS,
                    BigInteger.valueOf(0),
                    encodedFunction);

            EthSendTransaction response = web3.ethSendTransaction(transaction).sendAsync().get();
            String transactionHash = response.getTransactionHash();
            logger.info("executed transaction with hash " + transactionHash);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private BigInteger getNonce() throws Exception {
        EthGetTransactionCount count = web3.ethGetTransactionCount(ACCOUNT, DefaultBlockParameterName.LATEST)
                .sendAsync()
                .get();
        BigInteger nonce = count.getTransactionCount();
        return nonce;
    }
}
