package com.cmsc190.ics.uplbnb;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 6 Mar 2018.
 */

public class Units_List extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Unit_Item> unit_items;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refUnits;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unit_list,container,false);
        refUnits = FirebaseDatabase.getInstance().getReference("establishment");
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewUnitList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        unit_items = new ArrayList<>();
        adapter = new Unit_Item_Adapter(unit_items,getActivity(),getArguments().getString("establishmentContact"));

        recyclerView.setAdapter(adapter);

        refUnits.child(getArguments().getString("establishmentId")).child("unit").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                unit_items.clear();
                for(DataSnapshot unitSnapshot : dataSnapshot.getChildren()){
                    Unit_Item u = unitSnapshot.getValue(Unit_Item.class);
                    unit_items.add(u);
                }
                Unit_Item_Adapter ue = new Unit_Item_Adapter(unit_items,getActivity(),getArguments().getString("establishmentContact"));
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
}
