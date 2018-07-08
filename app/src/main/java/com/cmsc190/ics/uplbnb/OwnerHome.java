package com.cmsc190.ics.uplbnb;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OwnerHome extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceEstablishments;
    FirebaseUser firebaseUser;
    String firebaseUserId;
    TextView ownerName;
    ImageView ownerProfilePicture;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Establishment_Item> establishment_items;
    FloatingActionButton fab;
    ActionBar actionBar;
    User user;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    Uri filePath;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    final int PICK_IMAGE_REQUEST = 71;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getActionBar();
        setContentView(R.layout.activity_owner_home);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUserId = firebaseUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        ownerName = (TextView) findViewById(R.id.ownerName);
        ownerProfilePicture = findViewById(R.id.ownerProfilePicture);
        ownerProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        initUser();
        databaseReference = firebaseDatabase.getReference("user");
        fab = (FloatingActionButton) findViewById(R.id.addEstablishment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEstablishment();
            }
        });
        establishment_items = new ArrayList<>();
        databaseReferenceEstablishments = firebaseDatabase.getReference("establishment");
        databaseReferenceEstablishments.orderByChild("owner_id").equalTo(firebaseUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                establishment_items.clear();
                for(DataSnapshot establishmentSnapshot : dataSnapshot.getChildren()){
                    Establishment_Item e = establishmentSnapshot.getValue(Establishment_Item.class);
                    establishment_items.add(e);
                }
                Establishment_Item_Adapter e_adapter = new Establishment_Item_Adapter(establishment_items,getApplicationContext(),1);
                recyclerView.setAdapter(e_adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewOwnerHome);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference.child(firebaseUserId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
    //            ownerName.setText(user.getFirst_name() + " " + user.getLast_name());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.renter_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.Logout:
                logout();
                return true;
            case R.id.SUS:
                SUS();
                return true;
            case R.id.Manual:
                manual();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent i = new Intent(getApplicationContext(),Sign_in.class);
        startActivity(i);
    }
    public void SUS(){
        DatabaseReference susReference = database.getReference("links").child("system_usability_scale_link");

        susReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    public void manual(){
        DatabaseReference manualReference = database.getReference("links").child("uplbnb_owner_manual");
        manualReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addEstablishment(){
        Intent i = new Intent(getApplicationContext(),AddEstablishment.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed(){
        DialogFragment newFragment = new QuitApplicationDialogFragment();
        newFragment.show(getFragmentManager(), "Quit");
    }

    public void initUser(){

        databaseReference = firebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        databaseReference.child("user").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                ownerName.setText(user.getFullname());
                StorageReference userIconRef = storageReference.child("user/owner/"+user.getId());
                GlideApp.with(getApplication())
                        .load(userIconRef)
                        .placeholder(R.drawable.user_male_gray_48px)
                        .apply(RequestOptions.circleCropTransform())
                        .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                        .into(ownerProfilePicture);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void uploadImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            //uriList.add(filePath);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                GlideApp.with(this)
                        .load(bitmap)
                        .apply(RequestOptions.circleCropTransform())
                        .into(ownerProfilePicture);

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        saveImage();
    }

    public void saveImage(){
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref;
            Bitmap bmp = null;

            try{
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            }catch (IOException e){
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();
            ref = storageReference.child("user/"+"owner/"+user.getId());

            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //                        progressDialog.dismiss();
                            Toast.makeText(OwnerHome.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            //                          progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });

/*            for(int i = 0; i < uriList.size(); i++){

                }*/
        }



    }

}
