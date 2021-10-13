package com.codewolf.sitzungoracle.oracle;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;

public class Sitzung extends DynamicStruct {
    public byte[] id;
    public String name;

    public Sitzung(byte id[], String name) {
        super(ByteConverter.bytesToBytes32(id), new Utf8String(name));

        this.id = id;
        this.name = name;
    }

    public Sitzung(String id, String name) {
        super(ByteConverter.stringToBytes32(id), new Utf8String(name));

        this.id = id.getBytes();
        this.name = name;
    }

    public Sitzung(Bytes32 id, Utf8String name) {
        super(id, name);

        this.id = id.getValue();
        this.name = name.getValue();
    }
}
