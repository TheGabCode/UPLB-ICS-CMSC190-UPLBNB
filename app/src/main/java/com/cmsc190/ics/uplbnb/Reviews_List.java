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
import android.widget.Button;
import android.content.Intent;

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

public class Reviews_List extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Review> review_items;
    private Button addReviewButton;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refReviews;

    public Reviews_List(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_list,container,false);
        refReviews = FirebaseDatabase.getInstance().getReference("establishment");
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewReviewList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        addReviewButton = view.findViewById(R.id.writeReviewButton);
        addReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),WriteReview.class);
                i.putExtra("establishmentName",getArguments().getString("establishmentName"));
                i.putExtra("establishmentType",getArguments().getInt("establishmentType"));
                i.putExtra("establishmentRating",getArguments().getFloat("establishmentRating"));
                i.putExtra("establishmentId",getArguments().getString("establishmentId"));

                startActivity(i);
                    //move to write review intent
            }
        });
        review_items = new ArrayList<>();
        adapter = new Review_Item_Adapter(review_items,getActivity());

        recyclerView.setAdapter(adapter);

        refReviews.child(getArguments().getString("establishmentId")).child("review").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                review_items.clear();
                for(DataSnapshot reviewSnapshot : dataSnapshot.getChildren()){
                    Review review = reviewSnapshot.getValue(Review.class);
                    review_items.add(review);
                }
                Review_Item_Adapter ra = new Review_Item_Adapter(review_items,getActivity());
                recyclerView.setAdapter(ra);
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
