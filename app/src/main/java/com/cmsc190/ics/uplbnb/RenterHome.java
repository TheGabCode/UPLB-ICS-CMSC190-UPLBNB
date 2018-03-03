package com.cmsc190.ics.uplbnb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RenterHome extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Establishment_Item> establishment_items;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refEstablishments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renter_home);
        establishment_items = new ArrayList<>();


        //Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        //setSupportActionBar(toolbar);
        refEstablishments = database.getReference("establishment");
        refEstablishments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                establishment_items.clear();
                for(DataSnapshot establishmentSnapshot: dataSnapshot.getChildren()){
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
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewHome);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        establishment_items = new ArrayList<>();


        adapter = new Establishment_Item_Adapter(establishment_items,this);

        recyclerView.setAdapter(adapter);
        Toast.makeText(getApplicationContext(),"Successfully logged in",Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.address_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*switch (item.getItemId()) {

            case R.id.sortHome:
                //do something

                break;
            default:
                break;
        }*/
        return super.onOptionsItemSelected(item);
    }

}
