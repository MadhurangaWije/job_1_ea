package com.pavithra.roadsy.request_service;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pavithra.roadsy.User;
import com.pavithra.roadsy.location.Location;

import java.io.Serializable;
import java.util.List;

public class ServiceRequestCall implements Serializable {
    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("data")
    @Expose
    private String dataString;


    private List<RequiredService> requiredServiceList;
    private User client;
    private User serviceProvider;
    private AdditionalServiceRequestDetail additionalServiceRequestDetail;

    public ServiceRequestCall() {

    }

    public ServiceRequestCall(List<RequiredService> requiredServiceList, User client, User serviceProvider, AdditionalServiceRequestDetail additionalServiceRequestDetail) {
        this.requiredServiceList = requiredServiceList;
        this.client = client;
        this.serviceProvider = serviceProvider;
        this.additionalServiceRequestDetail = additionalServiceRequestDetail;
        this.token=serviceProvider.getFcmToken();
        System.out.println("*************************************"+this.token);
        Data data=new Data(requiredServiceList,client,serviceProvider,additionalServiceRequestDetail);
        this.dataString=new Gson().toJson(data);
        System.out.println(this.dataString);
    }

    public List<RequiredService> getRequiredServiceList() {
        return requiredServiceList;
    }

    public void setRequiredServiceList(List<RequiredService> requiredServiceList) {
        this.requiredServiceList = requiredServiceList;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public AdditionalServiceRequestDetail getAdditionalServiceRequestDetail() {
        return additionalServiceRequestDetail;
    }

    public void setAdditionalServiceRequestDetail(AdditionalServiceRequestDetail additionalServiceRequestDetail) {
        this.additionalServiceRequestDetail = additionalServiceRequestDetail;
        stringifyTheJson();
    }

    public User getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(User serviceProvider) {
        this.serviceProvider = serviceProvider;
        this.token=serviceProvider.getFcmToken();
        System.out.println("*************************************"+this.token);
    }

    public void stringifyTheJson(){
        Data data=new Data(requiredServiceList,client,serviceProvider,additionalServiceRequestDetail);
        this.dataString=new Gson().toJson(data);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n\n\n\n\n"+this.dataString);
    }

    class Data{
        private  List<RequiredService> requiredServiceList;
        private  User client;
        private  User serviceProvider;
        private  AdditionalServiceRequestDetail additionalServiceRequestDetail;

        public Data(List<RequiredService> requiredServiceList, User client, User serviceProvider, AdditionalServiceRequestDetail additionalServiceRequestDetail) {
            this.requiredServiceList = requiredServiceList;
            this.client = client;
            this.serviceProvider = serviceProvider;
            this.additionalServiceRequestDetail = additionalServiceRequestDetail;
        }

        public List<RequiredService> getRequiredServiceList() {
            return requiredServiceList;
        }

        public void setRequiredServiceList(List<RequiredService> requiredServiceList) {
            this.requiredServiceList = requiredServiceList;
        }

        public User getClient() {
            return client;
        }

        public void setClient(User client) {
            this.client = client;
        }

        public User getServiceProvider() {
            return serviceProvider;
        }

        public void setServiceProvider(User serviceProvider) {
            this.serviceProvider = serviceProvider;
        }

        public AdditionalServiceRequestDetail getAdditionalServiceRequestDetail() {
            return additionalServiceRequestDetail;
        }

        public void setAdditionalServiceRequestDetail(AdditionalServiceRequestDetail additionalServiceRequestDetail) {
            this.additionalServiceRequestDetail = additionalServiceRequestDetail;
        }
    }
}
