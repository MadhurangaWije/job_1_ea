package com.pavithra.roadsy.request_service;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServiceRequestAPI {
    @POST("helloWorld")
    Call<Void> postServiceRequest(@Body ServiceRequestCall serviceRequestCall);

    @POST("helloWorld2")
    Call<Void> postServiceStatus(@Body ServiceStatusCall serviceStatusCall);
}
