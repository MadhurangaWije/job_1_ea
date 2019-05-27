package com.pavithra.roadsy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pavithra.roadsy.location.CurrentLocationActivity;
import com.pavithra.roadsy.login.LoginActivity;
import com.pavithra.roadsy.registration.MechanicRegistrationActivity;
import com.pavithra.roadsy.registration.UserRegistrationActivity;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN = 1;
    private static final int REQUEST_SIGNUP = 2;

    Button loginBtn;
    Button signUpBtn;
    TextView serviceProviderLink;
    FirebaseAuth firebaseAuth;
    ProgressBar _loginProgressBar;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        firebaseAuth=FirebaseAuth.getInstance();




        _loginProgressBar=findViewById(R.id.mainActivityProgressBar);
        _loginProgressBar.bringToFront();


        FirebaseUser user=firebaseAuth.getCurrentUser();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };

        _loginProgressBar.setVisibility(View.GONE);
        if(user!=null){

            _loginProgressBar.bringToFront();
            _loginProgressBar.setVisibility(View.VISIBLE);
//            Intent intent = new Intent(getApplicationContext(), CurrentLocationActivity.class);
//            startActivityForResult(intent, REQUEST_SIGNUP);
//            finish();
            FirebaseDatabase database=FirebaseDatabase.getInstance();

            database.getReference("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User loggedInUser=dataSnapshot.getValue(User.class);
                    if(loggedInUser!=null) {
                        Toast.makeText(getApplicationContext(), "Welcome "+loggedInUser.getName(), Toast.LENGTH_LONG).show();
                        signInUser(loggedInUser);
                        _loginProgressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

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
                Intent intent = new Intent(getApplicationContext(), MechanicRegistrationActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);

            }
        });
    }

    private void signInUser(User loggedInUser) {
        Intent intent=null;
        if(loggedInUser.getType().equals("user")){
            intent = new Intent(getApplicationContext(), CurrentLocationActivity.class);
        }else if(loggedInUser.getType().equals("mechanic")){
            intent = new Intent(getApplicationContext(), MechanicActivity.class);
        }else if(loggedInUser.getType().equals("admin")){
            intent = new Intent(getApplicationContext(), AdminActivity.class);
        }
        if(intent!=null){
            startActivityForResult(intent, REQUEST_SIGNUP);
        }
        finish();
    }
}
