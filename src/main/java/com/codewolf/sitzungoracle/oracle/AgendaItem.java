package com.codewolf.sitzungoracle.oracle;

import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;

public final class AgendaItem extends DynamicStruct {
    public byte[] id;
    public String name;
    public String aktZahl;
    public Boolean active;

    public AgendaItem(String id, String name, String aktZahl, Boolean active) {
        super(ByteConverter.stringToBytes32(id), new Utf8String(name), new Utf8String(aktZahl), new Bool(active));

        this.id = id.getBytes();
        this.name = name;
        this.aktZahl = aktZahl;
        this.active = active;
    }

    public AgendaItem(Bytes32 id, Utf8String name, Utf8String aktZahl, Bool active) {
        super(id,name);
        this.id = id.getValue();
        this.name = name.getValue();
        this.aktZahl = aktZahl.getValue();
        this.active = active.getValue();
    }
}