package com.pavithra.roadsy;

import com.pavithra.roadsy.location.Location;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String email;
    private String type;
    private String telephone;
    private String fcmToken;

    private String businessRegistrationNumber;

    private Location location;



    public User() {
    }

    public User(String name, String email,String type, String telephone) {
        this.name = name;
        this.email = email;
        this.type=type;
        this.telephone = telephone;
    }

    public User(String name, String email,String type, String telephone,String fcmToken,Location location) {
        this.name = name;
        this.email = email;
        this.type=type;
        this.telephone = telephone;
        this.fcmToken=fcmToken;
        this.location=location;
    }

    public User(String name, String email, String type, String telephone, String businessRegistrationNumber) {
        this.name = name;
        this.email = email;
        this.type = type;
        this.telephone = telephone;
        this.businessRegistrationNumber = businessRegistrationNumber;
    }

    public User(String name, String email, String type, String telephone, String fcmToken, String businessRegistrationNumber, Location location) {
        this.name = name;
        this.email = email;
        this.type = type;
        this.telephone = telephone;
        this.fcmToken = fcmToken;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBusinessRegistrationNumber() {
        return businessRegistrationNumber;
    }

    public void setBusinessRegistrationNumber(String businessRegistrationNumber) {
        this.businessRegistrationNumber = businessRegistrationNumber;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
