package com.pavithra.roadsy;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pavithra.roadsy.login.LoginActivity;
import com.pavithra.roadsy.registration.UserRegistrationActivity;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN = 1;
    private static final int REQUEST_SIGNUP = 2;

    Button loginBtn;
    Button signUpBtn;
    TextView serviceProviderLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        loginBtn=findViewById(R.id.loginBtn);
        signUpBtn=findViewById(R.id.signUpBtn);
        serviceProviderLink=findViewById(R.id.serviceProviderLink);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, REQUEST_LOGIN);
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserRegistrationActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });


        serviceProviderLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Currently Under Development",Toast.LENGTH_LONG).show();
            }
        });
    }
}
