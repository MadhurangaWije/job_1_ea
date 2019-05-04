package com.pavithra.roadsy.login;

//import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.pavithra.roadsy.R;


public class ResetPasswordActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN = 0;
    Button resetPasswordBtn;
    FirebaseAuth firebaseAuth;
    EditText editTextResetEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_reset_password);

        firebaseAuth=FirebaseAuth.getInstance();

        resetPasswordBtn=findViewById(R.id.btn_reset_password);
        editTextResetEmail=findViewById(R.id.reset_email);

        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
//                Toast.makeText(getApplicationContext(),"Password reset link sent to your email, please follow that",Toast.LENGTH_SHORT).show();
//                new android.os.Handler().postDelayed(
//                        new Runnable() {
//                            public void run() {
//                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                                startActivityForResult(intent, REQUEST_LOGIN);
//                            }
//                        }, 2000);

            }
        });
    }

    private void resetPassword(){
        String resetEmail=editTextResetEmail.getText().toString().trim();
        firebaseAuth.sendPasswordResetEmail(resetEmail)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Password reset link sent to your email, please follow that",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivityForResult(intent, REQUEST_LOGIN);
                        }
                    }
                });
    }
}
