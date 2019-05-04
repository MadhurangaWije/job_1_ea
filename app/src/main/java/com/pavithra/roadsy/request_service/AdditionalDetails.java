package com.pavithra.roadsy.request_service;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pavithra.roadsy.R;
import com.pavithra.roadsy.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdditionalDetails extends AppCompatActivity {

    private Button sendRequestBtn;
    private RadioGroup toolsAvailableRadioGroup;
    private TextView breakdownDescriptionTextView;

    private ServiceRequestCall serviceRequestCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_details);

        sendRequestBtn=findViewById(R.id.sendRequestBtn);
        toolsAvailableRadioGroup=findViewById(R.id.radioToolsAvailable);
        breakdownDescriptionTextView =findViewById(R.id.description);

        Intent intent=getIntent();

        serviceRequestCall=(ServiceRequestCall) intent.getSerializableExtra("service-request-call");

        sendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description=breakdownDescriptionTextView.getText().toString();

                int selectedId=toolsAvailableRadioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton=findViewById(selectedId);
                String selectedRadioButtonName=selectedRadioButton.getText().toString();
                boolean toolsAvailability;
                if(selectedRadioButtonName.equals("Yes")){
                    toolsAvailability=true;
                }else{
                    toolsAvailability=false;
                }

                System.out.println("999999999999999(((((((((((((( "+serviceRequestCall.getServiceProvider().getFcmToken());

                AdditionalServiceRequestDetail additionalServiceRequestDetail= new AdditionalServiceRequestDetail(description,toolsAvailability);

                serviceRequestCall.setAdditionalServiceRequestDetail(additionalServiceRequestDetail);
//                serviceRequestCall.stringifyTheJson();

                Controller controller=new Controller();
                controller.sendRequest(serviceRequestCall, new Callback<ServiceRequestCall>() {
                    @Override
                    public void onResponse(Call<ServiceRequestCall> call, Response<ServiceRequestCall> response) {
                        System.out.println("||||||||||||||||||||||||||||||||&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                    }

                    @Override
                    public void onFailure(Call<ServiceRequestCall> call, Throwable t) {
                        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                    }
                });


            }
        });

    }
}
