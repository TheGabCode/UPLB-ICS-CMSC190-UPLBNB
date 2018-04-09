package com.cmsc190.ics.uplbnb;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dell on 6 Mar 2018.
 */

public class Units_List extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Unit_Item> unit_items;
    Button addUnitFab;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refUnits;
    String contactNumber1;
    int establishmentType;
    String establishmentId;
    boolean ratePerHead;
    HashMap<String,Integer> furniture;
    Establishment_Item e;
    public void getEstablishmentData(String establishmentId, final int establishmentType){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("establishment");
        ref.child(establishmentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(establishmentType == 1){
                    e = dataSnapshot.getValue(Apartment_Item.class);
                }
                else{
                    e = dataSnapshot.getValue(Dormitory_Item.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unit_list,container,false);
        contactNumber1 = getArguments().getString("establishmentContact");
        establishmentType = getArguments().getInt("establishmentType");
        establishmentId = getArguments().getString("establishmentId");
        getEstablishmentData(establishmentId,establishmentType);
/*        ratePerHead = getArguments().getBoolean("ratePerHead");
        furniture = (HashMap<String,Integer>)getArguments().getSerializable("furniture");*/
        refUnits = FirebaseDatabase.getInstance().getReference("establishment");
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewUnitList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        addUnitFab = (Button)view.findViewById(R.id.addUnitButton);
        addUnitFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUnit();
            }
        });
        unit_items = new ArrayList<>();
        adapter = new Unit_Item_Adapter(unit_items,getActivity(),contactNumber1,establishmentType,establishmentId);

        recyclerView.setAdapter(adapter);
        addUnitFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUnit();
            }
        });
        refUnits.child(getArguments().getString("establishmentId")).child("unit").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                unit_items.clear();
                for(DataSnapshot unitSnapshot : dataSnapshot.getChildren()){
                    Unit_Item u = unitSnapshot.getValue(Unit_Item.class);
                    unit_items.add(u);
                }
                Unit_Item_Adapter ue = new Unit_Item_Adapter(unit_items,getActivity(),contactNumber1,establishmentType,establishmentId);
                recyclerView.setAdapter(ue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;

    }

    @Override
    public void onClick(View view) {

    }

    public void addUnit(){
        Intent i = new Intent(getActivity(),AddUnit.class);
        i.putExtra("establishment",e);
        i.putExtra("establishmentType",establishmentType);
        i.putExtra("establishmentId",establishmentId);
        if(establishmentType == 0){
            i.putExtra("ratePerHead",((Dormitory_Item)e).isRatePerHead());
            i.putExtra("furniture",((Dormitory_Item)e).getAvailableFurniture());
        }else{
            i.putExtra("ratePerHead",false);
            i.putExtra("furniture",new HashMap<String,Integer>());
        }

/*        i.putExtra("ratePerHead",ratePerHead);
        i.putExtra("furniture",furniture);*/
        startActivity(i);
    }
}
