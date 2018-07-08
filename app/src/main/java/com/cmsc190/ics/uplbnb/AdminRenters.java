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
import java.util.List;

/**
 * Created by Dell on 20 Apr 2018.
 */

public class AdminRenters extends Fragment {
    private RecyclerView recyclerView;
    private List<User> user_items;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refRenters;
    public AdminRenters() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_renters,container,false);
        user_items = new ArrayList<User>();
        refRenters = database.getReference("user");
        refRenters.orderByChild("user_type").equalTo("renter").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_items.clear();
                for (DataSnapshot logSnapshot : dataSnapshot.getChildren()) {
                    User user = logSnapshot.getValue(User.class);

                    user_items.add(user);
                    User_Item_Adapter adapter = new User_Item_Adapter(user_items,getActivity());
                    recyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewAdminRenters);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Renters");
    }
}
