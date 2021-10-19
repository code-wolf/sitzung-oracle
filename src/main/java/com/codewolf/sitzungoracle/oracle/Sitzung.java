package com.codewolf.sitzungoracle.oracle;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;

public class Sitzung extends DynamicStruct {
    public byte[] id;
    public String name;
    public String startDate;
    public Boolean isActive;

    public Sitzung(byte id[], String name, String startDate, Boolean isActive) {
        super(ByteConverter.bytesToBytes32(id), new Utf8String(name), new Utf8String(startDate), new Bool(isActive));

        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.isActive = isActive;
    }

    public Sitzung(String id, String name, String startDate, Boolean isActive) {
        super(ByteConverter.stringToBytes32(id), new Utf8String(name), new Utf8String(startDate), new Bool(isActive));

        this.id = id.getBytes();
        this.name = name;
        this.startDate = startDate;
        this.isActive = isActive;
    }

    public Sitzung(Bytes32 id, Utf8String name, Utf8String startDate, Bool isActive) {
        super(id, name, startDate, isActive);

        this.id = id.getValue();
        this.name = name.getValue();
        this.startDate = startDate.getValue();
        this.isActive = isActive.getValue();
    }

    public Bytes32 getId() {
        return ByteConverter.bytesToBytes32(id);
    }

    public void setId(Bytes32 id) {
        this.id = id.getValue();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
