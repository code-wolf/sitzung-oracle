package com.codewolf.sitzungoracle.kafka.consumer.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties
public class AgendaItemPojo {
    private String _id;
    private String key;
    private String aktZahl;
    private Integer aktTypId;
    private String aktTyp;
    private String aktBetreff;
    private Integer optionType;

    public List<VotingPojo> getVotings() {
        return votings;
    }

    public void setVotings(List<VotingPojo> votings) {
        this.votings = votings;
    }

    private List<VotingPojo> votings;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAktZahl() {
        return aktZahl;
    }

    public void setAktZahl(String aktZahl) {
        this.aktZahl = aktZahl;
    }

    public Integer getAktTypId() {
        return aktTypId;
    }

    public void setAktTypId(Integer aktTypId) {
        this.aktTypId = aktTypId;
    }

    public String getAktTyp() {
        return aktTyp;
    }

    public void setAktTyp(String aktTyp) {
        this.aktTyp = aktTyp;
    }

    public String getAktBetreff() {
        return aktBetreff;
    }

    public void setAktBetreff(String aktBetreff) {
        this.aktBetreff = aktBetreff;
    }

    public Integer getOptionType() {
        return optionType;
    }

    public void setOptionType(Integer optionType) {
        this.optionType = optionType;
    }
}
