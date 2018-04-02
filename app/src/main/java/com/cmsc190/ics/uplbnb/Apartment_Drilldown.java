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
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.content.Intent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    public static RatingBar rating;
    public static ImageButton editEstablishmentBtn;
    private DatabaseReference databaseReference;
    public static Apartment_Item e;
    public static FloatingActionButton openMap;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {


        View view =  inflater.inflate(R.layout.fragment_establishment__drilldown,container,false);
        //Establishment_Item e = (Establishment_Item) getArguments().getSerializable("e");
        editEstablishmentBtn = (ImageButton)view.findViewById(R.id.editEstablishmentBtn);
        editEstablishmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editEstablishment();
            }
        });
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
        rentYears = view.findViewById(R.id.drilldownApartmentRentYears);
        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),Map_Activity.class);
                startActivity(i);
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("establishment");
        databaseReference.child(getArguments().getString("id")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                e = dataSnapshot.getValue(Apartment_Item.class);

                rating.setRating(computeRating(e));

                establishmentName.setText(e.getEstablishmentName());
                establishmentAddress.setText(e.getAddress());

                contactPerson.setText(e.getContactPerson());

                contactNumber.setText(e.getContactNumber1() + "");

                contactNumber2.setText(e.getContactNumber2()+"");

                priceRange.setText(e.getPrice() + " PHP");
                if(e.isBillsIncludedInRate() == true){
                    priceRange.append(",bills included");
                }
                else{
                    priceRange.append(",bills not included");
                }

                curfewHours.setText(e.getCurfewHours());

                if(e.isVisitorsAllowed() == true){
                    visitorsAllowed.setText("Yes");
                }else {
                    visitorsAllowed.setText("No");
                }

                distanceFromCampus.setText(e.getDistanceFromCampus() + "m");

                if(e.isSecurity() == true){
                    security.setText("Security measures implemented");
                }
                else{
                    security.setText("No security measures implemented");
                }

                rentYears.setText(e.getRentYears()+" ");



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

    }

}
