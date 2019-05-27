package com.pavithra.roadsy.request_service;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ServiceStatusCall implements Serializable {
    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("data")
    @Expose
    private String dataString;
    private String status;

    public ServiceStatusCall() {
    }

    public ServiceStatusCall(String status,String token) {
        this.status = status;
        this.token=token;
        if(status!=null) {
            Data data = new Data(status);
            this.dataString = new Gson().toJson(data);
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDataString() {
        return dataString;
    }

    public void setDataString(String dataString) {
        this.dataString = dataString;
    }

    class Data{
        private String status;

        public Data() {
        }

        public Data(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
