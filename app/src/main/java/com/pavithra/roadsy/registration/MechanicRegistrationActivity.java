package com.pavithra.roadsy.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pavithra.roadsy.MechanicActivity;
import com.pavithra.roadsy.R;
import com.pavithra.roadsy.User;
import com.pavithra.roadsy.location.CurrentLocationActivity;
import com.pavithra.roadsy.login.LoginActivity;


public class MechanicRegistrationActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private static final int REQUEST_CURRENTLOCATION = 1;
    private static final int REQUEST_LOGIN = 2;

    EditText _businessNameText;
    EditText _emailText;
    EditText _hotline;
    EditText _businessRegistrationNumber;
    EditText _passwordText;
    EditText _verifyPassword;
    Button _signupButton;
    TextView _loginLink;

    FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_mechanic_registration);

        _signupButton=findViewById(R.id.createMechanicAccountBtn);
        _loginLink=findViewById(R.id.link_mechanic_login);
        _hotline =findViewById(R.id.input_hotline);
        _passwordText=findViewById(R.id.input_mechanic_password);
        _verifyPassword=findViewById(R.id.input_verify_mechanic_password);
        _businessNameText =findViewById(R.id.input_business_name);
        _businessRegistrationNumber=findViewById(R.id.input_business_registration_number);
        _emailText=findViewById(R.id.input_mechanic_email);


        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, REQUEST_LOGIN);
                finish();
            }
        });

        firebaseAuth=FirebaseAuth.getInstance();

    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(MechanicRegistrationActivity.this,
                R.style.Theme_AppCompat_DayNight);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String name = _businessNameText.getText().toString().trim();
        final String email = _emailText.getText().toString().trim();
        final String telephone= _hotline.getText().toString().trim();
        final String businessRegistrationNumber=_businessRegistrationNumber.getText().toString().trim();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            DatabaseReference dataseRef=FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid());
                            User user=new User(name,email,"mechanic",telephone,businessRegistrationNumber);
                            dataseRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        onSignupSuccess();
                                        progressDialog.dismiss();
                                    }else{
                                        Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }else{
                            onSignupFailed();
                        }

                    }
                });

    }


    public void onSignupSuccess() {
        Toast.makeText(getBaseContext(), "Registration Successful", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(getApplicationContext(), MechanicActivity.class);
        startActivityForResult(intent, REQUEST_CURRENTLOCATION);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Registration failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _businessNameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _businessNameText.setError("at least 3 characters");
            valid = false;
        } else {
            _businessNameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 ) {
            _passwordText.setError("more than 4 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
