package com.codewolf.sitzungoracle.kafka.consumer.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties
public class SitzungPojo {
    private String _id;
    private String key;
    private String name;
    private String startDate;
    private Boolean isActive;
    private List<AgendaItemPojo> agenda;
    private List<PersonPojo> persons;

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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        this.isActive = active;
    }

    public List<AgendaItemPojo> getAgenda() {
        return agenda;
    }

    public void setAgenda(List<AgendaItemPojo> agenda) {
        this.agenda = agenda;
    }

    public List<PersonPojo> getPersons() {
        return persons;
    }

    public void setPersons(List<PersonPojo> persons) {
        this.persons = persons;
    }
}
