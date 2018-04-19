package com.cmsc190.ics.uplbnb;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class EditEstablishment extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, ConfirmDeleteDialogFragment.NoticeDialogListener {
    String TAG = "Log";
    double UPLB_GATE_LAT = 14.1675928;
    double UPLB_GATE_LONG = 121.24345819999999;
    PlaceInfo mPlace;
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
    LinearLayout dormAcceptedSexLayout;
    Spinner acceptedSexSpinner;
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
    Intent intent;
    ActionBar actionBar;
    public static Establishment_Item e;
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
            progressDialog.show();
            StorageReference ref = storageReference.child("establishments/"+ establishmentId);

            Bitmap bmp = null;

            try{
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            }catch (IOException e){
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();
            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(EditEstablishment.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
              //              progressDialog.dismiss();
                            Toast.makeText(EditEstablishment.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                //            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });

/*            for(int i = 0; i < uriList.size(); i++){

                }*/
        }
        else{
            finish();
        }

    }

    public void updateEstablishment(){
        Establishment_Item newEstablishment;
        String establishmentNameString = establishmentName.getText().toString().trim();
        String contactPerson = user.getId();
        String contactNumber = user.getNumber();
        String address = addEstablishmentAddressAutocomplete.getText().toString().trim();
        double latitude = mPlace.getLatitude();
        double longitude = mPlace.getLongitude();
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
        int intAcceptedSex = acceptedSexSpinner.getSelectedItemPosition();
        float rating = e.getRating();
        int acceptedSex = -1;
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

        if(intAcceptedSex == 0){
            acceptedSex = -1;
        }
        else if(intAcceptedSex == 1){
            acceptedSex = 1;
        }
        else{
            acceptedSex = 0;
        }

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
        HashMap<String,Review> review = e.getReviews();
        HashMap<String,Unit_Item> unit = e.getUnits();
        HashMap<String,String> picture = e.getPictures();
        databaseReference = FirebaseDatabase.getInstance().getReference("establishment");
        String id = intent.getStringExtra("establishmentId");
        if(establishmentType == 1){
            newEstablishment  = new Apartment_Item(establishmentNameString,contactPerson, contactNumber, contactNumber, price, address, curfewHours, visitorsAllowed, establishmentType, includeBillsInRate, distanceFromCampus[0], security, concealContactPerson, concealPrice, concealUnits, rentYears, furnished,rating,id, e.getOwner_id(), isFixedPrice,review,latitude,longitude,mPlace,unit,e.getNumUnitsAvailable(),picture);
            databaseReference.child(id).setValue(newEstablishment);
        }
        else if(establishmentType == 0){
            newEstablishment = new Dormitory_Item(establishmentNameString,contactPerson, contactNumber,contactNumber, price, address,curfewHours,visitorsAllowed,establishmentType, includeBillsInRate, distanceFromCampus[0], security, concealContactPerson,concealPrice,concealUnits, ratePerHead,intCapacityPerUnit, rating,id,e.getOwner_id(),furniture,review,latitude,longitude,mPlace,unit,e.getNumUnitsAvailable(),acceptedSex,picture);
            newEstablishment = new Dormitory_Item(establishmentNameString,contactPerson, contactNumber,contactNumber, price, address,curfewHours,visitorsAllowed,establishmentType, includeBillsInRate, distanceFromCampus[0], security, concealContactPerson,concealPrice,concealUnits, ratePerHead,intCapacityPerUnit, rating,id,e.getOwner_id(),furniture,review,latitude,longitude,mPlace,unit,e.getNumUnitsAvailable(),acceptedSex,picture);
            databaseReference.child(id).setValue(newEstablishment);
        }
        saveImage(id);

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
        deleteEstablishment();
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

    public void deleteEstablishment(){
        databaseReference = FirebaseDatabase.getInstance().getReference("establishment");
        databaseReference.child(e.getId()).removeValue();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent = getIntent();
        initUser();
        super.onCreate(savedInstanceState);
        actionBar = getActionBar();
        setContentView(R.layout.activity_edit_establishment);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        concealContactPersonSpinner = (Spinner)(findViewById(R.id.concealContactPersonSpinner));
        uriList = new ArrayList<Uri>();
        /*concealContactPersonSpinner.setSelection(1);*/
        concealPriceSpinner = (Spinner)findViewById(R.id.concealPriceSpinner);
        concealUnitsSpinner = (Spinner)findViewById(R.id.concealUnitsSpinner);
        includeBillsInRateSpinner = (Spinner)findViewById(R.id.includeBillsInRateSpinner);
        securitySpinner = (Spinner)findViewById(R.id.securitySpinner);
        visitorsAllowedSpinner = (Spinner)findViewById(R.id.visitorsAllowedSpinner);
        conditionSpinner = (Spinner)findViewById(R.id.conditionSpinner);
        dormitoryPriceLayout = (LinearLayout)findViewById(R.id.editEstablishmentPriceDormitoryLayout);
        apartmentPriceLayout = (LinearLayout)findViewById(R.id.editEstablishmentPriceApartmentLayout);
        apartmentRentYearsLayout = (LinearLayout)findViewById(R.id.editEstablishmentApartmentRentYearsLayout);
        apartmentConditionLayout = (LinearLayout)findViewById(R.id.editEstablishmentApartmentConditionLayout);
        dormitoryCapacityPerUnitLayout = (LinearLayout)findViewById(R.id.editEstablishmentDormitoryCapacityLayout);
        dormFurnitureContainer = (LinearLayout)findViewById(R.id.editEstablishmentFurnitureContainer);
        dormitoryFurnitureLayout = (LinearLayout)findViewById(R.id.editEstablishmentFurnitureLayout);
        dormAcceptedSexLayout = (LinearLayout)findViewById(R.id.editEstablishmentAcceptedSexLayout);
        acceptedSexSpinner = (Spinner)findViewById(R.id.editEstablishmentAcceptedSexSpinner);
        dormitoryFurnitureQty = (EditText)findViewById(R.id.dormitoryFurnitureQty);
        dormitoryFurnitureItem = (EditText)findViewById(R.id.dormitoryFurnitureItem);
        dormitoryAddItemBtn = (Button)findViewById(R.id.addFurnitureBtn);
        dormitoryDeleteItemBtn = (ImageButton)findViewById(R.id.removeFurnitureBtn);
        establishmentName = (EditText)findViewById(R.id.editEstablishmentName);
        dormitoryPrice = (EditText)findViewById(R.id.editEstablishmentPriceDormitory);
        perHead = (CheckBox)findViewById(R.id.checkBoxPerHead);
        minPriceApartment = (EditText)findViewById(R.id.editEstablishmentPriceApartmentMin);
        maxPriceApartment = (EditText)findViewById(R.id.editEstablishmentPriceApartmentMax);
        fixedPriceApartment = (CheckBox)findViewById(R.id.checkBoxFixedRate);
        startCurfewHours = (EditText) findViewById(R.id.editEstablishmentCurfewStart);
        endCurfewHours = (EditText) findViewById(R.id.editEstablishmentCurfewEnd);
        apartmentRentYears = (EditText)findViewById(R.id.editEstablishmentRentYears);
        capacityPerUnitDorm = (EditText)findViewById(R.id.editEstablishmentCapacityPerUnit);
/*        capacityPerUnitDorm.setText("0");
        apartmentRentYears.setText("0");*/
        uploadImageButton = (Button)findViewById(R.id.editEstablishmentUploadImageBtn);
        /*saveImageButton = (Button)findViewById(R.id.addEstablishmentSaveImageBtn);*/
        previewImage = (ImageView)findViewById(R.id.editEstablishmentUploadPhotoPreview);
        addEstablishmentSubmitButton = (Button)findViewById(R.id.editEstablishmentSubmitButton);
        addEstablishmentSubmitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                updateEstablishment();
            }
        });
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        addEstablishmentAddressAutocomplete = (AutoCompleteTextView)findViewById(R.id.editEstablishmentAddress);
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this,mGoogleApiClient,LAT_LNG_BOUNDS,null);
        addEstablishmentAddressAutocomplete.setAdapter(placeAutocompleteAdapter);
        addEstablishmentAddressAutocomplete.setOnItemClickListener(mAutocompleteClickListener);
/*        establishmentTypeSpinner = (Spinner) findViewById(R.id.establishmentTypeSpinner);
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
        });*/
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

        getEstablishmentData();
    }

    public void getEstablishmentData(){
        Establishment_Item retrieved;
        String id = intent.getStringExtra("establishmentId");
        Log.d("Stuff",id);
        final int eType = intent.getIntExtra("establishmentType",1);
        databaseReference = FirebaseDatabase.getInstance().getReference("establishment");
        databaseReference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("WTF","Hey");

                if(eType == 1){

                    e = dataSnapshot.getValue(Apartment_Item.class);
                    if(e == null){
                        finish();
                        return;
                    }
                    setupEstablishmentInputFields(e);
                }
                else if(eType == 0){
                    e = dataSnapshot.getValue(Dormitory_Item.class);
                    if(e == null){
                        finish();
                        return;
                    }
                    setupEstablishmentInputFields(e);
                }else{

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setupEstablishmentInputFields(Establishment_Item item){
        StorageReference previewRef = storageReference.child("establishments/"+item.getId());
        GlideApp.with(getApplicationContext())
                .load(previewRef)
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .into(previewImage);
        mPlace = item.getPlaceInfo();
       // Log.d("Stuff1",e.getEstablishmentName());
        establishmentName.setText(item.getEstablishmentName());
        addEstablishmentAddressAutocomplete.setText(item.getAddress());
        if(item.getEstablishmentType() == 1){
            establishmentType = 1; //if adding apartment
            //hide capacity per unit and dormitory price
            apartmentConditionLayout.setVisibility(View.VISIBLE);
            apartmentPriceLayout.setVisibility(View.VISIBLE);
            apartmentRentYearsLayout.setVisibility(View.VISIBLE);
            dormitoryPriceLayout.setVisibility(View.GONE);
            dormitoryCapacityPerUnitLayout.setVisibility(View.GONE);
            dormitoryFurnitureLayout.setVisibility(View.GONE);
            dormAcceptedSexLayout.setVisibility(View.GONE);
            if(((Apartment_Item)item).getIsFixedPrice() == true){
                fixedPriceApartment.setChecked(true);
                maxPriceApartment.setText(item.getPrice());
            }
            else{
                fixedPriceApartment.setChecked(false);
                String min, max;
                String[] tokens;
                tokens = item.getPrice().split("-");
                min = tokens[0];
                max = tokens[1];
                minPriceApartment.setText(min);
                maxPriceApartment.setText(max);
            }
            apartmentRentYears.setText(((Apartment_Item) item).getRentYears()+"");
            if(((Apartment_Item) item).isFurnished()){
                conditionSpinner.setSelection(0);
            }
            else{
                conditionSpinner.setSelection(1);
            }
        }
        else{
            establishmentType = 0; //if adding dormitory
            dormitoryCapacityPerUnitLayout.setVisibility(View.VISIBLE);
            dormitoryPriceLayout.setVisibility(View.VISIBLE);
            dormitoryFurnitureLayout.setVisibility(View.VISIBLE);
            dormAcceptedSexLayout.setVisibility(View.VISIBLE);
            apartmentConditionLayout.setVisibility(View.GONE);
            apartmentPriceLayout.setVisibility(View.GONE);
            apartmentRentYearsLayout.setVisibility(View.GONE);
            dormitoryPrice.setText(item.getPrice());
            capacityPerUnitDorm.setText(""+((Dormitory_Item)item).getCapacityPerUnit());
            if(((Dormitory_Item)item).isRatePerHead() == true){
                perHead.setChecked(true);
            }
            else{
                perHead.setChecked(false);
            }

            if(((Dormitory_Item)item).getAvailableFurniture() != null){
                Iterator entries = ((Dormitory_Item)item).getAvailableFurniture().entrySet().iterator();
                while (entries.hasNext()){
                    HashMap.Entry entry = (HashMap.Entry) entries.next();
                    String key = (String)entry.getKey();
                    Integer value = (Integer)entry.getValue();
                    addFurniture(key,value);
                }
            }

            if(((Dormitory_Item)item).getAcceptedSex() == -1){
                acceptedSexSpinner.setSelection(0);
            }
            else if(((Dormitory_Item)item).getAcceptedSex() == 1){
                acceptedSexSpinner.setSelection(1);
            }
            else{
                acceptedSexSpinner.setSelection(2);
            }
        }

        if(!item.getCurfewHours().equals("-")){
            String start,end;
            String[] tokens;
            tokens = item.getCurfewHours().split("-");
            start = tokens[0];
            end = tokens[1];
            startCurfewHours.setText(start);
            endCurfewHours.setText(end);
        }
        if(item.isSecurity() == true){
            securitySpinner.setSelection(0);
        }
        else{
            securitySpinner.setSelection(1);
        }

        if(item.isVisitorsAllowed() == true){
            visitorsAllowedSpinner.setSelection(0);
        }
        else{
            visitorsAllowedSpinner.setSelection(1);
        }

        if(item.isConcealContactPerson() == true){
            concealContactPersonSpinner.setSelection(0);
        }
        else{
            concealContactPersonSpinner.setSelection(1);
        }

        if(item.isConcealPrice() == true){
            concealPriceSpinner.setSelection(0);
        }
        else{
            concealPriceSpinner.setSelection(1);
        }
        if(item.isConcealAvailableUnits()){
            concealUnitsSpinner.setSelection(0);
        }
        else{
            concealUnitsSpinner.setSelection(1);
        }

        if(item.isBillsIncludedInRate()){
            includeBillsInRateSpinner.setSelection(0);
        }
        else{
            includeBillsInRateSpinner.setSelection(1);
        }


    }

    public void addFurniture(View v){
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View furnitureView = inflater.inflate(R.layout.furniture_layout,null);
        dormFurnitureContainer.addView(furnitureView,dormFurnitureContainer.getChildCount() - 1);
    }

    public void addFurniture(String item, int quantity){
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View furnitureView = inflater.inflate(R.layout.furniture_layout,null);
        EditText itemName = (EditText)furnitureView.findViewById(R.id.dormitoryFurnitureItem);
        EditText itemQty = (EditText)furnitureView.findViewById(R.id.dormitoryFurnitureQty);
        itemName.setText(item);
        itemQty.setText(""+quantity);
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
                mPlace.setLongitude(place.getLatLng().longitude);
                Log.d(TAG, "onResult: latlng: " + place.getLatLng());
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
