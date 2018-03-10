package com.cmsc190.ics.uplbnb;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;

import com.google.firebase.database.DatabaseReference;

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
    Button callButton;
    LinearLayout ll;
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_drilldown);
        i = getIntent();
        Unit_Item unit = (Unit_Item)i.getSerializableExtra("unit");

        ll = findViewById(R.id.furniture);

        unitDrilldownCondition = findViewById(R.id.apartmentUnitDrilldownCondition);
        unitDrilldownCapacity = findViewById(R.id.apartmentUnitDrilldownCapacity);
        unitDrilldownSlotsAvailable = findViewById(R.id.apartmentUnitDrilldownSlotsAvailable);
        furnitureItems = findViewById(R.id.furnitureItems);
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

    public void callOwner(){
        String contactNumber = (String)i.getStringExtra("establishmentContactNumber");
        String uri = "tel:" + contactNumber;
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
        startActivity(callIntent);
    }
    public String stringifyFurniture(HashMap<String,Integer> furniture){
        String furnitureItems = "";
        Iterator entries = furniture.entrySet().iterator();
        while (entries.hasNext()){
            HashMap.Entry entry = (HashMap.Entry) entries.next();
            String key = (String)entry.getKey();
            Integer value = (Integer)entry.getValue();
            furnitureItems+=value + " x " + key + "\n";
        }
        furnitureItems = furnitureItems.substring(0,furnitureItems.length() - 1);
        return furnitureItems;
    }
}

