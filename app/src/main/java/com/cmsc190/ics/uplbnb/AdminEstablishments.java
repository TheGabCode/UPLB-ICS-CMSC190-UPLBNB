package com.cmsc190.ics.uplbnb;

import android.app.Fragment;
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
import java.util.Collections;
import java.util.List;

/**
 * Created by Dell on 20 Apr 2018.
 */

public class AdminEstablishments extends Fragment {
    private RecyclerView recyclerView;

    private List<Establishment_Item> establishment_items;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refEstablishments;
    public AdminEstablishments() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_list_establishments,container,false);
        establishment_items = new ArrayList<Establishment_Item>();
        refEstablishments = database.getReference("establishment");
        recyclerView = view.findViewById(R.id.recyclerViewAdminEstablishments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        refEstablishments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                establishment_items.clear();
                for (DataSnapshot establishmentSnapshot : dataSnapshot.getChildren()) {
                    Establishment_Item e = establishmentSnapshot.getValue(Establishment_Item.class);
                    establishment_items.add(e);
                }
                Establishment_Item_Adapter e_adapter = new Establishment_Item_Adapter(establishment_items, getActivity(),1);
                recyclerView.setAdapter(e_adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Establishments");
    }
}
