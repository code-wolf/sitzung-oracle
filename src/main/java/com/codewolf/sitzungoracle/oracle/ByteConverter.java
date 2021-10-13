package com.codewolf.sitzungoracle.oracle;

import org.web3j.abi.datatypes.generated.Bytes32;

public final class ByteConverter {
    public static Bytes32 stringToBytes32(String string) {
        byte[] byteValue = string.getBytes();
        byte[] byteValueLen32 = new byte[32];
        System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
        return new Bytes32(byteValueLen32);
    }

    public static Bytes32 bytesToBytes32(byte[] bytes) {
        byte[] byteValueLen32 = new byte[32];
        System.arraycopy(bytes, 0, byteValueLen32, 0, bytes.length);
        return new Bytes32(byteValueLen32);
    }
}
