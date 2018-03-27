package com.cmsc190.ics.uplbnb;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class OwnerHome extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceEstablishments;
    FirebaseUser firebaseUser;
    String firebaseUserId;
    TextView ownerName;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Establishment_Item> establishment_items;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_home);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUserId = firebaseUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
  //      ownerName = (TextView) findViewById(R.id.ownerName);
        databaseReference = firebaseDatabase.getReference("user");
        fab = (FloatingActionButton) findViewById(R.id.addEstablishment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEstablishment();
            }
        });
        establishment_items = new ArrayList<>();
        databaseReferenceEstablishments = firebaseDatabase.getReference("establishment");
        databaseReferenceEstablishments.orderByChild("owner_id").equalTo(firebaseUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                establishment_items.clear();
                for(DataSnapshot establishmentSnapshot : dataSnapshot.getChildren()){
                    Establishment_Item e = establishmentSnapshot.getValue(Establishment_Item.class);
                    establishment_items.add(e);
                }
                Establishment_Item_Adapter e_adapter = new Establishment_Item_Adapter(establishment_items,getApplicationContext());
                recyclerView.setAdapter(e_adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewOwnerHome);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference.child(firebaseUserId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
    //            ownerName.setText(user.getFirst_name() + " " + user.getLast_name());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void addEstablishment(){
        Intent i = new Intent(getApplicationContext(),AddEstablishment.class);
        startActivity(i);
    }

}
