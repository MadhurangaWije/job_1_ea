package com.pavithra.roadsy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.pavithra.roadsy.request_service.ServiceRequestCall;

public class ServiceProviderContact extends AppCompatActivity {

    TextView serviceProviderName,serviceProviderTelephone,serviceProviderEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_contact);

        serviceProviderName=findViewById(R.id.serviceProviderNameTextView);
        serviceProviderTelephone=findViewById(R.id.serviceProviderMobileNumberTextView);
        serviceProviderEmail=findViewById(R.id.serviceProviderEmailTextView);


        final Intent intent = getIntent();
        final ServiceRequestCall serviceRequestCall = (ServiceRequestCall) intent.getSerializableExtra("service-request-call-for-mechanic");

        String telephone=serviceRequestCall.getServiceProvider().getTelephone();
        String email=serviceRequestCall.getServiceProvider().getEmail();
        String name=serviceRequestCall.getServiceProvider().getName();

        serviceProviderName.setText(name);
        serviceProviderEmail.setText(email);
        serviceProviderTelephone.setText(telephone);



    }
}
