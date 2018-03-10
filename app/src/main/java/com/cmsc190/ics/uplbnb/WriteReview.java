package com.cmsc190.ics.uplbnb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.content.Intent;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class WriteReview extends AppCompatActivity {
    private TextView addReviewCurrentEstablishmentLabel;
    private TextView addReviewEstablishmentType;
    private RatingBar addReviewCurrentEstablishmentRating;
    private RatingBar addReviewRating;
    private EditText addReviewAuthorName;
    private EditText addReviewTitle;
    private EditText addReviewDescription;
    private Button addReviewSubmitButton;
    private Intent intent;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        setContentView(R.layout.activity_write_review);
        setTitle("Add a Review");
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
        addReviewTitle = findViewById(R.id.addReviewTitle);
        addReviewDescription = findViewById(R.id.addReviewDescription);
        addReviewSubmitButton = findViewById(R.id.addReviewSubmitButton);

        addReviewSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReview();
            }
        });


    }


    public void addReview(){
        String authorname = addReviewAuthorName.getText().toString().trim();
        String reviewTitle = addReviewTitle.getText().toString().trim();
        String description = addReviewDescription.getText().toString().trim();
        float rating = addReviewRating.getRating();
        Review rev = new Review(authorname,description,rating,reviewTitle);
        databaseReference = FirebaseDatabase.getInstance().getReference("establishment").child(intent.getStringExtra("establishmentId")).child("review").push();
        databaseReference.setValue(rev);
        finish();
    }
}
