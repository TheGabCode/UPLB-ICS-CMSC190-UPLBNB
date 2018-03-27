package com.cmsc190.ics.uplbnb;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddEstablishment extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    Spinner establishmentTypeSpinner;
    Spinner securitySpinner;
    Spinner concealContactPersonSpinner;
    Spinner concealPriceSpinner;
    Spinner concealUnitsSpinner;
    Spinner includeBillsInRateSpinner;
    Spinner visitorsAllowedSpinner;
    Spinner conditionSpinner;
    EditText establishmentName;
    EditText dormitoryPrice;
    CheckBox perHead;
    EditText minPriceApartment;
    EditText maxPriceApartment;
    CheckBox fixedPriceApartment;
    EditText startCurfewHours;
    EditText endCurfewHours;
/*
    CheckBox securityCCTV;
    CheckBox securityGuard;
    RadioGroup visitorsRadioGroup;
*/
    EditText apartmentRentYears;
/*    RadioGroup conditionRadioGroup;*/
    EditText capacityPerUnitDorm;
    LinearLayout dormitoryPriceLayout;
    LinearLayout apartmentPriceLayout;
    LinearLayout apartmentRentYearsLayout;
    LinearLayout apartmentConditionLayout;
    LinearLayout dormitoryCapacityPerUnitLayout;
    AutoCompleteTextView addEstablishmentAddressAutocomplete;
    Button addEstablishmentSubmitButton;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    User user;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(14.136028, 121.200107),new LatLng(14.175857, 121.265629)); //Rougly los banos bounds
    private int establishmentType = 1;

    public void initUser(){
        databaseReference = firebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        databaseReference.child("user").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              user = dataSnapshot.getValue(User.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void addEstablishment(){
        Establishment_Item newEstablishment;
        String establishmentNameString = establishmentName.getText().toString().trim();
        String contactPerson = user.getId();
        String contactNumber = user.getNumber();
        String address = addEstablishmentAddressAutocomplete.getText().toString().trim();
        String price;
        String curfewHours = startCurfewHours.getText().toString().trim() + "-" + endCurfewHours.getText().toString().trim();
        boolean security;
        boolean visitorsAllowed;
        boolean furnished;
        boolean isFixedPrice = false;
        boolean concealContactPerson;
        boolean concealPrice;
        boolean concealUnits;
        boolean includeBillsInRate;
        boolean ratePerHead;
        int intCapacityPerUnit = 0;
        int rentYears = 0;
        int intSecurity = securitySpinner.getSelectedItemPosition();
        int intVisitors = visitorsAllowedSpinner.getSelectedItemPosition();
        int intCondition = conditionSpinner.getSelectedItemPosition();
        int intConcealContactPerson = concealContactPersonSpinner.getSelectedItemPosition();
        int intConcealPrice = concealPriceSpinner.getSelectedItemPosition();
        int intConcealUnits = concealUnitsSpinner.getSelectedItemPosition();
        int intIncludeBillsInRate = includeBillsInRateSpinner.getSelectedItemPosition();
        float rating = 0f;

        if(establishmentType == 1){
            if(fixedPriceApartment.isChecked()){
                isFixedPrice = true;
                price = maxPriceApartment.getText().toString().trim();
            }
            else{
                price = minPriceApartment.getText().toString().trim() + "-" + maxPriceApartment.getText().toString().trim();
            }
        }
        else{
            price = dormitoryPrice.getText().toString().trim();
        }

        if(establishmentType == 1){
            rentYears = Integer.parseInt(apartmentRentYears.getText().toString().trim());
        }
        if(establishmentType == 0){
            intCapacityPerUnit = Integer.parseInt(capacityPerUnitDorm.getText().toString().trim());
        }


        if(perHead.isChecked()){
            ratePerHead = true;
        }else{
            ratePerHead = false;
        }

        security = (intSecurity == 0) ? true : false;
        visitorsAllowed = (intVisitors == 0) ? true : false;
        furnished = (intCondition == 0) ? true : false;
        concealContactPerson = (intConcealContactPerson == 0) ? true : false;
        concealPrice = (intConcealPrice == 0) ? true : false;
        concealUnits = (intConcealUnits == 0)? true : false;
        includeBillsInRate = (intIncludeBillsInRate == 0) ? true : false;




        databaseReference = FirebaseDatabase.getInstance().getReference("establishment").push();
        String id = databaseReference.getKey();
        if(establishmentType == 1){
            newEstablishment  = new Apartment_Item(establishmentNameString,contactPerson, contactNumber, contactNumber, price, address, curfewHours, visitorsAllowed, establishmentType, includeBillsInRate, 100, security, concealContactPerson, concealPrice, concealUnits, rentYears, furnished,rating,id, user.getId(), isFixedPrice);
            databaseReference.setValue(newEstablishment);
        }
        else if(establishmentType == 0){
            newEstablishment = new Dormitory_Item(establishmentNameString,contactPerson, contactNumber,contactNumber, price, address,curfewHours,visitorsAllowed,establishmentType, includeBillsInRate, 100, security, concealContactPerson,concealPrice,concealUnits, ratePerHead,intCapacityPerUnit, rating,id,user.getId());
            databaseReference.setValue(newEstablishment);
        }

        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_establishment);
        concealContactPersonSpinner = (Spinner)(findViewById(R.id.concealContactPersonSpinner));
        concealPriceSpinner = (Spinner)findViewById(R.id.concealPriceSpinner);
        concealUnitsSpinner = (Spinner)findViewById(R.id.concealUnitsSpinner);
        includeBillsInRateSpinner = (Spinner)findViewById(R.id.includeBillsInRateSpinner);
        securitySpinner = (Spinner)findViewById(R.id.securitySpinner);
        visitorsAllowedSpinner = (Spinner)findViewById(R.id.visitorsAllowedSpinner);
        conditionSpinner = (Spinner)findViewById(R.id.conditionSpinner);
        dormitoryPriceLayout = (LinearLayout)findViewById(R.id.addEstablishmentPriceDormitoryLayout);
        apartmentPriceLayout = (LinearLayout)findViewById(R.id.addEstablishmentPriceApartmentLayout);
        apartmentRentYearsLayout = (LinearLayout)findViewById(R.id.addEstablishmentApartmentRentYearsLayout);
        apartmentConditionLayout = (LinearLayout)findViewById(R.id.addEstablishmentApartmentConditionLayout);
        dormitoryCapacityPerUnitLayout = (LinearLayout)findViewById(R.id.addEstablishmentDormitoryCapacityLayout);
        establishmentName = (EditText)findViewById(R.id.addEstablishmentName);
        dormitoryPrice = (EditText)findViewById(R.id.addEstablishmentPriceDormitory);
        perHead = (CheckBox)findViewById(R.id.checkBoxPerHead);
        minPriceApartment = (EditText)findViewById(R.id.addEstablishmentPriceApartmentMin);
        maxPriceApartment = (EditText)findViewById(R.id.addEstablishmentPriceApartmentMax);
        fixedPriceApartment = (CheckBox)findViewById(R.id.checkBoxFixedRate);
        startCurfewHours = (EditText) findViewById(R.id.addEstablishmentCurfewStart);
        endCurfewHours = (EditText) findViewById(R.id.addEstablishmentCurfewEnd);
   //     securityCCTV = (CheckBox)findViewById(R.id.addEstablishmentCheckboxCCTV);
   //     securityGuard = (CheckBox)findViewById(R.id.addEstablishmentCheckboxGuard);
   //     visitorsRadioGroup = (RadioGroup)findViewById(R.id.visitorsRadioGroup);
        apartmentRentYears = (EditText)findViewById(R.id.addEstablishmentRentYears);
   //     conditionRadioGroup = (RadioGroup)findViewById(R.id.conditionRadioGroup);
        capacityPerUnitDorm = (EditText)findViewById(R.id.addEstablishmentCapacityPerUnit);
        capacityPerUnitDorm.setText("0");
        apartmentRentYears.setText("0");
        addEstablishmentSubmitButton = (Button)findViewById(R.id.addEstablishmentSubmitButton);
        addEstablishmentSubmitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                addEstablishment();
            }
        });

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();



        addEstablishmentAddressAutocomplete = (AutoCompleteTextView)findViewById(R.id.addEstablishmentAddress);
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this,mGoogleApiClient,LAT_LNG_BOUNDS,null);

        addEstablishmentAddressAutocomplete.setAdapter(placeAutocompleteAdapter);
        establishmentTypeSpinner = (Spinner) findViewById(R.id.establishmentTypeSpinner);
        establishmentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    establishmentType = 1; //if adding apartment
                    //hide capacity per unit and dormitory price
                    apartmentConditionLayout.setVisibility(View.VISIBLE);
                    apartmentPriceLayout.setVisibility(View.VISIBLE);
                    apartmentRentYearsLayout.setVisibility(View.VISIBLE);
                    dormitoryPriceLayout.setVisibility(View.GONE);
                    dormitoryCapacityPerUnitLayout.setVisibility(View.GONE);
                }
                else if(i == 1){
                    establishmentType = 0; //if adding dormitory
                    dormitoryCapacityPerUnitLayout.setVisibility(View.VISIBLE);
                    dormitoryPriceLayout.setVisibility(View.VISIBLE);
                    apartmentConditionLayout.setVisibility(View.GONE);
                    apartmentPriceLayout.setVisibility(View.GONE);
                    apartmentRentYearsLayout.setVisibility(View.GONE);
                }
//                Toast.makeText(getApplication(),adapterView.getItemAtPosition(i).toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        fixedPriceApartment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(fixedPriceApartment.isChecked()){
                 //   isFixedPrice = true;
                    minPriceApartment.setVisibility(View.GONE);
                    maxPriceApartment.setHint("Price");
                 //   price = maxPriceApartment.getText().toString().trim();
                }
                else{
                    minPriceApartment.setVisibility(View.VISIBLE);
                    //   price = minPriceApartment.getText().toString().trim() + "-" + maxPriceApartment.getText().toString().trim();
                }
            }
        });

        perHead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
    }



}
