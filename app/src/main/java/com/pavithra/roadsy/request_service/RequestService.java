package com.pavithra.roadsy.request_service;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pavithra.roadsy.CustomRequiredServicesListAdapter;
import com.pavithra.roadsy.R;
import com.pavithra.roadsy.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestService extends AppCompatActivity {

    RequiredService[] REQUIRED_SERVICES_ARRAY = {
            new RequiredService("On-location flat tire support"),
            new RequiredService("Battery jump start"),
            new RequiredService("Lockout service"),
            new RequiredService("Towing vehicles"),
            new RequiredService("Fuel delivery service"),
            new RequiredService("Minor mechanical repairs"),
            new RequiredService("Vehicle service"),
            new RequiredService("Vehicle Grooming"),
    };

    List<RequiredService> requiredServiceList = Arrays.asList(REQUIRED_SERVICES_ARRAY);



    ListView listView;
    CheckBox checkBox;
    CustomRequiredServicesListAdapter adapter;
//    ArrayAdapter<String> adapter;
    Button requestServiceProceedBtn;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    User loggedInUser;
    User selectedServiceProvider;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_service);

        Intent intent=getIntent();
        loggedInUser=(User)intent.getSerializableExtra("client");
        selectedServiceProvider=(User)intent.getSerializableExtra("service-provider");

        System.out.println("{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{c  "+selectedServiceProvider.getName());

        requestServiceProceedBtn=findViewById(R.id.requestServiceProceedBtn);

        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        listView=findViewById(R.id.requestServiceListView);
        checkBox=listView.findViewById(R.id.requiredServiceCheckbox);
        adapter=new CustomRequiredServicesListAdapter(requiredServiceList,getApplicationContext());
//        adapter=new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,a);
        listView.setAdapter(adapter);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                RequiredService requiredService=Arrays.asList(requiredServiceList).get(position);
////                checkBox.setSelected(requiredService.isRequired());
//                Toast.makeText(getApplicationContext(),a[position],Toast.LENGTH_LONG).show();
//
//            }
//        });


        requestServiceProceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a="";
                List<RequiredService> selectedRequiredServiceList=new ArrayList<>();
                for (RequiredService requiredService: requiredServiceList){
                    if(requiredService.isRequired().equals("true")){
                        a+=requiredService.getName()+ ", \n";
                        selectedRequiredServiceList.add(requiredService);
                    }
                }

                ServiceRequestCall serviceRequestCall= new ServiceRequestCall();
                serviceRequestCall.setRequiredServiceList(selectedRequiredServiceList);
                serviceRequestCall.setClient(loggedInUser);
                serviceRequestCall.setServiceProvider(selectedServiceProvider);

                System.out.println("88888888888888888(((((((((((((( "+serviceRequestCall.getServiceProvider().getFcmToken());
                System.out.println("88888888888888888(((((((((((((( "+serviceRequestCall.getToken());

                Intent intent = new Intent(getApplicationContext(), AdditionalDetails.class);
                intent.putExtra("service-request-call",serviceRequestCall);

                startActivity(intent);
                finish();

                Toast.makeText(getApplicationContext(),a,Toast.LENGTH_LONG).show();
            }
        });

    }
}
