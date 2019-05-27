package com.pavithra.roadsy.request_service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Controller {

    static final String BASE_URL="https://us-central1-com-pavithra-roadsy.cloudfunctions.net/";
    ServiceRequestAPI serviceRequestAPI;

    public Controller() {
        Gson gson = new GsonBuilder().setLenient().excludeFieldsWithoutExposeAnnotation().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        serviceRequestAPI = retrofit.create(ServiceRequestAPI.class);
    }

    public void sendRequest(ServiceRequestCall serviceRequestCall, Callback<Void> serviceRequestCallCallback){
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();

//        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();
//
//        ServiceRequestAPI serviceRequestAPI = retrofit.create(ServiceRequestAPI.class);

        Call<Void> call = serviceRequestAPI.postServiceRequest(serviceRequestCall);
        call.enqueue(serviceRequestCallCallback);
    }

    public void sendStatus(ServiceStatusCall serviceStatusCall,Callback<Void> serviceStatusCallCallback){


        Call<Void> call = serviceRequestAPI.postServiceStatus(serviceStatusCall);
        call.enqueue(serviceStatusCallCallback);
    }


}
