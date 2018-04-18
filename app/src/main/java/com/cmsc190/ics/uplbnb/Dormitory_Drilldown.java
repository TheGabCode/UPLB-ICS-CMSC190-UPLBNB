package com.cmsc190.ics.uplbnb;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.signature.ObjectKey;
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

public class Dormitory_Drilldown extends Fragment implements View.OnClickListener {
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
    public static TextView furnitureAvailable;
    public static TextView capacityPerUnit;
    public static RatingBar rating;
    public static ImageView headPhoto;
    public static ImageButton editEstablishmentBtn;
    public static TextView establishmentType;
    private DatabaseReference databaseReference;
    StorageReference headerReference;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    public static Dormitory_Item e;
    public static FloatingActionButton openMap;
    FirebaseUser firebaseUser;
    DatabaseReference userRef;
    User user;
    FirebaseDatabase firebaseDatabase;

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
                        if(user.getUser_type().equals("renter") || !user.getId().equals(e.getOwner_id()) ){
                            editEstablishmentBtn.setVisibility(View.GONE);
                        }
                        else if(user.getId().equals(e.getOwner_id())){
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


        View view =  inflater.inflate(R.layout.fragment_dormitory_drilldown,container,false);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        rating = (RatingBar)view.findViewById(R.id.drilldownDormitoryRating);
        establishmentType = (TextView)view.findViewById(R.id.drilldownDormitoryType);
        furnitureAvailable = (TextView)view.findViewById(R.id.drilldownDormitoryAvailableFurniture);
        headPhoto = (ImageView)view.findViewById(R.id.imageDormitory);
        establishmentAddress = (TextView)view.findViewById(R.id.drilldownDormitoryAddress);
        establishmentName = (TextView) view.findViewById(R.id.drilldownDormitoryName);
        contactPerson = (TextView)view.findViewById(R.id.drilldownDormitoryContactPerson);
        contactNumber2 =  view.findViewById(R.id.drilldownDormitoryContactNumber2);
        contactNumber =  view.findViewById(R.id.drilldownDormitoryContactNumber1);
        priceRange =  view.findViewById(R.id.drilldownDormitoryPriceRange);
        security = view.findViewById(R.id.drilldownDormitorySecurity);
        curfewHours = view.findViewById(R.id.drilldownDormitoryCurfewHours);
        visitorsAllowed = view.findViewById(R.id.drilldownDormitoryVisitorsAllowed);
        distanceFromCampus = view.findViewById(R.id.drilldownDormitoryDistanceFromCampus);
        capacityPerUnit = view.findViewById(R.id.drilldownDormitoryCapacityPerUnit);
        editEstablishmentBtn = (ImageButton)view.findViewById(R.id.editEstablishmentBtn);
        editEstablishmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editEstablishment();
            }
        });
        openMap = (FloatingActionButton)view.findViewById(R.id.floatingActionButtonGetDirections);
        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),Map_Activity.class);
                i.putExtra("address",establishmentName.getText().toString().trim());
                i.putExtra("latitude",e.getLatitude());
                i.putExtra("longitude",e.getLongitude());
                startActivity(i);
            }
        });
        //Establishment_Item e = (Establishment_Item) getArguments().getSerializable("e");
        databaseReference = FirebaseDatabase.getInstance().getReference("establishment");
        databaseReference.child(getArguments().getString("id2")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                e = dataSnapshot.getValue(Dormitory_Item.class);
                if(e == null){
                    getActivity().finish();
                    return;
                }
                rating.setRating(e.getRating());

                establishmentName.setText(e.getEstablishmentName());
                establishmentType.setText("Dormitory");
                establishmentAddress.setText(e.getAddress());

                contactPerson.setText(e.getContactPerson());

                contactNumber.setText(e.getContactNumber1() + "");

                contactNumber2.setText(e.getContactNumber2()+"");

                priceRange.setText(e.getPrice() + " PHP");
                if(e.isRatePerHead() == true){
                    priceRange.append(" - per head");
                }
                if(e.isBillsIncludedInRate() == true){
                    priceRange.append(", bills included");
                }
                else{
                    priceRange.append(", bills not included");
                }



                if(e.getCurfewHours().equals("-")){
                    curfewHours.setText("No curfew hours");
                }
                else{
                    curfewHours.setText(e.getCurfewHours());
                }

                //visitorsAllowed.setText("Visitors allowed? ");
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

                furnitureAvailable.setText(stringifyFurniture(e.getAvailableFurniture()));
                capacityPerUnit.setText(e.getCapacityPerUnit() + " persons");
                headerReference = storageReference.child("establishments/"+e.getId());
                GlideApp.with(getContext())
                        .load(headerReference)
                        .placeholder(R.drawable.logo2)
                        .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                        .into(headPhoto);
                initUser();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



//      rentYears.setText("Contract Years: " + ((Dormitory_Item)e).getRentYears());

        return view;
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

    public void editEstablishment(){
        Intent i = new Intent(getActivity(),EditEstablishment.class);
        i.putExtra("establishmentType",0);
        i.putExtra("establishmentId",e.getId());
        startActivity(i);
    }

    @Override
    public void onClick(View view){

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
