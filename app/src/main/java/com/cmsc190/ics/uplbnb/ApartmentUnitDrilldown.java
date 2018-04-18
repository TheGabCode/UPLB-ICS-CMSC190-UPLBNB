package com.cmsc190.ics.uplbnb;

import android.app.ActionBar;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toolbar;

import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static android.provider.LiveFolders.INTENT;
import static com.cmsc190.ics.uplbnb.Establishment_Drilldown.e;


public class ApartmentUnitDrilldown extends AppCompatActivity {
    private DatabaseReference databaseReference;
    TextView unitDrilldownRate;
    TextView unitDrilldownStatus;
    TextView unitDrilldownCondition;
    TextView unitDrilldownCapacity;
    TextView unitDrilldownSlotsAvailable;
    TextView unitIdentifier;
    TextView furnitureItems;
    ImageButton editButton;
    Button callButton;
    LinearLayout ll;
    LinearLayout apartmentConditionLayout;
    LinearLayout dormCapacityLayout;
    LinearLayout dormSlotsLayout;
    LinearLayout photosContainer;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    ArrayList<String> photo_items;
    Intent i;
    ActionBar toolbar;
    Unit_Item unit;
    DatabaseReference ref;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userRef;
    FirebaseUser firebaseUser;
    User user;

    public void initUser(){
        userRef = firebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        Log.d("User id","User id " + userId);
        userRef.child("user").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if(firebaseUser == null){
                    editButton.setVisibility(View.GONE);
                }else{
                    if(user != null){
                        if(user.getUser_type().equals("renter") || !user.getId().equals(e.getOwner_id()) ){
                            editButton.setVisibility(View.GONE);
                        }
                        else if(user.getId().equals(e.getOwner_id())){
                            editButton.setVisibility(View.VISIBLE);
                        }
                    }
                    else{
                        Log.d("User id",firebaseUser.getUid());
                    }

                }
                Log.d("User id","XXX" + user.getFullname());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }



    public void getUnitData(String establishmentId, String unitId){
        ref = FirebaseDatabase.getInstance().getReference("establishment").child(establishmentId).child("unit").child(unitId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                unit = dataSnapshot.getValue(Unit_Item.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_drilldown2);
        i = getIntent();
        final String eId = i.getStringExtra("establishmentId");
        String uId = ((Unit_Item)i.getSerializableExtra("unit")).getId();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        unitIdentifier = findViewById(R.id.apartmentUnitDrilldownIdentifier);
        ll = findViewById(R.id.furniture);
        unitDrilldownCondition = findViewById(R.id.apartmentUnitDrilldownCondition);
        unitDrilldownCapacity = findViewById(R.id.apartmentUnitDrilldownCapacity);
        unitDrilldownSlotsAvailable = findViewById(R.id.apartmentUnitDrilldownSlotsAvailable);
        furnitureItems = findViewById(R.id.furnitureItems);
        unitDrilldownRate = findViewById(R.id.apartmentUnitDrilldownRate);
        apartmentConditionLayout = findViewById(R.id.apartmentConditionLayout);
        dormCapacityLayout = findViewById(R.id.dormCapacityLayout);
        dormSlotsLayout = findViewById(R.id.dormSlotsLayout);
        unitDrilldownStatus = findViewById(R.id.apartmentUnitDrilldownStatus);
        callButton = findViewById(R.id.callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call owner
                callOwner();
            }
        });
        recyclerView = findViewById(R.id.photoRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        photo_items = new ArrayList<String>();
        editButton = (ImageButton)findViewById(R.id.unitDrilldownEditBtn);
        editButton.setVisibility(View.GONE);
        initUser();
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editUnit();
            }
        });
  /*      photosContainer = findViewById(R.id.photosContainer);*/
        ref = FirebaseDatabase.getInstance().getReference("establishment").child(eId).child("unit").child(uId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                photo_items.clear();
                unit = dataSnapshot.getValue(Unit_Item.class);
                if(unit == null){
                    finish();
                    return;
                }
                unitIdentifier.setText(unit.getUnitIdentifier());
                if(unit.getSlotsAvailable() == -1){
                    ll.setVisibility(View.GONE);
                    dormCapacityLayout.setVisibility(View.GONE);
                    dormSlotsLayout.setVisibility(View.GONE);
                    if(unit.getCondition() == 0){
                        unitDrilldownCondition.setText("Unfurnished");
                    }else{
                        unitDrilldownCondition.setText("Furnished");
                    }
                }else{ //if dormitory
                    apartmentConditionLayout.setVisibility(View.GONE);
                    unitDrilldownCapacity.setText(unit.getCapacity() + " persons");
                    unitDrilldownSlotsAvailable.setText(unit.getSlotsAvailable() + " slots available out of " + unit.getCapacity());
                    furnitureItems.setText(stringifyFurniture(unit.getFurniture()));
                }

                unitDrilldownRate.setText(unit.getRate() + " PHP");
                if(unit.isRatePerHead() == true){
                    unitDrilldownRate.append(" per head");
                }

                if(unit.getStatus() == 1){
                    unitDrilldownStatus.setText("Open");
                }
                else{
                    unitDrilldownStatus.setText("Closed");
                }
                initializeUploadedPictures(unit);
                adapter = new Photo_Item_Adapter(photo_items,getApplicationContext(),eId,unit.getId(),false);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void editUnit(){
        Intent intent = new Intent(getApplicationContext(),EditUnit.class);
        intent.putExtra("unit",unit);
        intent.putExtra("establishmentType",i.getIntExtra("establishmentType",1));
        intent.putExtra("establishmentId",i.getStringExtra("establishmentId"));
        startActivity(intent);
    }

    public void callOwner(){
        String contactNumber = (String)i.getStringExtra("establishmentContactNumber");
        String uri = "tel:" + contactNumber;
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
        startActivity(callIntent);
    }
    public String stringifyFurniture(HashMap<String,Integer> furniture){
        String furnitureItems = "";
        if(furniture != null){
            Iterator entries = furniture.entrySet().iterator();
            while (entries.hasNext()){
                HashMap.Entry entry = (HashMap.Entry) entries.next();
                String key = (String)entry.getKey();
                Integer value = (Integer)entry.getValue();
                furnitureItems+=value + " x " + key + "\n";
            }
            furnitureItems = furnitureItems.substring(0,furnitureItems.length() - 1);
        }

        return furnitureItems;
    }


    public void initializeUploadedPictures(Unit_Item unit){
        HashMap<String,String> pictures = (HashMap<String,String>)unit.getPictures();
        if(pictures != null){
            Iterator entries = pictures.entrySet().iterator();
            while(entries.hasNext()){
                HashMap.Entry entry = (HashMap.Entry) entries.next();
                String key = (String)entry.getKey();
                String value = (String)entry.getValue();
                photo_items.add(value);
            }
        }
    }

/*    public void loadIntoView(String lastPathSegment,String unitId){
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE); //creates upload previews
        View uploadedPictureView = inflater.inflate(R.layout.uploaded_image,null);
        ImageView retrievedImage = (ImageView)uploadedPictureView.findViewById(R.id.fetchedImage);

        StorageReference previewRef = storageReference.child("units/"+unitId+"/"+lastPathSegment);
        GlideApp.with(getApplication()) //load compressed image to image views for preview
                .load(previewRef)
                .centerCrop()
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .into(retrievedImage);

        photosContainer.addView(uploadedPictureView);

    }*/

}

