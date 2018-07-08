package com.cmsc190.ics.uplbnb;

import android.content.Context;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Dell on 26 Feb 2018.
 */

public class Apartment_Drilldown extends Fragment implements View.OnClickListener {
    public static TextView establishmentName;
    public static TextView establishmentAddress;
    public static TextView contactPerson;
    public static TextView contactNumber;
    public static TextView contactNumber2;
    public static TextView priceRange;
    public static TextView curfewHours;
    public static TextView visitorsAllowed;
    public static TextView distanceFromCampus;
    public static TextView security;
    public static TextView rentYears;
    public static TextView condition;
    public static TextView openUnits;
    public static RatingBar rating;
    public static ImageView headPhoto;
    public static ImageButton editEstablishmentBtn;
    public static TextView establishmentType;
    private DatabaseReference databaseReference;
    public static Apartment_Item e;
    StorageReference headerReference;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    double addressLat;
    double addressLong;
    public static FloatingActionButton openMap;
    FirebaseUser firebaseUser;
    User user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userRef;
    ImageView addressIcon,contactPersonIcon,contactNumberIcon,priceIcon,availableUnitsIcon,curfewHoursIcon,visitorsAllowedIcon,distanceFromCampusIcon,securityIcon,rentYearsIcon,conditionIcon;

    public void initUser(){
        userRef = firebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        Log.d("User id","User id " + userId);
        userRef.child("user").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if(firebaseUser == null){
                    editEstablishmentBtn.setVisibility(View.GONE);
                }else{
                    if(user != null){
                        if(user.getUser_type().equals("renter") || (!user.getId().equals(e.getOwner_id()) && !user.getUser_type().equals("admin"))){
                            editEstablishmentBtn.setVisibility(View.GONE);
                        }
                        else if(user.getId().equals(e.getOwner_id()) || user.getUser_type().equals("admin")){
                            editEstablishmentBtn.setVisibility(View.VISIBLE);
                        }
                    }
                    else{
                        Log.d("User id",firebaseUser.getUid());
                    }

                }
                Log.d("User id","XXX" + user.getFullname());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {


        View view =  inflater.inflate(R.layout.fragment_apartment_drilldown,container,false);
        //Establishment_Item e = (Establishment_Item) getArguments().getSerializable("e");
        addressIcon = view.findViewById(R.id.addressIcon);
        contactPersonIcon = view.findViewById(R.id.contactPersonIcon);
        contactNumberIcon = view.findViewById(R.id.contactNumberIcon);
        priceIcon = view.findViewById(R.id.priceIcon);
        availableUnitsIcon = view.findViewById(R.id.availableUnitsIcon);
        curfewHoursIcon = view.findViewById(R.id.curfewHoursIcon);
        visitorsAllowedIcon = view.findViewById(R.id.visitorsIcon);
        distanceFromCampusIcon = view.findViewById(R.id.distanceFromCampusIcon);
        securityIcon = view.findViewById(R.id.securityIcon);
        rentYearsIcon = view.findViewById(R.id.contractYearsIcon);
        conditionIcon = view.findViewById(R.id.conditionIcon);
        addressIcon.setOnClickListener(this);
        contactPersonIcon.setOnClickListener(this);
        contactNumberIcon.setOnClickListener(this);
        priceIcon.setOnClickListener(this);
        availableUnitsIcon.setOnClickListener(this);
        curfewHoursIcon.setOnClickListener(this);
        visitorsAllowedIcon.setOnClickListener(this);
        distanceFromCampusIcon.setOnClickListener(this);
        securityIcon.setOnClickListener(this);
        rentYearsIcon.setOnClickListener(this);
        conditionIcon.setOnClickListener(this);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        establishmentType = (TextView)view.findViewById(R.id.drilldownApartmentType);
        openUnits = (TextView)view.findViewById(R.id.drilldownApartmentOpenUnits);
        editEstablishmentBtn = (ImageButton)view.findViewById(R.id.editEstablishmentBtn);
        editEstablishmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editEstablishment();
            }
        });
        editEstablishmentBtn.setVisibility(View.GONE);
        rating = (RatingBar)view.findViewById(R.id.drilldownApartmentRating);
        establishmentName = (TextView) view.findViewById(R.id.drilldownApartmentName);
        establishmentAddress = (TextView)view.findViewById(R.id.drilldownAparmentAddress);
        contactPerson = (TextView)view.findViewById(R.id.drilldownAparmentContactPerson);
        contactNumber2 =  view.findViewById(R.id.drilldownApartmentContactNumber2);
        contactNumber =  view.findViewById(R.id.drilldownApartmentContactNumber1);
        priceRange =  view.findViewById(R.id.drilldownApartmentPriceRange);
        curfewHours = view.findViewById(R.id.drilldownAparmentCurfewHours);
        visitorsAllowed = view.findViewById(R.id.drilldownApartmentVisitorsAllowed);
        distanceFromCampus = view.findViewById(R.id.drilldownApartmentDistanceFromCampus);
        security = view.findViewById(R.id.drilldownApartmentSecurity);
        condition = view.findViewById(R.id.drilldownApartmentCondition);
        openMap = (FloatingActionButton)view.findViewById(R.id.floatingActionButtonGetDirections);
        headPhoto = (ImageView)view.findViewById(R.id.imageViewApartment);
        rentYears = view.findViewById(R.id.drilldownApartmentRentYears);
        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manageLocationPermissions();

            }
        });
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        databaseReference = FirebaseDatabase.getInstance().getReference("establishment");
        databaseReference.child(getArguments().getString("id")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                e = dataSnapshot.getValue(Apartment_Item.class);
                if(e == null){
                    if(getActivity() != null){
                        getActivity().finish();
                    }
                    return;




                }
                if(e != null){
                    addressLat = e.getLatitude();
                    addressLong = e.getLongitude();
                    rating.setRating(e.getRating());
                    establishmentType.setText("Apartment");
                    establishmentName.setText(e.getEstablishmentName());
                    establishmentAddress.setText(e.getAddress());
                    if(e.getUnits() != null){
                        openUnits.setText(e.getNumUnitsAvailable() + " / " + e.getUnits().size() + " units available");
                    }
                    else{
                        openUnits.setText("No available information yet");
                    }

                    if(e.isConcealContactPerson() == true){
                        contactPerson.setText("Owner/Manager chooses not to disclose this information, call/text number for details");
                    }
                    else{
                        contactPerson.setText(e.getContactPerson());
                    }

                    if(e.getContactNumber1() != null){
                        contactNumber.setText(e.getContactNumber1() + "");
                    }
                    else{
                        contactNumber.setText("No available information");
                    }
                    if(e.getContactNumber2() != null){
                        contactNumber2.setText(e.getContactNumber2()+"");
                    }
                    else{
                        contactNumber2.setText("No available information");
                    }



                    if(e.isConcealPrice() == true){
                        priceRange.setText("Owner/Manager chooses not to disclose this information, call/text number for details");
                    }
                    else{
                        priceRange.setText(e.getPrice() + " PHP");
                        if(e.isBillsIncludedInRate() == true){
                            priceRange.append(" - bills included");
                        }
                        else{
                            priceRange.append(" - bills not included");
                        }
                    }


                    if(e.getCurfewHours().equals("-")){
                        curfewHours.setText("No curfew hours");
                    }
                    else{
                        curfewHours.setText(e.getCurfewHours());
                    }


                    if(e.isVisitorsAllowed() == true){
                        visitorsAllowed.setText("Visitors allowed");
                    }else {
                        visitorsAllowed.setText("No visitors allowed");
                    }
                    if(e.getDistanceFromCampus() >= 1000){
                        distanceFromCampus.setText(Math.floor((e.getDistanceFromCampus()/1000)) + "km");
                    }
                    else{
                        distanceFromCampus.setText(Math.floor(e.getDistanceFromCampus()) + "m");
                    }


                    if(e.isSecurity() == true){
                        security.setText("Security measures implemented");
                    }
                    else{
                        security.setText("No security measures implemented");
                    }

                    rentYears.setText(e.getRentYears()+" years");



                    if(e.isFurnished() == true){
                        condition.setText("Furnished");
                    }
                    else
                    {
                        condition.setText("Unfurnished");
                    }
                    headerReference = storageReference.child("establishments/"+e.getId()+"/"+e.getHeaderUrl());
                    while (getContext() == null){

                    }
                    GlideApp.with(getContext())
                            .load(headerReference)
                            .placeholder(R.drawable.logo2)
/*                            .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))*/
                            .into(headPhoto);
                    initUser();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }


    public void manageLocationPermissions() {
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
        }
        else{
            Intent i = new Intent(getActivity(),Map_Activity.class);
            i.putExtra("address",establishmentName.getText().toString().trim());
            i.putExtra("latitude",addressLat);
            i.putExtra("longitude",addressLong);
            startActivity(i);
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            switch (requestCode) {
                case 1:
                    Intent i = new Intent(getActivity(),Map_Activity.class);
                    i.putExtra("address",establishmentName.getText().toString().trim());
                    i.putExtra("latitude",addressLat);
                    i.putExtra("longitude",addressLong);
                    startActivity(i);
                    break;
            }
        }
    }



    public void editEstablishment(){
        Intent i = new Intent(getActivity(),EditEstablishment.class);
        i.putExtra("establishmentType",1);
        i.putExtra("establishmentId",e.getId());
        startActivity(i);
    }
    public void stringifyFurniture(){

    }

    public float computeRating(Establishment_Item e){
        e.getReviews();
        float totalSum = 0f;
        if(e.getReviews() != null){
            int totalReview = e.getReviews().size();
            Iterator entries = e.getReviews().entrySet().iterator();
            while (entries.hasNext()){
                HashMap.Entry entry = (HashMap.Entry) entries.next();
                Review value = (Review)entry.getValue();
                totalSum += value.getRating();
            }
            return totalSum/totalReview;
        }
        return totalSum;
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.addressIcon){
            Toast.makeText(getContext(),"Address",Toast.LENGTH_SHORT).show();
        }
        else if(view.getId() == R.id.contactPersonIcon){
            Toast.makeText(getContext(),"Contact Person",Toast.LENGTH_SHORT).show();
        }
        else if(view.getId() == R.id.contactNumberIcon){
            Toast.makeText(getContext(),"Contact Info",Toast.LENGTH_SHORT).show();
        }
        else if(view.getId() == R.id.priceIcon){
            Toast.makeText(getContext(),"Expected monthly Rate",Toast.LENGTH_SHORT).show();
        }
        else if(view.getId() == R.id.availableUnitsIcon){
            Toast.makeText(getContext(),"Units available",Toast.LENGTH_SHORT).show();
        }
        else if(view.getId() == R.id.curfewHoursIcon){
            Toast.makeText(getContext(),"Curfew Hours",Toast.LENGTH_SHORT).show();
        }
        else if(view.getId() == R.id.visitorsIcon){
            Toast.makeText(getContext(),"Visitors allowed?",Toast.LENGTH_SHORT).show();
        }
        else if(view.getId() == R.id.distanceFromCampusIcon){
            Toast.makeText(getContext(),"Distance from campus",Toast.LENGTH_SHORT).show();
        }
        else if(view.getId() == R.id.securityIcon){
            Toast.makeText(getContext(),"Security?",Toast.LENGTH_SHORT).show();
        }
        else if(view.getId() == R.id.contractYearsIcon){
            Toast.makeText(getContext(),"Contract Years",Toast.LENGTH_SHORT).show();
        }
        else if(view.getId() == R.id.conditionIcon){
            Toast.makeText(getContext(),"Condition",Toast.LENGTH_SHORT).show();
        }

    }

}
