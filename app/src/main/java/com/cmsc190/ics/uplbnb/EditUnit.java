package com.cmsc190.ics.uplbnb;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class EditUnit extends AppCompatActivity implements ConfirmDeleteDialogFragment.NoticeDialogListener{
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
    Unit_Item unit;
    ActionBar actionBar;
    DatabaseReference databaseReference;
    DatabaseReference unitRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        intent = getIntent();
        unit = (Unit_Item)intent.getSerializableExtra("unit");
        int establishmentType   = intent.getIntExtra("establishmentType",1);
        actionBar = getActionBar();
        if(establishmentType == 1){
            e = (Apartment_Item)intent.getSerializableExtra("establishment");
        }
        else{
            e = (Dormitory_Item)intent.getSerializableExtra("establishment");
        }
        setContentView(R.layout.activity_edit_unit);
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

            if(unit.getStatus() == 1){
                statusSpinner.setSelection(0);
            }
            else{
                statusSpinner.setSelection(1);
            }

            if(unit.getCondition() == 1){
                conditionSpinner.setSelection(0);
            }
            else{
                conditionSpinner.setSelection(1);
            }
        }
        else{
            apartmentAttributes.setVisibility(View.GONE);
        }
        unitIdentifierText.setText(unit.getUnitIdentifier());
        if(establishmentType == 1){
            unitRate.setText(unit.getRate()+"");
        }else{

            unitRate.setText(unit.getRate()+"");
            unitCapacity.setText(unit.getCapacity()+"");
            unitSlotsAvailable.setText(unit.getSlotsAvailable()+"");

            initializeFurniture(unit);
        }
    }

    public void initializeFurniture(Unit_Item unit){
        HashMap<String,Integer> furniture = (HashMap<String,Integer>)unit.getFurniture();
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.deleteEstablishment:
                confirmDelete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        deleteUnit();
/*        Toast.makeText(getApplicationContext(),"Delete",Toast.LENGTH_SHORT);*/
        dialog.dismiss();
        finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        dialog.dismiss();

    }
    public void confirmDelete(){
        DialogFragment deleteFragment = new ConfirmDeleteDialogFragment();
        deleteFragment.show(getFragmentManager(),"delete");
    }

    public void deleteUnit(){
        String eId = intent.getStringExtra("establishmentId");
        Toast.makeText(getApplicationContext(),"Delete",Toast.LENGTH_SHORT).show();
        databaseReference = FirebaseDatabase.getInstance().getReference("establishment").child(eId).child("unit");
        databaseReference.child(unit.getId()).removeValue();
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
        String unitIdentifier = unitIdentifierText.getText().toString().trim();
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
            if(slotsAvailable > 0){
                open = 1;
            }else{
                open = 0;
            }

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


        databaseReference = FirebaseDatabase.getInstance().getReference("establishment").child(establishmentId).child("unit").child(unit.getId());
        Unit_Item unitUpdate = new Unit_Item(unitIdentifier, open, slotsAvailable, rate, unit.getId(),  condition, furniture, ratePerHead,capacity);
        databaseReference.setValue(unitUpdate);



        DatabaseReference establishmentReference = FirebaseDatabase.getInstance().getReference("establishment").child(establishmentId);
        establishmentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Establishment_Item e = dataSnapshot.getValue(Establishment_Item.class);
                unitRef = FirebaseDatabase.getInstance().getReference("establishment").child(e.getId()).child("numUnitsAvailable");
                unitRef.setValue(countOpenUnits(e));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        finish();

    }


    public int countOpenUnits(Establishment_Item e){
        int totalOpen = 0;
        if(e.unit != null){
            Iterator entries = e.unit.entrySet().iterator();
            while (entries.hasNext()){
                HashMap.Entry entry = (HashMap.Entry) entries.next();
                Unit_Item value = (Unit_Item) entry.getValue();
                if(value.getStatus() == 1){
                    totalOpen++;
                }
            }
        }
        return totalOpen;
    }

}
