package com.cmsc190.ics.uplbnb;

import android.app.ActionBar;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;

import static android.provider.LiveFolders.INTENT;

public class ApartmentUnitDrilldown extends AppCompatActivity {
    private DatabaseReference databaseReference;
    TextView unitDrilldownRate;
    TextView unitDrilldownStatus;
    TextView unitDrilldownCondition;
    TextView unitDrilldownCapacity;
    TextView unitDrilldownSlotsAvailable;
    TextView furnitureItems;
    ImageButton editButton;
    Button callButton;
    LinearLayout ll;
    Intent i;
    ActionBar toolbar;
    Unit_Item unit;
    DatabaseReference ref;
    public void getUnitData(String establishmentId, String unitId){
        ref = FirebaseDatabase.getInstance().getReference("establishment").child(establishmentId).child("unit").child(unitId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                unit = dataSnapshot.getValue(Unit_Item.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_drilldown);
        i = getIntent();
        String eId = i.getStringExtra("establishmentId");
        String uId = ((Unit_Item)i.getSerializableExtra("unit")).getId();

        ll = findViewById(R.id.furniture);
        unitDrilldownCondition = findViewById(R.id.apartmentUnitDrilldownCondition);
        unitDrilldownCapacity = findViewById(R.id.apartmentUnitDrilldownCapacity);
        unitDrilldownSlotsAvailable = findViewById(R.id.apartmentUnitDrilldownSlotsAvailable);
        furnitureItems = findViewById(R.id.furnitureItems);
        unitDrilldownRate = findViewById(R.id.apartmentUnitDrilldownRate);

        unitDrilldownStatus = findViewById(R.id.apartmentUnitDrilldownStatus);
        callButton = findViewById(R.id.callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call owner
                callOwner();
            }
        });
        editButton = (ImageButton)findViewById(R.id.unitDrilldownEditBtn);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editUnit();
            }
        });

        ref = FirebaseDatabase.getInstance().getReference("establishment").child(eId).child("unit").child(uId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                unit = dataSnapshot.getValue(Unit_Item.class);
                if(unit == null){
                    finish();
                    return;
                }
                if(unit.getSlotsAvailable() == -1){
                    ll.setVisibility(View.GONE);
                    unitDrilldownCapacity.setVisibility(View.GONE);
                    unitDrilldownSlotsAvailable.setVisibility(View.GONE);
                    if(unit.getCondition() == 0){
                        unitDrilldownCondition.setText("Unfurnished");
                    }else{
                        unitDrilldownCondition.setText("Furnished");
                    }
                }else{ //if dormitory
                    unitDrilldownCondition.setVisibility(View.GONE);
                    unitDrilldownCapacity.setText(unit.getCapacity() + " persons");
                    unitDrilldownSlotsAvailable.setText(unit.getSlotsAvailable() + " slots available out of " + unit.getCapacity());
                    furnitureItems.setText(stringifyFurniture(unit.getFurniture()));
                }

                unitDrilldownRate.setText(unit.getRate() + " PHP");
                if(unit.isRatePerHead() == true){
                    unitDrilldownRate.append(" per head");
                }

                if(unit.getStatus() == 1){
                    unitDrilldownStatus.setText("Open");
                }
                else{
                    unitDrilldownStatus.setText("Closed");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void editUnit(){
        Intent intent = new Intent(getApplicationContext(),EditUnit.class);
        intent.putExtra("unit",unit);
        intent.putExtra("establishmentType",i.getIntExtra("establishmentType",1));
        intent.putExtra("establishmentId",i.getStringExtra("establishmentId"));
        startActivity(intent);
    }

    public void callOwner(){
        String contactNumber = (String)i.getStringExtra("establishmentContactNumber");
        String uri = "tel:" + contactNumber;
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
        startActivity(callIntent);
    }
    public String stringifyFurniture(HashMap<String,Integer> furniture){
        String furnitureItems = "";
        if(furniture != null){
            Iterator entries = furniture.entrySet().iterator();
            while (entries.hasNext()){
                HashMap.Entry entry = (HashMap.Entry) entries.next();
                String key = (String)entry.getKey();
                Integer value = (Integer)entry.getValue();
                furnitureItems+=value + " x " + key + "\n";
            }
            furnitureItems = furnitureItems.substring(0,furnitureItems.length() - 1);
        }

        return furnitureItems;
    }
}

