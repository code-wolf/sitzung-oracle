package com.codewolf.sitzungoracle.oracle;

import org.web3j.protocol.core.Response;

public class OracleException extends Exception{
    private Response response;
    private String message;

    public OracleException(Response transaction, String message) {
        this.response = transaction;
        this.message = message;
    }

    public OracleException(String message) {
        this.message = message;
    }

    public Response getResponse() {
        return response;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
