package com.codewolf.sitzungoracle.kafka.consumer.pojo;

public class VotingPojo {
    private String _id;
    private Integer option;
    private Integer power;
    private String from;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Integer getOption() {
        return option;
    }

    public void setOption(Integer option) {
        this.option = option;
    }

    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
