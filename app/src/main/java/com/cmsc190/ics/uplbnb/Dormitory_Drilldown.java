package com.cmsc190.ics.uplbnb;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private DatabaseReference databaseReference;
    public static Dormitory_Item e;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {


        View view =  inflater.inflate(R.layout.fragment_establishment__drilldown_dormitory,container,false);
        //Establishment_Item e = (Establishment_Item) getArguments().getSerializable("e");
        databaseReference = FirebaseDatabase.getInstance().getReference("establishment");
        databaseReference.child(getArguments().getString("id2")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                e = dataSnapshot.getValue(Dormitory_Item.class);
                rating = (RatingBar)getView().findViewById(R.id.drilldownDormitoryRating);
                rating.setRating(e.getRating());
                establishmentName = (TextView) getView().findViewById(R.id.drilldownDormitoryName);
                establishmentName.setText(e.getEstablishmentName());
                establishmentAddress = (TextView)getView().findViewById(R.id.drilldownDormitoryAddress);
                establishmentAddress.setText(e.getAddress());
                contactPerson = (TextView)getView().findViewById(R.id.drilldownDormitoryContactPerson);
                contactPerson.setText(e.getContactPerson());
                contactNumber =  getView().findViewById(R.id.drilldownDormitoryContactNumber1);
                contactNumber.setText(e.getContactNumber1() + "");
                contactNumber2 =  getView().findViewById(R.id.drilldownDormitoryContactNumber2);
                contactNumber2.setText(e.getContactNumber2()+"");
                priceRange =  getView().findViewById(R.id.drilldownDormitoryPriceRange);
                priceRange.setText(e.getPrice() + " PHP");
                if(e.isRatePerHead() == true){
                    priceRange.append(" - per head");
                }
                if(e.isBillsIncludedInRate() == true){
                    priceRange.append(", bills included");
                }
                else{
                    priceRange.append(", bills excluded");
                }


                curfewHours = getView().findViewById(R.id.drilldownDormitoryCurfewHours);
                curfewHours.setText(e.getCurfewHours());
                visitorsAllowed = getView().findViewById(R.id.drilldownDormitoryVisitorsAllowed);
                //visitorsAllowed.setText("Visitors allowed? ");
                if(e.isVisitorsAllowed() == true){
                    visitorsAllowed.setText("Yes");
                }else {
                    visitorsAllowed.setText("No");
                }
                distanceFromCampus = getView().findViewById(R.id.drilldownDormitoryDistanceFromCampus);
                distanceFromCampus.setText(e.getDistanceFromCampus() + "m");
                security = getView().findViewById(R.id.drilldownDormitorySecurity);
                if(e.isSecurity() == true){
                    security.setText("Security measures implemented");
                }
                else{
                    security.setText("No security measures implemented");
                }
                capacityPerUnit = getView().findViewById(R.id.drilldownDormitoryCapacityPerUnit);
                capacityPerUnit.setText(e.getCapacityPerUnit() + " persons");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



//      rentYears.setText("Contract Years: " + ((Dormitory_Item)e).getRentYears());

        return view;
    }



    @Override
    public void onClick(View view){

    }

}
