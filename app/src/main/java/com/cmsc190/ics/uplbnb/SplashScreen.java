package com.cmsc190.ics.uplbnb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference("user");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null){
            Intent i = new Intent(getApplicationContext(),Sign_in.class);
            finish();
            startActivity(i);
        }
        else{
            String userId = firebaseUser.getUid();
            databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    Intent i;
                    if(user.getUser_type().equals("renter")){
                        i = new Intent(getApplicationContext(),RenterHome.class);
                        finish();
                        startActivity(i);

                    }
                    else if(user.getUser_type().equals("owner")){
                        i = new Intent(getApplicationContext(),OwnerHome.class);

                        finish();
                        startActivity(i);
                    }
                    else{
                        i = new Intent(getApplicationContext(),AdminHome.class);
                        finish();
                        startActivity(i);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }
}
