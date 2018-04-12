package com.cmsc190.ics.uplbnb;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RenterHome extends AppCompatActivity implements FilterDialogFragment.OnInputListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Establishment_Item> establishment_items;
    Spinner sortSpinner;
    Button filterButton;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refEstablishments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renter_home);
        establishment_items = new ArrayList<>();


        //Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        //setSupportActionBar(toolbar);
        filterButton = (Button)findViewById(R.id.filterOption);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterMenu();
            }
        });
        refEstablishments = database.getReference("establishment");
        refEstablishments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                establishment_items.clear();
                for (DataSnapshot establishmentSnapshot : dataSnapshot.getChildren()) {
                    Establishment_Item e = establishmentSnapshot.getValue(Establishment_Item.class);

                    establishment_items.add(e);

                }
                Collections.sort(establishment_items, new EstablishmentNameComparatorAscending());
                Establishment_Item_Adapter e_adapter = new Establishment_Item_Adapter(establishment_items, getApplicationContext());
                recyclerView.setAdapter(e_adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewHome);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sortSpinner = (Spinner) findViewById(R.id.sortspinner);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Establishment_Item_Adapter e_adapter;
                switch (i) {
                    case 0:
                        //Sort A-Z
                        Collections.sort(establishment_items, new EstablishmentNameComparatorAscending());
                        //e_adapter = new Establishment_Item_Adapter(establishment_items, getApplicationContext());
                        //recyclerView.setAdapter(e_adapter);

                        break;
                    case 1:
                        //Sort Z-A
                        Collections.sort(establishment_items, new EstablishmentNameComparatorDescending());
                        //e_adapter = new Establishment_Item_Adapter(establishment_items, getApplicationContext());
                        //recyclerView.setAdapter(e_adapter);
                        break;
                    case 2:
                        Collections.sort(establishment_items, new EstablishmentPriceComparatorAscending());
                        //Sort price low to high
                        break;
                    case 3:
                        Collections.sort(establishment_items, new EstablishmentPriceComparatorDescending());
                        //Sort price high to low
                        break;
                    case 4:
                        //Sort rating low to high
                        Collections.sort(establishment_items, new EstablishmentRatingComparatorAscending());
                        break;
                    case 5:
                        //Sort rating high to low
                        Collections.sort(establishment_items, new EstablishmentRatingComparatorDescending());
                        break;
                    case 6:
                        //Sort distance from campus low to high
                        Collections.sort(establishment_items, new EstablishmentDistanceComparatorAscending());
                        break;
                    case 7:
                        //Sort distance from high to low
                        Collections.sort(establishment_items, new EstablishmentDistanceComparatorDescending());
                        break;


                }
                e_adapter = new Establishment_Item_Adapter(establishment_items, getApplicationContext());
                recyclerView.setAdapter(e_adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        Toast.makeText(getApplicationContext(), "Successfully logged in", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }



    public void filterMenu(){
        DialogFragment dialogFragment = new FilterDialogFragment();
        dialogFragment.show(getFragmentManager(),"filters");
    }

    @Override
    public void sendFilter(FilterInfo filterInfo) {
        double minPriceFilter = filterInfo.getMinPrice();
        double maxPriceFilter = filterInfo.getMaxPrice();
        double comparePrice;
        double minComparePrice, maxComparePrice;
        boolean add = true;
        Establishment_Item_Adapter e_adapter;
        List<Establishment_Item> filtered_establishment_items = new ArrayList<Establishment_Item>();
        for(int i = 0; i < establishment_items.size(); i++){
            add = true;
            if(filterInfo.getEstablishmentType() != -1){ //if 1 or 0
                if(establishment_items.get(i).getEstablishmentType() != filterInfo.getEstablishmentType()){
                    add = false;
                }
            }
            if(establishment_items.get(i).getPrice().contains("-")){
                String[] token;
                token = establishment_items.get(i).getPrice().split("-");
                    minComparePrice = Double.parseDouble(token[0]);
                    maxComparePrice = Double.parseDouble(token[1]);
                    if(minComparePrice < minPriceFilter){
                        add = false;
                    }
                    if(minComparePrice > maxPriceFilter){
                        add = false;
                    }
                    if(maxComparePrice > maxPriceFilter){
                        add = false;
                    }
                    if(maxComparePrice < minPriceFilter){
                        add = false;
                    }
            }
            else{
                comparePrice = Double.parseDouble(establishment_items.get(i).getPrice());
                if(comparePrice < minPriceFilter){
                    add = false;
                }
                else if(comparePrice > maxPriceFilter){
                    add = false;
                }
            }

            if(filterInfo.getVacancies() != -1){
                if(filterInfo.getVacancies() == 1){
                    if(establishment_items.get(i).getNumUnitsAvailable() <= 0){
                        add = false;
                    }
                }
            }

            if(filterInfo.getCurfew() != -1){
                if(filterInfo.getCurfew() == 1){
                    if(establishment_items.get(i).getCurfewHours().equals("-")){
                        add = false;
                    }
                }
                else if(filterInfo.getCurfew() == 0){
                    if(!establishment_items.get(i).getCurfewHours().equals("-")){
                        add = false;
                    }
                }
            }

            if(filterInfo.getVisitors() != -1){
                if(filterInfo.getVisitors() == 1){
                    if(establishment_items.get(i).isVisitorsAllowed() == false){
                        add = false;
                    }
                }
                else if(filterInfo.getVisitors() == 0){
                    if(establishment_items.get(i).isVisitorsAllowed() == true){
                        add = false;
                    }
                }
            }

            if(filterInfo.getSecurity() != -1){
                if(filterInfo.getSecurity() == 1){
                    if(establishment_items.get(i).isSecurity() == false){
                        add = false;
                    }
                }
                else if(filterInfo.getSecurity() == 0){
                    if(establishment_items.get(i).isSecurity() == true){
                        add = false;
                    }
                }
            }

            if(establishment_items.get(i).getRating() < filterInfo.getRating()){
                add = false;
            }



            if(add == true){
                filtered_establishment_items.add(establishment_items.get(i));
            }
        }
        e_adapter = new Establishment_Item_Adapter(filtered_establishment_items, getApplicationContext());
        recyclerView.setAdapter(e_adapter);

        /*        Toast.makeText(getApplicationContext(),rating+"",Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(),establishment_items.get(0).getEstablishmentName(),Toast.LENGTH_SHORT).show();*/
    }

    class EstablishmentNameComparatorAscending implements Comparator<Establishment_Item> {
        @Override
        public int compare(Establishment_Item a, Establishment_Item b) {
            return a.getEstablishmentName().compareToIgnoreCase(b.getEstablishmentName());
        }
    }

    class EstablishmentNameComparatorDescending implements Comparator<Establishment_Item> {
        @Override
        public int compare(Establishment_Item a, Establishment_Item b) {
            return b.getEstablishmentName().compareToIgnoreCase(a.getEstablishmentName());
        }
    }

    class EstablishmentPriceComparatorAscending implements Comparator<Establishment_Item> {
        @Override
        public int compare(Establishment_Item a, Establishment_Item b) {
            double aPrice;
            double bPrice;
            if (a.getEstablishmentType() == 1) { //if apartment
                    String[] token;
                    token = a.getPrice().split("-");
                    if(token.length == 2){
                        aPrice = (Double.parseDouble(token[0]) + Double.parseDouble(token[1])) / 2;
                    }
                    else{
                        aPrice = Double.parseDouble(a.getPrice());
                    }
            } else {
                aPrice = Double.parseDouble(a.getPrice());
            }

            if (b.getEstablishmentType() == 1) { //if apartment
                String[] token;
                token = b.getPrice().split("-");
                if(token.length == 2){
                    bPrice = (Double.parseDouble(token[0]) + Double.parseDouble(token[1])) / 2;
                }
                else{
                    bPrice = Double.parseDouble(b.getPrice());
                }
            } else {
                bPrice = Double.parseDouble(b.getPrice());
            }
            return aPrice < bPrice ? -1 : aPrice == bPrice ? 0 : 1;
        }
    }
    class EstablishmentPriceComparatorDescending implements Comparator<Establishment_Item> {
        @Override
        public int compare(Establishment_Item a, Establishment_Item b) {
            double aPrice;
            double bPrice;
            if (a.getEstablishmentType() == 1) { //if apartment
                String[] token;
                token = a.getPrice().split("-");
                if(token.length == 2){
                    aPrice = (Double.parseDouble(token[0]) + Double.parseDouble(token[1])) / 2;
                }
                else{
                    aPrice = Double.parseDouble(a.getPrice());
                }
            } else {
                aPrice = Double.parseDouble(a.getPrice());
            }
            if (b.getEstablishmentType() == 1) { //if apartment
                String[] token;
                token = b.getPrice().split("-");
                if(token.length == 2){
                    bPrice = (Double.parseDouble(token[0]) + Double.parseDouble(token[1])) / 2;
                }
                else{
                    bPrice = Double.parseDouble(b.getPrice());
                }
            } else {
                bPrice = Double.parseDouble(b.getPrice());
            }
            return aPrice > bPrice ? -1 : aPrice == bPrice ? 0 : 1;
        }
    }

    class EstablishmentRatingComparatorAscending implements Comparator<Establishment_Item> {
        @Override
        public int compare(Establishment_Item a, Establishment_Item b) {
            return a.getRating() < b.getRating() ? -1 : a.getRating() == b.getRating() ? 0 : 1;
        }
    }

    class EstablishmentRatingComparatorDescending implements Comparator<Establishment_Item> {
        @Override
        public int compare(Establishment_Item a, Establishment_Item b) {
            return a.getRating() > b.getRating() ? -1 : a.getRating() == b.getRating() ? 0 : 1;
        }
    }

    class EstablishmentDistanceComparatorAscending implements Comparator<Establishment_Item> {
        @Override
        public int compare(Establishment_Item a, Establishment_Item b) {
            return a.getDistanceFromCampus() < b.getDistanceFromCampus() ? -1 : a.getDistanceFromCampus() == b.getDistanceFromCampus() ? 0 : 1;
        }
    }

    class EstablishmentDistanceComparatorDescending implements Comparator<Establishment_Item> {
        @Override
        public int compare(Establishment_Item a, Establishment_Item b) {
            return a.getDistanceFromCampus() > b.getDistanceFromCampus() ? -1 : a.getDistanceFromCampus() == b.getDistanceFromCampus() ? 0 : 1;
        }
    }
}
