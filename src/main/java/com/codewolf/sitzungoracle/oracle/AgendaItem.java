package com.codewolf.sitzungoracle.oracle;

import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;

public final class AgendaItem extends DynamicStruct {
    public byte[] id;

    public String name;

    public AgendaItem(byte[] id, String name) {
        super(new org.web3j.abi.datatypes.generated.Bytes32(id),new org.web3j.abi.datatypes.Utf8String(name));
        this.id = id;
        this.name = name;
    }

    public AgendaItem(Bytes32 id, Utf8String name) {
        super(id,name);
        this.id = id.getValue();
        this.name = name.getValue();
    }
}