package com.cmsc190.ics.uplbnb;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class AddEstablishment extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    String TAG = "Log";
    double UPLB_GATE_LAT = 14.1675928;
    double UPLB_GATE_LONG = 121.24345819999999;
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
    EditText apartmentRentYears;
    EditText capacityPerUnitDorm;
    LinearLayout dormitoryPriceLayout;
    LinearLayout apartmentPriceLayout;
    LinearLayout apartmentRentYearsLayout;
    LinearLayout apartmentConditionLayout;
    LinearLayout dormitoryCapacityPerUnitLayout;
    LinearLayout dormitoryFurnitureLayout;
    LinearLayout dormFurnitureContainer;
    EditText dormitoryFurnitureQty;
    EditText dormitoryFurnitureItem;
    Button dormitoryAddItemBtn;
    ImageButton dormitoryDeleteItemBtn;
    AutoCompleteTextView addEstablishmentAddressAutocomplete;
    Button addEstablishmentSubmitButton;
    Button uploadImageButton, saveImageButton;
    ImageView previewImage;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    ArrayList<Uri> uriList;
    User user;
    PlaceInfo mPlace;
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

    public void uploadImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            //uriList.add(filePath);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                previewImage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void saveImage(String establishmentId){
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
//            progressDialog.show();
            StorageReference ref = storageReference.child("establishments/"+establishmentId);

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            progressDialog.dismiss();
                            Toast.makeText(AddEstablishment.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
    //                        progressDialog.dismiss();
                            Toast.makeText(AddEstablishment.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
  //                          progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });

/*            for(int i = 0; i < uriList.size(); i++){

                }*/
            }


    }

    public void addEstablishment(){
        Establishment_Item newEstablishment;
        String establishmentNameString = establishmentName.getText().toString().trim();
        String contactPerson = user.getId();
        String contactNumber = user.getNumber();
        String address = addEstablishmentAddressAutocomplete.getText().toString().trim();
        double latitude,longitude;
        latitude = mPlace.getLatitude();
        longitude = mPlace.getLongitude();
        float[] distanceFromCampus = new float[1];
        Location.distanceBetween(latitude,longitude,UPLB_GATE_LAT,UPLB_GATE_LONG,distanceFromCampus);
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
                if(TextUtils.isEmpty(maxPriceApartment.getText().toString().trim())){
                    Toast.makeText(getApplicationContext(), "Please enter valid price.",Toast.LENGTH_SHORT).show();
                    return;
                }
                price = maxPriceApartment.getText().toString().trim();
            }
            else{
                if(TextUtils.isEmpty(minPriceApartment.getText().toString().trim()) || TextUtils.isEmpty(maxPriceApartment.getText().toString().trim())){
                    Toast.makeText(getApplicationContext(), "Please enter valid price.",Toast.LENGTH_SHORT).show();
                    return;
                }
                price = minPriceApartment.getText().toString().trim() + "-" + maxPriceApartment.getText().toString().trim();
            }
        }
        else{
            if(TextUtils.isEmpty(dormitoryPrice.getText().toString().trim())){
                Toast.makeText(getApplicationContext(), "Please enter valid price.",Toast.LENGTH_SHORT).show();
                return;
            }
            price = dormitoryPrice.getText().toString().trim();
        }


        ratePerHead = (perHead.isChecked()) ? true : false;
        security = (intSecurity == 0) ? true : false;
        visitorsAllowed = (intVisitors == 0) ? true : false;
        furnished = (intCondition == 0) ? true : false;
        concealContactPerson = (intConcealContactPerson == 0) ? true : false;
        concealPrice = (intConcealPrice == 0) ? true : false;
        concealUnits = (intConcealUnits == 0)? true : false;
        includeBillsInRate = (intIncludeBillsInRate == 0) ? true : false;

        if(TextUtils.isEmpty(establishmentNameString)){
            Toast.makeText(getApplicationContext(), "Please enter establishment name.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(address)){
            Toast.makeText(getApplicationContext(), "Please enter establishment address.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(price)){
            Toast.makeText(getApplicationContext(), "Please enter price.",Toast.LENGTH_SHORT).show();
            return;
        }

        if(establishmentType == 1){
            if(TextUtils.isEmpty(apartmentRentYears.getText().toString().trim())){
                Toast.makeText(getApplicationContext(), "Please enter valid rent years.",Toast.LENGTH_SHORT).show();
                return;
            }
            rentYears = Integer.parseInt(apartmentRentYears.getText().toString().trim());
        }
        if(establishmentType == 0){
            if(TextUtils.isEmpty(capacityPerUnitDorm.getText().toString().trim())){
                Toast.makeText(getApplicationContext(), "Please enter valid dorm capacity.",Toast.LENGTH_SHORT).show();
                return;
            }
            intCapacityPerUnit = Integer.parseInt(capacityPerUnitDorm.getText().toString().trim());
        }
        HashMap<String,Integer> furniture = new HashMap<String, Integer>();
        HashMap<String, Review> reviews = new HashMap<String, Review>();
        if(establishmentType == 0){
            for(int i = 1; i < dormFurnitureContainer.getChildCount() - 1; i++){
                View v = dormFurnitureContainer.getChildAt(i);
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
        databaseReference = FirebaseDatabase.getInstance().getReference("establishment").push();
        String id = databaseReference.getKey();
        if(establishmentType == 1){
            newEstablishment  = new Apartment_Item(establishmentNameString,contactPerson, contactNumber, contactNumber, price, address, curfewHours, visitorsAllowed, establishmentType, includeBillsInRate, distanceFromCampus[0], security, concealContactPerson, concealPrice, concealUnits, rentYears, furnished,rating,id, user.getId(), isFixedPrice,reviews, latitude,longitude,mPlace);
            databaseReference.setValue(newEstablishment);
        }
        else if(establishmentType == 0){
            newEstablishment = new Dormitory_Item(establishmentNameString,contactPerson, contactNumber,contactNumber, price, address,curfewHours,visitorsAllowed,establishmentType, includeBillsInRate, distanceFromCampus[0], security, concealContactPerson,concealPrice,concealUnits, ratePerHead,intCapacityPerUnit, rating,id,user.getId(),furniture,reviews,latitude,longitude,mPlace);
            databaseReference.setValue(newEstablishment);
        }
        saveImage(id);
        finish();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_establishment);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        concealContactPersonSpinner = (Spinner)(findViewById(R.id.concealContactPersonSpinner));
        uriList = new ArrayList<Uri>();
        concealContactPersonSpinner.setSelection(1);
        concealPriceSpinner = (Spinner)findViewById(R.id.concealPriceSpinner);
        concealPriceSpinner.setSelection(1);
        concealUnitsSpinner = (Spinner)findViewById(R.id.concealUnitsSpinner);
        concealUnitsSpinner.setSelection(1);
        includeBillsInRateSpinner = (Spinner)findViewById(R.id.includeBillsInRateSpinner);
        securitySpinner = (Spinner)findViewById(R.id.securitySpinner);
        visitorsAllowedSpinner = (Spinner)findViewById(R.id.visitorsAllowedSpinner);
        conditionSpinner = (Spinner)findViewById(R.id.conditionSpinner);
        dormitoryPriceLayout = (LinearLayout)findViewById(R.id.addEstablishmentPriceDormitoryLayout);
        apartmentPriceLayout = (LinearLayout)findViewById(R.id.addEstablishmentPriceApartmentLayout);
        apartmentRentYearsLayout = (LinearLayout)findViewById(R.id.addEstablishmentApartmentRentYearsLayout);
        apartmentConditionLayout = (LinearLayout)findViewById(R.id.addEstablishmentApartmentConditionLayout);
        dormitoryCapacityPerUnitLayout = (LinearLayout)findViewById(R.id.addEstablishmentDormitoryCapacityLayout);
        dormFurnitureContainer = (LinearLayout)findViewById(R.id.addEstablishmentFurnitureContainer);
        dormitoryFurnitureLayout = (LinearLayout)findViewById(R.id.addEstablishmentFurnitureLayout);
        dormitoryFurnitureQty = (EditText)findViewById(R.id.dormitoryFurnitureQty);
        dormitoryFurnitureItem = (EditText)findViewById(R.id.dormitoryFurnitureItem);
        dormitoryAddItemBtn = (Button)findViewById(R.id.addFurnitureBtn);
        dormitoryDeleteItemBtn = (ImageButton)findViewById(R.id.removeFurnitureBtn);
        establishmentName = (EditText)findViewById(R.id.addEstablishmentName);
        dormitoryPrice = (EditText)findViewById(R.id.addEstablishmentPriceDormitory);
        perHead = (CheckBox)findViewById(R.id.checkBoxPerHead);
        minPriceApartment = (EditText)findViewById(R.id.addEstablishmentPriceApartmentMin);
        maxPriceApartment = (EditText)findViewById(R.id.addEstablishmentPriceApartmentMax);
        fixedPriceApartment = (CheckBox)findViewById(R.id.checkBoxFixedRate);
        startCurfewHours = (EditText) findViewById(R.id.addEstablishmentCurfewStart);
        endCurfewHours = (EditText) findViewById(R.id.addEstablishmentCurfewEnd);
        apartmentRentYears = (EditText)findViewById(R.id.addEstablishmentRentYears);
        capacityPerUnitDorm = (EditText)findViewById(R.id.addEstablishmentCapacityPerUnit);
        capacityPerUnitDorm.setText("0");
        apartmentRentYears.setText("0");
        uploadImageButton = (Button)findViewById(R.id.addEstablishmentUploadImageBtn);
        /*saveImageButton = (Button)findViewById(R.id.addEstablishmentSaveImageBtn);*/
        previewImage = (ImageView)findViewById(R.id.addEstablishmentUploadPhotoPreview);
        addEstablishmentSubmitButton = (Button)findViewById(R.id.addEstablishmentSubmitButton);
        addEstablishmentSubmitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                addEstablishment();
            }
        });
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

/*        saveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage();
            }
        });*/


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        addEstablishmentAddressAutocomplete = (AutoCompleteTextView)findViewById(R.id.addEstablishmentAddress);
        addEstablishmentAddressAutocomplete.setOnItemClickListener(mAutocompleteClickListener);
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
                    dormitoryFurnitureLayout.setVisibility(View.GONE);
                }
                else if(i == 1){
                    establishmentType = 0; //if adding dormitory
                    dormitoryCapacityPerUnitLayout.setVisibility(View.VISIBLE);
                    dormitoryPriceLayout.setVisibility(View.VISIBLE);
                    dormitoryFurnitureLayout.setVisibility(View.VISIBLE);
                    apartmentConditionLayout.setVisibility(View.GONE);
                    apartmentPriceLayout.setVisibility(View.GONE);
                    apartmentRentYearsLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        fixedPriceApartment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(fixedPriceApartment.isChecked()){
                    minPriceApartment.setVisibility(View.GONE);
                    maxPriceApartment.setHint("Price");
                }
                else{
                    maxPriceApartment.setHint("Max Price");
                    minPriceApartment.setVisibility(View.VISIBLE);
                }
            }
        });

        perHead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
    }

    public void addFurniture(View v){
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View furnitureView = inflater.inflate(R.layout.furniture_layout,null);
        dormFurnitureContainer.addView(furnitureView,dormFurnitureContainer.getChildCount() - 1);
    }

    public  void deleteFurniture(View v){
        dormFurnitureContainer.removeView((View)v.getParent());
    }


    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);

            try{
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                Log.d("Wee","Nostradamus");
                Log.d(TAG, "onResult: name: " + place.getName());
                mPlace.setAddress(place.getAddress().toString());
                Log.d(TAG, "onResult: address: " + place.getAddress());
//                mPlace.setAttributions(place.getAttributions().toString());
//                Log.d(TAG, "onResult: attributions: " + place.getAttributions());
                mPlace.setId(place.getId());

                Log.d(TAG, "onResult: id:" + place.getId());

                mPlace.setLatitude(place.getLatLng().latitude);
                Log.d(TAG, "onResult: latitude:" + place.getLatLng().latitude);
                mPlace.setLongitude(place.getLatLng().longitude);
                Log.d(TAG, "onResult: longitude:" + place.getLatLng().longitude);
                mPlace.setRating(place.getRating());
                Log.d(TAG, "onResult: rating: " + place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                Log.d(TAG, "onResult: phone number: " + place.getPhoneNumber());

                Log.d(TAG, "onResult: place: " + mPlace.toString());
            }catch (NullPointerException e){
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage() );
            }


            places.release();
        }
    };


}
