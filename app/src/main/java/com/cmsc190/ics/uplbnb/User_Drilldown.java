package com.cmsc190.ics.uplbnb;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.view.View.GONE;

/**
 * Created by Dell on 23 Apr 2018.
 */

public class User_Drilldown extends AppCompatActivity implements  Serializable {
    FirebaseUser firebaseUser;
    FirebaseUser adminUser;
    TextView userName;
    TextView userEmail;
    TextView userNumber1,userNumber2;
    ImageView ownerProfilePicture;
    Intent i;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ownerRef;
    DatabaseReference establishmentRef;
    DatabaseReference logRef;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    LinearLayout ownerNumbers;
    LinearLayout ownedEstablishments;
    String userId;
    User user;
    RecyclerView establishmentsRecyclerView;
    RecyclerView ownerLogsRecyclerView;
    List<Establishment_Item> establishment_items;
    List<Log> log_items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_drilldown);
        adminUser = FirebaseAuth.getInstance().getCurrentUser();
        establishment_items = new ArrayList<Establishment_Item>();
        log_items = new ArrayList<Log>();
        i = getIntent();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        ownerNumbers = findViewById(R.id.ownerNumbersLayout);
        ownedEstablishments = findViewById(R.id.ownerOwnedEstablishmentLayout);
        ownerNumbers.setVisibility(GONE);
        ownedEstablishments.setVisibility(GONE);
        userId = i.getStringExtra("userId");
        userName = findViewById(R.id.profileName);
        userEmail = findViewById(R.id.profileEmail);
        userNumber1 = findViewById(R.id.userNumber1);
        userNumber2 = findViewById(R.id.userNumber2);
        ownerProfilePicture = findViewById(R.id.profilePicture);
        ownerRef = firebaseDatabase.getReference("user");
        ownerRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              user = dataSnapshot.getValue(User.class);
              userName.setText(user.getFullname());
              userEmail.setText(user.getEmail());
              if(user.getUser_type().equals("owner")){
                  ownerNumbers.setVisibility(View.VISIBLE);
                  ownedEstablishments.setVisibility(View.VISIBLE);
                  userNumber1.setText(user.getNumber());
                  userNumber2.setText(user.getNumber2());
                  StorageReference userIconRef = storageReference.child("user/owner/"+user.getId());
                  GlideApp.with(getApplication())
                          .load(userIconRef)
                          .placeholder(R.drawable.user_male_gray_48px)
                          .apply(RequestOptions.circleCropTransform())
                          .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                          .into(ownerProfilePicture);
                }
                else{
                  ownerNumbers.setVisibility(GONE);
                  ownedEstablishments.setVisibility(GONE);
              }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        establishmentRef = firebaseDatabase.getReference("establishment");
        establishmentRef.orderByChild("owner_id").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                establishment_items.clear();
                for(DataSnapshot establishmentSnapshot : dataSnapshot.getChildren()){
                    Establishment_Item e = establishmentSnapshot.getValue(Establishment_Item.class);
                    establishment_items.add(e);
                }
                Establishment_Item_Adapter e_adapter = new Establishment_Item_Adapter(establishment_items,getApplicationContext(),0);
                establishmentsRecyclerView.setAdapter(e_adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        logRef = firebaseDatabase.getReference("logs");
        logRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                log_items.clear();
                for(DataSnapshot logSnapshot : dataSnapshot.getChildren()){
                    Log log = logSnapshot.getValue(Log.class);
                    log_items.add(log);
                }
                Log_Item_Adapter log_adapter = new Log_Item_Adapter(log_items,getApplicationContext());
                ownerLogsRecyclerView.setAdapter(log_adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ownerLogsRecyclerView = findViewById(R.id.ownerLogs);
        ownerLogsRecyclerView.setHasFixedSize(true);
        ownerLogsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        establishmentsRecyclerView = findViewById(R.id.ownerOwnedEstablishment);
        establishmentsRecyclerView.setHasFixedSize(true);
        establishmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


}
