package com.cmsc190.ics.uplbnb;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
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
import android.widget.Toast;

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
    public static TextView openUnits;
    public static TextView acceptedSex;
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
    ImageView addressIcon,contactPersonIcon,contactNumberIcon,priceIcon,availableUnitsIcon,curfewHoursIcon,visitorsAllowedIcon,acceptedSexIcon,capacityIcon,distanceFromCampusIcon,securityIcon,furnitureIcon;

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
                        if(user.getUser_type().equals("renter") || (!user.getId().equals(e.getOwner_id()) && !user.getUser_type().equals("admin")) ){
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
        addressIcon = view.findViewById(R.id.addressIcon);
        contactPersonIcon = view.findViewById(R.id.contactPersonIcon);
        contactNumberIcon = view.findViewById(R.id.contactNumberIcon);
        priceIcon = view.findViewById(R.id.priceIcon);
        availableUnitsIcon = view.findViewById(R.id.availableUnitsIcon);
        curfewHoursIcon = view.findViewById(R.id.curfewHoursIcon);
        visitorsAllowedIcon = view.findViewById(R.id.visitorsIcon);
        distanceFromCampusIcon = view.findViewById(R.id.distanceFromCampusIcon);
        securityIcon = view.findViewById(R.id.securityIcon);
        capacityIcon = view.findViewById(R.id.capacityIcon);
        furnitureIcon = view.findViewById(R.id.furnitureIcon);
        acceptedSexIcon = view.findViewById(R.id.acceptedSexIcon);
        addressIcon.setOnClickListener(this);
        contactPersonIcon.setOnClickListener(this);
        contactNumberIcon.setOnClickListener(this);
        priceIcon.setOnClickListener(this);
        availableUnitsIcon.setOnClickListener(this);
        curfewHoursIcon.setOnClickListener(this);
        visitorsAllowedIcon.setOnClickListener(this);
        distanceFromCampusIcon.setOnClickListener(this);
        securityIcon.setOnClickListener(this);
        capacityIcon.setOnClickListener(this);
        furnitureIcon.setOnClickListener(this);
        acceptedSexIcon.setOnClickListener(this);
        rating = (RatingBar)view.findViewById(R.id.drilldownDormitoryRating);
        openUnits = (TextView)view.findViewById(R.id.drilldownDormitoryOpenUnits);
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
        acceptedSex = view.findViewById(R.id.drilldownDormitoryAcceptedSex);
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
        editEstablishmentBtn.setVisibility(View.GONE);
        openMap = (FloatingActionButton)view.findViewById(R.id.floatingActionButtonGetDirections);
        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manageLocationPermissions();
            }
        });
        //Establishment_Item e = (Establishment_Item) getArguments().getSerializable("e");
        databaseReference = FirebaseDatabase.getInstance().getReference("establishment");
        databaseReference.child(getArguments().getString("id2")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                e = dataSnapshot.getValue(Dormitory_Item.class);
                if(e == null){
                    if(getActivity() != null){
                        getActivity().finish();
                    }
                     return;
                }
                rating.setRating(e.getRating());

                establishmentName.setText(e.getEstablishmentName());
                establishmentType.setText("Dormitory");
                establishmentAddress.setText(e.getAddress());
                if(e.getUnits() != null){
                    openUnits.setText(e.getNumUnitsAvailable() + "/ " + e.getUnits().size() +" units available");
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


                contactNumber.setText(e.getContactNumber1() + "");

                contactNumber2.setText(e.getContactNumber2()+"");
                if(e.isConcealPrice() == true){
                     priceRange.setText("Owner/Manager chooses not to disclose this information, call/text number for details");
                }
                else{
                    priceRange.setText(e.getPrice() + " PHP");
                    if(e.isRatePerHead() == true){
                        priceRange.append(" - per head");
                    }
                    if(e.isBillsIncludedInRate() == true){
                        priceRange.append(" - bills included");
                    }
                    else{
                        priceRange.append(" - bills not included");
                    }
                }


                if(e.getAcceptedSex() == -1){
                    acceptedSex.setText("Coed");
                }
                else if(e.getAcceptedSex() == 1){
                    acceptedSex.setText("Male");
                }
                else{
                    acceptedSex.setText("Female");
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
                headerReference = storageReference.child("establishments/"+e.getId()+"/"+e.getHeaderUrl());
                while (getContext() == null){

                }
                GlideApp.with(getContext())
                        .load(headerReference)
                        .placeholder(R.drawable.logo2)
/*                        .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))*/
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

    public void manageLocationPermissions() {
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
        }
        else{
            Intent i = new Intent(getActivity(),Map_Activity.class);
            i.putExtra("address",establishmentName.getText().toString().trim());
            i.putExtra("latitude",e.getLatitude());
            i.putExtra("longitude",e.getLongitude());
            startActivity(i);
        }

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
        else if(view.getId() == R.id.capacityIcon){
            Toast.makeText(getContext(),"Expected capacity per unit",Toast.LENGTH_SHORT).show();
        }
        else if(view.getId() == R.id.furnitureIcon){
            Toast.makeText(getContext(),"Available furniture, appliances, rooms, misc",Toast.LENGTH_SHORT).show();
        }
        else if(view.getId() == R.id.acceptedSexIcon){
            Toast.makeText(getContext(),"Accepted sex",Toast.LENGTH_SHORT).show();
        }
    }



}
