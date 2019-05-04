package com.pavithra.roadsy.request_service;

import java.io.Serializable;

public class RequiredService implements Serializable {
    private String name;
    private String isRequired;

    public RequiredService() {
        isRequired="false";
    }

    public RequiredService(String name) {
        this.name = name;
        isRequired="false";
    }

    public RequiredService(String name, String isRequired) {
        this.name = name;
        this.isRequired = isRequired;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String isRequired() {
        return isRequired;
    }

    public void setRequired(String required) {
        isRequired = required;
    }
}
