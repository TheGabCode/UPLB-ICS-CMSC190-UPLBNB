package com.cmsc190.ics.uplbnb;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.content.Intent;
import android.widget.Toast;


import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;

public class AddUnit extends AppCompatActivity {
    ImageView unitImagePreview;
    Button uploadImageBtn;
    EditText unitIdentifierText;
    EditText unitRate;
    EditText unitCapacity;
    EditText unitSlotsAvailable;
    Spinner statusSpinner;
    Spinner conditionSpinner;
    Button submitUnitBtn;
    Button addFurnitureBtn;
    Intent intent;
    LinearLayout unitFurnitureContainer;
    LinearLayout dormitoryAttributes;
    LinearLayout apartmentAttributes;
    LinearLayout dormitoryFurnitureAttributes;
    Establishment_Item e;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        intent = getIntent();

        int establishmentType   = intent.getIntExtra("establishmentType",1);
        if(establishmentType == 1){
            e = (Apartment_Item)intent.getSerializableExtra("establishment");
        }
        else{
            e = (Dormitory_Item)intent.getSerializableExtra("establishment");
        }
        setContentView(R.layout.activity_add_unit);
        dormitoryAttributes = (LinearLayout)findViewById(R.id.addUnitDormCapacityContainer);
        apartmentAttributes = (LinearLayout)findViewById(R.id.addUnitApartmentPart);
        dormitoryFurnitureAttributes = (LinearLayout)findViewById(R.id.addUnitDormitoryFurnitureLayout);


        unitImagePreview = (ImageView)findViewById(R.id.addUnitUploadPhotoPreview);
        uploadImageBtn = (Button)findViewById(R.id.addUnitUploadImageBtn);
        unitIdentifierText = (EditText)findViewById(R.id.addUnitIdentifier);
        unitRate = (EditText)findViewById(R.id.addUnitPrice);
        unitCapacity = (EditText)findViewById(R.id.addUnitCapacity);


        unitSlotsAvailable = (EditText)findViewById(R.id.addUnitDormitoryAvailableSlots);
        statusSpinner = (Spinner)findViewById(R.id.apartmentStatusSpinner);
        statusSpinner.setSelection(1);
        conditionSpinner = (Spinner)findViewById(R.id.conditionSpinner);
        unitFurnitureContainer = findViewById(R.id.addUnitFurnitureContainer);
        submitUnitBtn = (Button)findViewById(R.id.addUnitSubmitButton);
        submitUnitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUnit();
            }
        });
        addFurnitureBtn = (Button)findViewById(R.id.addFurnitureBtn);
        addFurnitureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFurniture(view);
            }
        });
        if(establishmentType == 1){
            dormitoryAttributes.setVisibility(View.GONE);
            dormitoryFurnitureAttributes.setVisibility(View.GONE);
        }
        else{
            apartmentAttributes.setVisibility(View.GONE);
        }
        if(establishmentType == 1){

        }else{
            unitRate.setText(e.getPrice());
            unitCapacity.setText(((Dormitory_Item)e).getCapacityPerUnit()+"");
            initializeFurniture(e);
        }
    }

    public void initializeFurniture(Establishment_Item e){
        HashMap<String,Integer> furniture = (HashMap<String,Integer>)((Dormitory_Item)e).getAvailableFurniture();
        if(furniture != null){
            Toast.makeText(getApplicationContext(),"Has furniture",Toast.LENGTH_SHORT).show();
            Iterator entries = furniture.entrySet().iterator();
            while (entries.hasNext()){
                HashMap.Entry entry = (HashMap.Entry) entries.next();
                String key = (String)entry.getKey();
                Integer value = (Integer)entry.getValue();
                addFurniture(key,value);
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"No furniture",Toast.LENGTH_SHORT).show();
        }
    }

    public void addFurniture(View v){
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View furnitureView = inflater.inflate(R.layout.furniture_layout,null);
        unitFurnitureContainer.addView(furnitureView,unitFurnitureContainer.getChildCount() - 1);
    }

    public void addFurniture(String item, int quantity){
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View furnitureView = inflater.inflate(R.layout.furniture_layout,null);
        EditText itemName = (EditText)furnitureView.findViewById(R.id.dormitoryFurnitureItem);
        EditText itemQty = (EditText)furnitureView.findViewById(R.id.dormitoryFurnitureQty);
        itemName.setText(item);
        itemQty.setText(""+quantity);
        unitFurnitureContainer.addView(furnitureView,unitFurnitureContainer.getChildCount() - 1);
    }

    public  void deleteFurniture(View v){
        unitFurnitureContainer.removeView((View)v.getParent());
    }



    public void addUnit(){
        final DatabaseReference databaseReference;
        DatabaseReference furnitureRef;
        int establishmentType = intent.getIntExtra("establishmentType",1);
        String establishmentId = intent.getStringExtra("establishmentId");
        String unitIdentifier = unitIdentifierText.getText().toString().trim();
        int intUnitCondition;
        int condition = 0;
        int intUnitStatus = 1;
        int slotsAvailable = 0; //for dorms
        int capacity = 0; //for dorms;
        int open = 1;
        String id;
        HashMap<String,Integer> furniture = new HashMap<>();
        double rate;

        boolean ratePerHead = false;
        if(TextUtils.isEmpty(unitIdentifierText.getText().toString().trim())){
            Toast.makeText(getApplication(),"Please fill in the unit identifier.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(establishmentType == 1){
            intUnitCondition = conditionSpinner.getSelectedItemPosition(); //for apartment
            slotsAvailable = -1;
            intUnitStatus = statusSpinner.getSelectedItemPosition();
            if(intUnitStatus == 0){
                open = 1;
            }
            else{
                open = 0;
            }
            if(intUnitCondition == 0){
                condition = 1;
            }
            else{
                condition = 0;
            }

            if(TextUtils.isEmpty(unitRate.getText().toString().trim())){
                Toast.makeText(getApplication(),"Please fill in unit rate.",Toast.LENGTH_SHORT).show();
                return;
            }

            rate = Double.parseDouble(unitRate.getText().toString().trim());
        }
        else{
            if(TextUtils.isEmpty(unitRate.getText().toString().trim())){
                Toast.makeText(getApplication(),"Please fill in unit rate.",Toast.LENGTH_SHORT).show();
                return;
            }
            rate = Double.parseDouble(unitRate.getText().toString().trim());

            if(TextUtils.isEmpty(unitCapacity.getText().toString().trim())){
                Toast.makeText(getApplication(),"Please fill in unit capacity.",Toast.LENGTH_SHORT).show();
                return;
            }
            capacity = Integer.parseInt(unitCapacity.getText().toString().trim());
            if(TextUtils.isEmpty(unitSlotsAvailable.getText().toString().trim())){
                Toast.makeText(getApplication(),"Please fill in unit slots available.",Toast.LENGTH_SHORT).show();
                return;
            }
            slotsAvailable = Integer.parseInt(unitSlotsAvailable.getText().toString().trim());
            ratePerHead = intent.getBooleanExtra("ratePerHead",false);


            for(int i = 1; i < unitFurnitureContainer.getChildCount() - 1; i++){
                View v = unitFurnitureContainer.getChildAt(i);
                EditText itemQuantity = (EditText)v.findViewById(R.id.dormitoryFurnitureQty);
                EditText itemName = (EditText)v.findViewById(R.id.dormitoryFurnitureItem);
                int qty = 0;
                String item;
                if(TextUtils.isEmpty(itemQuantity.getText().toString().trim())){
                    Toast.makeText(getApplication(),"Please fill in the furniture fields.",Toast.LENGTH_SHORT).show();
                    return;
                }
                qty = Integer.parseInt(itemQuantity.getText().toString().trim());
                if(TextUtils.isEmpty(itemName.getText().toString().trim())){
                    Toast.makeText(getApplication(),"Please fill in the furniture fields.",Toast.LENGTH_SHORT).show();
                    return;
                }
                item = itemName.getText().toString().trim();
                furniture.put(item,qty);

            }


        }

/*        if(establishmentType == 1){
            if(TextUtils.isEmpty(unitRate.getText().toString().trim())){
                Toast.makeText(getApplication(),"Please fill in unit rate.",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else{
            if(TextUtils.isEmpty(unitRate.getText().toString().trim())){
                Toast.makeText(getApplication(),"Please fill in unit rate.",Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(unitCapacity.getText().toString().trim())){
                Toast.makeText(getApplication(),"Please fill in unit capacity.",Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(unitSlotsAvailable.getText().toString().trim())){
                Toast.makeText(getApplication(),"Please fill in unit slots available.",Toast.LENGTH_SHORT).show();
                return;
            }
        }*/


        databaseReference = FirebaseDatabase.getInstance().getReference("establishment").child(establishmentId).child("unit").push();
        id = databaseReference.getKey();

        Unit_Item unit = new Unit_Item(unitIdentifier, open, slotsAvailable, rate, id,  condition, furniture, ratePerHead,capacity);
        databaseReference.setValue(unit);
        finish();

    }



}
