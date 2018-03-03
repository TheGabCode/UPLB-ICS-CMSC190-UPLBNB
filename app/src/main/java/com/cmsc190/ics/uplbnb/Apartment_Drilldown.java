package com.cmsc190.ics.uplbnb;

import android.support.design.widget.FloatingActionButton;
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
import android.content.Intent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    public static RatingBar rating;
    private DatabaseReference databaseReference;
    public static Apartment_Item e;
    public static FloatingActionButton openMap;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {


        View view =  inflater.inflate(R.layout.fragment_establishment__drilldown,container,false);
        //Establishment_Item e = (Establishment_Item) getArguments().getSerializable("e");
        openMap = (FloatingActionButton)view.findViewById(R.id.floatingActionButtonGetDirections);
        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),Map_Activity.class);
                startActivity(i);
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("establishment");
        databaseReference.child(getArguments().getString("id")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                e = dataSnapshot.getValue(Apartment_Item.class);
                rating = (RatingBar)getView().findViewById(R.id.drilldownApartmentRating);
                rating.setRating(e.getRating());
                establishmentName = (TextView) getView().findViewById(R.id.drilldownApartmentName);
                establishmentName.setText(e.getEstablishmentName());
                establishmentAddress = (TextView)getView().findViewById(R.id.drilldownAparmentAddress);
                establishmentAddress.setText(e.getAddress());
                contactPerson = (TextView)getView().findViewById(R.id.drilldownAparmentContactPerson);
                contactPerson.setText(e.getContactPerson());
                contactNumber =  getView().findViewById(R.id.drilldownApartmentContactNumber1);
                contactNumber.setText(e.getContactNumber1() + "");
                contactNumber2 =  getView().findViewById(R.id.drilldownApartmentContactNumber2);
                contactNumber2.setText(e.getContactNumber2()+"");
                priceRange =  getView().findViewById(R.id.drilldownApartmentPriceRange);
                priceRange.setText(e.getPrice() + " PHP");
                if(e.isBillsIncludedInRate() == true){
                    priceRange.append(",bills included");
                }
                else{
                    priceRange.append(",bills not included");
                }
                curfewHours = getView().findViewById(R.id.drilldownAparmentCurfewHours);
                curfewHours.setText(e.getCurfewHours());
                visitorsAllowed = getView().findViewById(R.id.drilldownApartmentVisitorsAllowed);
                if(e.isVisitorsAllowed() == true){
                    visitorsAllowed.setText("Yes");
                }else {
                    visitorsAllowed.setText("No");
                }
                distanceFromCampus = getView().findViewById(R.id.drilldownApartmentDistanceFromCampus);
                distanceFromCampus.setText(e.getDistanceFromCampus() + "m");
                security = getView().findViewById(R.id.drilldownApartmentSecurity);
                if(e.isSecurity() == true){
                    security.setText("Security measures implemented");
                }
                else{
                    security.setText("No security measures implemented");
                }
                rentYears = getView().findViewById(R.id.drilldownApartmentRentYears);
                rentYears.setText(e.getRentYears()+" ");

                condition = getView().findViewById(R.id.drilldownApartmentCondition);

                if(e.isFurnished() == true){
                    condition.setText("Furnished");
                }
                else
                {
                    condition.setText("Unfurnished");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



//      rentYears.setText("Contract Years: " + ((Apartment_Item)e).getRentYears());

        return view;
    }



    @Override
    public void onClick(View view){

    }

}
