package com.codewolf.sitzungoracle.oracle;

import okio.Utf8;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;

public final class Voter extends DynamicStruct {
    public String addr;
    public String firstName;
    public String lastName;
    public String party;
    public String key;

    public Voter(String addr, String firstName, String lastName, String party, String key) {
        super(new Address(addr),
                new Utf8String(firstName),
                new Utf8String(lastName),
                new Utf8String(party),
                new Utf8String(key));

        this.addr = addr;
        this.firstName = firstName;
        this.lastName = lastName;
        this.party = party;
        this.key = key;
    }

    public Voter(Address addr, Utf8String firstName, Utf8String lastName, Utf8String party, Utf8String key) {
        super(addr, firstName, lastName, party, key);

        this.addr = addr.getValue();
        this.firstName = firstName.getValue();
        this.lastName = lastName.getValue();
        this.party = party.getValue();
        this.key = key.getValue();
    }
}