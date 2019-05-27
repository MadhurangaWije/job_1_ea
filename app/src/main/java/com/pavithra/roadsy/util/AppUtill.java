package com.pavithra.roadsy.util;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pavithra.roadsy.User;

import java.util.concurrent.CountDownLatch;

public class AppUtill {

    public static boolean isLoggedInUserMechanic() throws InterruptedException {
        final User user=new User();

        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference=database.getReference("users").child(firebaseUser.getUid());


        final CountDownLatch latch=new CountDownLatch(1);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User loggedInUser=dataSnapshot.getValue(User.class);
                user.setType(loggedInUser.getType());
                latch.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("[[[[[[[[[[[[[[[[[[[[[[[[[[[[[["+databaseError.getMessage());
                latch.countDown();
            }
        });

        latch.await();

        return user.getType().equals("mechanic");

    }
}
