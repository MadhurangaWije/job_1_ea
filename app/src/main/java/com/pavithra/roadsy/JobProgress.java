package com.pavithra.roadsy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pavithra.roadsy.request_service.Controller;
import com.pavithra.roadsy.request_service.ServiceRequestCall;
import com.pavithra.roadsy.request_service.ServiceStatusCall;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobProgress extends AppCompatActivity {

    Button attendingBtn,visitedBtn,completedBtn,rejectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_progress);
        Intent intent=getIntent();
        ServiceRequestCall serviceRequestCall=(ServiceRequestCall) intent.getSerializableExtra("service-request-call-for-mechanic");

        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||"+serviceRequestCall.getClient().getName());
        final String token=serviceRequestCall.getClient().getFcmToken();

        attendingBtn=findViewById(R.id.attendingBtn);
        visitedBtn=findViewById(R.id.visitedBtn);
        completedBtn=findViewById(R.id.completedBtn);
        rejectBtn=findViewById(R.id.rejectBtn);

        attendingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controller controller=new Controller();
                ServiceStatusCall serviceStatusCall= new ServiceStatusCall("Attending...",token);
                controller.sendStatus(serviceStatusCall, new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Toast.makeText(getApplicationContext(),"Status Send...",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"Something Went Wrong, couldnt able to update the status...",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        visitedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controller controller=new Controller();
                ServiceStatusCall serviceStatusCall= new ServiceStatusCall("Visited...",token);
                controller.sendStatus(serviceStatusCall, new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Toast.makeText(getApplicationContext(),"Status Send...",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"Something Went Wrong, couldn't able to update the status...\n"+t.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        completedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controller controller=new Controller();
                ServiceStatusCall serviceStatusCall= new ServiceStatusCall("Completed...",token);
                controller.sendStatus(serviceStatusCall, new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Toast.makeText(getApplicationContext(),"Status Send...",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"Something Went Wrong, couldnt able to update the status...",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controller controller=new Controller();
                ServiceStatusCall serviceStatusCall= new ServiceStatusCall("Rejected...",token);
                controller.sendStatus(serviceStatusCall, new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Toast.makeText(getApplicationContext(),"Status Send...",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"Something Went Wrong, couldnt able to update the status...",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
