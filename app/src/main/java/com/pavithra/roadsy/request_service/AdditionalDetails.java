package com.pavithra.roadsy.request_service;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pavithra.roadsy.JobProgress;
import com.pavithra.roadsy.R;
import com.pavithra.roadsy.ServiceStatus;
import com.pavithra.roadsy.ServiceStatusWithLiveLocationUpdate;
import com.pavithra.roadsy.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdditionalDetails extends AppCompatActivity {

    private Button sendRequestBtn;
    private RadioGroup toolsAvailableRadioGroup;
    private TextView breakdownDescriptionTextView;

    private PopupWindow mechanicSearchingProgressWindow;

    private ConstraintLayout additionalDetailLayout;

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

        additionalDetailLayout=findViewById(R.id.additionalDetailLayout);

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

                AdditionalServiceRequestDetail additionalServiceRequestDetail= new AdditionalServiceRequestDetail(description,toolsAvailability);

                serviceRequestCall.setAdditionalServiceRequestDetail(additionalServiceRequestDetail);
//                serviceRequestCall.stringifyTheJson();

                Controller controller=new Controller();

                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.service_provider_selection,null);

                mechanicSearchingProgressWindow= new PopupWindow(
                        customView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

//                mechanicSearchingProgressWindow.showAtLocation(additionalDetailLayout, Gravity.CENTER,0,0);
                Intent i = new Intent("android.intent.action.WindowOpen");
                getApplicationContext().sendBroadcast(i);


                controller.sendRequest(serviceRequestCall, new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

//                        mechanicSearchingProgressWindow.dismiss();
                        Intent i = new Intent("android.intent.action.WindowClose");
                        getApplicationContext().sendBroadcast(i);

                        Intent intentForServiceStatus = new Intent(getApplicationContext(), ServiceStatusWithLiveLocationUpdate.class);
                        intentForServiceStatus.putExtra("service-request-call-for-mechanic",serviceRequestCall);
                        startActivity(intentForServiceStatus);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Intent i = new Intent("android.intent.action.WindowClose");
                        getApplicationContext().sendBroadcast(i);
                        t.printStackTrace();
                        Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                        finish();
                    }
                });


            }
        });

    }
}
