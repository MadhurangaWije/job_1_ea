package com.pavithra.roadsy.login;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pavithra.roadsy.MechanicActivity;
import com.pavithra.roadsy.R;
import com.pavithra.roadsy.User;
import com.pavithra.roadsy.AdminActivity;
import com.pavithra.roadsy.location.CurrentLocationActivity;
import com.pavithra.roadsy.registration.UserRegistrationActivity;
import com.pavithra.roadsy.util.DisplayUtil;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final int REQUEST_RESET = 1;

    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;
    TextView _fogotPassword;
    ProgressBar _loginProgressBar;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        _emailText=findViewById(R.id.input_email);
        _passwordText=findViewById((R.id.input_password));
        _loginButton=findViewById(R.id.btn_login);
        _signupLink=findViewById(R.id.link_signup);
        _fogotPassword=findViewById(R.id.link_fogot_password);
        _loginProgressBar=findViewById(R.id.loginProgressBar);


        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), UserRegistrationActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        _fogotPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the ResetPassword activity
                Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                startActivityForResult(intent, REQUEST_RESET);
            }
        });

        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null){
            Intent intent = new Intent(getApplicationContext(), CurrentLocationActivity.class);
            startActivityForResult(intent, REQUEST_SIGNUP);
            finish();
        }
    }

    public void login() {
        Log.d(TAG, "Login");
        DisplayUtil.hideKeyboard(this);

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);
        _loginProgressBar.setVisibility(View.VISIBLE);



        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            onLoginSuccess(firebaseAuth.getCurrentUser());
                        }else{
                            onLoginFailed();
                        }
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(FirebaseUser loggedInFirebaseUser) {
        _loginButton.setEnabled(true);
        _loginProgressBar.setVisibility(View.INVISIBLE);

        FirebaseDatabase database=FirebaseDatabase.getInstance();

        database.getReference("users").child(loggedInFirebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User loggedInUser=dataSnapshot.getValue(User.class);
                if(loggedInUser!=null) {
                    Toast.makeText(getApplicationContext(), loggedInUser.getName(), Toast.LENGTH_LONG).show();
                    signInUser(loggedInUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
        _loginProgressBar.setVisibility(View.INVISIBLE);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 ) {
            _passwordText.setError("at least 4 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
