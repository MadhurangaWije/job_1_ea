package com.pavithra.roadsy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisteredMechanicsAdminView extends AppCompatActivity {

    private TextView name,businessRegNo,email,hotline;

    private Button blockMechanicsBtn,logoutBtn;

    private FirebaseDatabase firebaseDatabase;

    private boolean isBlocked=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_mechanics_admin_view);

        firebaseDatabase=FirebaseDatabase.getInstance();



        name=findViewById(R.id.mechanicsNameTextView);
        businessRegNo=findViewById(R.id.businessRegNoTextView);
        email=findViewById(R.id.mechanicemail_adminTextView);
        hotline=findViewById(R.id.hotlineTextView);
        blockMechanicsBtn=findViewById(R.id.blockMechanicsBtn);


        Intent intent=getIntent();
        User selectedMechanic=(User) intent.getSerializableExtra("selected-mechanic");

        String mechanicFirebaseUid=selectedMechanic.getFirebaseUid();
        final DatabaseReference databaseReference=firebaseDatabase.getReference("users").child(mechanicFirebaseUid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User mechanic=dataSnapshot.getValue(User.class);
                if(mechanic!=null) {

                    if(mechanic.isBlocked()){
                        blockMechanicsBtn.setText("UnBlock");
                        updateBlockStatus(true);
                    }else{
                        blockMechanicsBtn.setText("Block");
                        updateBlockStatus(false);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        name.setText(selectedMechanic.getName());
        businessRegNo.setText(selectedMechanic.getBusinessRegistrationNumber());
        email.setText(selectedMechanic.getEmail());
        hotline.setText(selectedMechanic.getTelephone());


//        if(selectedMechanic.isBlocked()){
//            blockMechanicsBtn.setText("UnBlock");
//        }else{
//            blockMechanicsBtn.setText("Block");
//        }

        blockMechanicsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBlocked()){
                    databaseReference.child("blocked").setValue(false);
                    updateBlockStatus(false);
                }else {
                    databaseReference.child("blocked").setValue(true);
                    updateBlockStatus(true);
                }

            }
        });



    }

    private void updateBlockStatus(boolean newStatus){
        isBlocked=newStatus;
    }

    private boolean isBlocked(){
        return isBlocked;
    }
}
