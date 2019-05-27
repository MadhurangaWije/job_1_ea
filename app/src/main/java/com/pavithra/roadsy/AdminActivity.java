package com.pavithra.roadsy;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pavithra.roadsy.R;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private CustomServiceProviderListAdapter adapter;
    private ListView registeredMechanics;
    private Button logoutBtn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        registeredMechanics=findViewById(R.id.registeredMechanicsInAdminListView);
        logoutBtn=findViewById(R.id.adminLogoutBtn);

        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        final List<User> userList = new ArrayList<>();
        firebaseDatabase.getReference("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    User user  = dsp.getValue(User.class);

                    if(user!=null && user.getType()!=null &&user.getType().equals("mechanic")){
                        userList.add(user); //add result into array list
                    }
                }
                adapter= new CustomServiceProviderListAdapter(userList,getApplicationContext());
                registeredMechanics.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


//        ArrayAdapter<String> adapterm= new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,new String[]{"Apple","Oragnge"});
//        registeredMechanics.setAdapter(adapter);

        registeredMechanics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user= userList.get(position);
                Intent intent=new Intent(getApplicationContext(),RegisteredMechanicsAdminView.class);
                intent.putExtra("selected-mechanic",user);
                startActivity(intent);

            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();

                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

//    class CustomAdminMechanicAdapter extends ArrayAdapter<User>{
//
//        public CustomAdminMechanicAdapter( Context context, int resource) {
//            super(context, resource);
//        }
//    }
}
