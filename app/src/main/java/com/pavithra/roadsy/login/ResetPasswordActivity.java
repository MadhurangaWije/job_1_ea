package com.pavithra.roadsy.login;

//import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.pavithra.roadsy.R;


public class ResetPasswordActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN = 0;
    Button resetPasswordBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_reset_password);

        resetPasswordBtn=findViewById(R.id.btn_reset_password);

        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Password reset link sent to your email, please follow that",Toast.LENGTH_SHORT).show();
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivityForResult(intent, REQUEST_LOGIN);
                            }
                        }, 2000);

            }
        });
    }
}
