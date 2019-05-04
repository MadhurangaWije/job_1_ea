package com.pavithra.roadsy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.pavithra.roadsy.location.Location;
import com.pavithra.roadsy.request_service.RequiredService;
import com.pavithra.roadsy.request_service.ServiceRequestCall;

import java.util.ArrayList;
import java.util.List;

public class ClientRequestDisplay extends AppCompatActivity {

    ListView requiredServicesListView;
    TextView descriptionOfRequestedService,isToolsAvailableTextView,distanceToClientTextView;
    Button acceptBtn,rejectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_request_display);

        Intent intent=getIntent();
        final ServiceRequestCall serviceRequestCall=(ServiceRequestCall)intent.getSerializableExtra("service-request-call-for-mechanic");

        requiredServicesListView=findViewById(R.id.requiredServiceListForMeachanicaListView);
        descriptionOfRequestedService=findViewById(R.id.requiredServiceDescriptionForMechanicTextView);
        isToolsAvailableTextView=findViewById(R.id.toolsAvailabilityForMechanicTextView);
        distanceToClientTextView=findViewById(R.id.distanceToClientTextView);
        acceptBtn=findViewById(R.id.acceptRequestBtn);
        rejectBtn=findViewById(R.id.rejectRequestBtn);

        List<String> requiredServiceList=new ArrayList<>();
        for (RequiredService requiredService:serviceRequestCall.getRequiredServiceList()){
            requiredServiceList.add(requiredService.getName());
        }

        String telephone=serviceRequestCall.getClient().getTelephone();
        String description=serviceRequestCall.getAdditionalServiceRequestDetail().getDescription();
        boolean isToolsAvailable=serviceRequestCall.getAdditionalServiceRequestDetail().isToolsAvailable();
        Location clientLocation=serviceRequestCall.getClient().getLocation();
        Location serviceProviderLocation=serviceRequestCall.getServiceProvider().getLocation();


        ArrayAdapter<String> arrayAdapterForRequiredServiceList=new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,requiredServiceList);
        requiredServicesListView.setAdapter(arrayAdapterForRequiredServiceList);


        descriptionOfRequestedService.setText(description);
        if(isToolsAvailable){
            isToolsAvailableTextView.setText("Yes");
        }else{
            isToolsAvailableTextView.setText("No");
        }

        android.location.Location clientLocationObj=new android.location.Location("");
        clientLocationObj.setLongitude(Double.parseDouble(clientLocation.getLongitude()));
        clientLocationObj.setLatitude(Double.parseDouble(clientLocation.getLatitude()));

        android.location.Location serviceProviderLocationObj=new android.location.Location("");
        serviceProviderLocationObj.setLongitude(Double.parseDouble(serviceProviderLocation.getLongitude()));
        serviceProviderLocationObj.setLatitude(Double.parseDouble(serviceProviderLocation.getLatitude()));

        float distanceToClient=clientLocationObj.distanceTo(serviceProviderLocationObj);
        distanceToClientTextView.setText(distanceToClientTextView.getText()+"  "+String.valueOf(distanceToClient)+"m");

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JobProgress.class);
                intent.putExtra("service-request-call-for-mechanic",serviceRequestCall);
                startActivity(intent);
            }
        });

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });





    }
}
