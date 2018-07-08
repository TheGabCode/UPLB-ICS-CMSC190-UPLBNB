package com.cmsc190.ics.uplbnb;

import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;


public class WriteReview extends AppCompatActivity {
    private TextView addReviewCurrentEstablishmentLabel;
    private TextView addReviewEstablishmentType;
    private RatingBar addReviewCurrentEstablishmentRating;
    private RatingBar addReviewRating;
    private EditText addReviewAuthorName;

    private EditText addReviewDescription;
    private Button addReviewSubmitButton;
    private Intent intent;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    DatabaseReference revReference;
    DatabaseReference userRef;
    FirebaseUser firebaseUser;
    public void initUser(){
        userRef = firebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        Log.d("User id","User id " + userId);
        userRef.child("user").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                addReviewAuthorName.setText(user.getFullname());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        intent = getIntent();
        setContentView(R.layout.activity_write_review);
        setTitle("Add a Review");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        addReviewCurrentEstablishmentLabel = findViewById(R.id.addReviewEstablishmentLabel);
        addReviewEstablishmentType = findViewById(R.id.addReviewEstablishmentType);
        addReviewCurrentEstablishmentRating = findViewById(R.id.addReviewCurrentRating);
        addReviewCurrentEstablishmentLabel.setText(intent.getStringExtra("establishmentName"));
        if(intent.getIntExtra("establishmentType",1) == 1){
            addReviewEstablishmentType.setText("Apartment");
        }
        else{
            addReviewEstablishmentType.setText("Dormitory");
        }
        addReviewCurrentEstablishmentRating.setRating(intent.getFloatExtra("establishmentRating",0));

        addReviewRating = (RatingBar) findViewById(R.id.addReviewRatingBar);

        addReviewAuthorName = findViewById(R.id.addReviewAuthorName);

        addReviewDescription = findViewById(R.id.addReviewDescription);
        addReviewSubmitButton = findViewById(R.id.addReviewSubmitButton);

        addReviewSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReview();
            }
        });

        initUser();

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void addReview(){
        String authorname = addReviewAuthorName.getText().toString().trim();
        String reviewTitle = "";
        String description = addReviewDescription.getText().toString().trim();
        float rating = addReviewRating.getRating();
        Review rev = new Review(authorname,description,rating,reviewTitle);
        databaseReference = FirebaseDatabase.getInstance().getReference("establishment").child(intent.getStringExtra("establishmentId")).child("review").push();
        databaseReference.setValue(rev);
        databaseReference = FirebaseDatabase.getInstance().getReference("establishment").child(intent.getStringExtra("establishmentId"));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Establishment_Item e = dataSnapshot.getValue(Establishment_Item.class);
                revReference = FirebaseDatabase.getInstance().getReference("establishment").child(e.getId()).child("rating");
                revReference.setValue(computeRating(e));
                addReviewRating.setRating(computeRating(e));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        })   ;
        finish();
    }

    public float computeRating(Establishment_Item e){
        float totalSum = 0f;

        if(e.review != null){
            int totalReview = e.getReviews().size();
            Iterator entries = e.getReviews().entrySet().iterator();
            while (entries.hasNext()){
                HashMap.Entry entry = (HashMap.Entry) entries.next();
                Review value = (Review)entry.getValue();
                totalSum += value.getRating();
            }
            return totalSum/totalReview;
        }
        else{
            return totalSum;
        }
    }
}
