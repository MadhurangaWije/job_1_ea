package com.pavithra.roadsy.registration;

import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pavithra.roadsy.R;
import com.pavithra.roadsy.User;
import com.pavithra.roadsy.location.CurrentLocationActivity;

import java.util.List;


public class UserRegistrationActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private static final int REQUEST_CURRENTLOCATION = 1;

    EditText _nameText;
    EditText _emailText;
    EditText _mobileNumber;
    EditText _userName;
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
        setContentView(R.layout.activity_user_registration);

        _signupButton=findViewById(R.id.createAccountBtn);
        _loginLink=findViewById(R.id.link_login);
        _mobileNumber=findViewById(R.id.input_mobile_number);
        _userName=findViewById(R.id.input_username);
        _passwordText=findViewById(R.id.input_password);
        _verifyPassword=findViewById(R.id.input_verify_password);
        _nameText=findViewById(R.id.input_name);
        _emailText=findViewById(R.id.input_email);


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

        final ProgressDialog progressDialog = new ProgressDialog(UserRegistrationActivity.this,
                R.style.Theme_AppCompat_DayNight);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String name = _nameText.getText().toString().trim();
        final String email = _emailText.getText().toString().trim();
        final String telephone=_mobileNumber.getText().toString().trim();
        final String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.

        firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()){
                    List<String> possibleSignInMehtods=task.getResult().getSignInMethods();
                    if(possibleSignInMehtods.size()==0){

                        firebaseAuth.createUserWithEmailAndPassword(email,password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            DatabaseReference dataseRef=FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid());
                                            User user=new User(name,email,"user",telephone);
                                            dataseRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        onSignupSuccess();
                                                        progressDialog.dismiss();
                                                    }else{
                                                        Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();
                                                        onSignupFailed();
                                                        progressDialog.dismiss();
                                                    }
                                                }
                                            });

                                        }else{
                                            onSignupFailed();
                                            progressDialog.dismiss();
                                        }

                                    }

                                });


                    }else{
                        Toast.makeText(getApplicationContext(),"Email Already Exists!",Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        onSignupFailed();
                    }
                }
            }
        });


//        firebaseAuth.createUserWithEmailAndPassword(email,password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()){
//                            DatabaseReference dataseRef=FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid());
//                            User user=new User(name,email,"user",telephone);
//                            dataseRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()){
//                                        onSignupSuccess();
//                                        progressDialog.dismiss();
//                                    }else{
//                                        Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();
//                                    }
//                                }
//                            });
//
//                        }else{
//                            onSignupFailed();
//                        }
//
//                    }
//
//                });




//        new android.os.Handler().postDelayed(
//                new Runnable() {
//                    public void run() {
//                        // On complete call either onSignupSuccess or onSignupFailed
//                        // depending on success
//                        onSignupSuccess();
//                        // onSignupFailed();
//                        progressDialog.dismiss();
//                    }
//                }, 3000);
    }


    public void onSignupSuccess() {
        Toast.makeText(getBaseContext(), "Registration Successful", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(getApplicationContext(), CurrentLocationActivity.class);
        startActivityForResult(intent, REQUEST_CURRENTLOCATION);
        finish();
    }

    public void onSignupFailed() {

        Toast.makeText(getBaseContext(), "Registration failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
