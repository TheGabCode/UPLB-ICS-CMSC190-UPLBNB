package com.cmsc190.ics.uplbnb;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Dell on 20 Apr 2018.
 */

public class AdminLogs extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Log> log_items;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refLogs;
    ImageView adminProfilePicture;
    TextView adminName;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference userReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    User user;
    Uri filePath;
    final int PICK_IMAGE_REQUEST = 71;
    public AdminLogs() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_profile,container,false);

        adminProfilePicture = view.findViewById(R.id.adminProfilePicture);
        adminProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        adminName = view.findViewById(R.id.adminName);

        initUser();
        log_items = new ArrayList<Log>();
        refLogs = database.getReference("logs");
        refLogs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                log_items.clear();
                for (DataSnapshot logSnapshot : dataSnapshot.getChildren()) {
                    Log log = logSnapshot.getValue(Log.class);
                    log_items.add(log);
                    Log_Item_Adapter adapter = new Log_Item_Adapter(log_items,getActivity());
                    recyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewAdminLogs);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;

    }


    public void initUser(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        databaseReference.child("user").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                adminName.setText(user.getFullname());
                StorageReference userIconRef = storageReference.child("user/admin/"+user.getId());
                GlideApp.with(getActivity())
                        .load(userIconRef)
                        .placeholder(R.drawable.user_male_gray_48px)
                        .apply(RequestOptions.circleCropTransform())
                        .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                        .into(adminProfilePicture);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            //uriList.add(filePath);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                GlideApp.with(this)
                        .load(bitmap)
                        .apply(RequestOptions.circleCropTransform())
                        .into(adminProfilePicture);

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
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref;
            Bitmap bmp = null;

            try{
                bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
            }catch (IOException e){
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();
            ref = storageReference.child("user/"+"admin/"+user.getId());

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
                            Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Admin Home");
    }
}
