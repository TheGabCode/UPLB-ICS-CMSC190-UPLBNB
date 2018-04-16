package com.cmsc190.ics.uplbnb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Dell on 15 Apr 2018.
 */

public class Photo_List  extends Fragment {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference imageRef;
    DatabaseReference photoRef;
    Establishment_Item e;
    LinearLayout photoList;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    Button addPhotoBtn;
    Button savePhotoBtn;
    private final int PICK_IMAGE_REQUEST = 71;
    Uri filePath;
    List<Uri> uris = new ArrayList<Uri>();
    String establishmentId;
    LinearLayout fetchedImageContainer;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    List<String> photo_items;
    public Photo_List(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_list,container,false);
        recyclerView = (RecyclerView)view.findViewById(R.id.fetchedImageRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        photo_items = new ArrayList<String>();
        establishmentId = getArguments().getString("establishmentId");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        database.getReference("establishment").child(establishmentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                photo_items.clear();
                e = dataSnapshot.getValue(Establishment_Item.class);
                getPhotos(e);
                adapter = new Photo_Item_Adapter(photo_items,getActivity(),e.getId());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        photoList = (LinearLayout)view.findViewById(R.id.photos_container);
        //fetchedImageContainer = (LinearLayout)view.findViewById(R.id.fetchedImagesContainer);
        addPhotoBtn = (Button)view.findViewById(R.id.addPhotoBtn);
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        savePhotoBtn = (Button)view.findViewById(R.id.savePhotoBtn);
        savePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImages();

            }
        });
        String establishmentId = getArguments().getString("establishmentId");
        adapter = new Photo_Item_Adapter(photo_items,getActivity(),establishmentId);
        recyclerView.setAdapter(adapter);
        return view;
    }


    public  void deletePhoto(View v){
        photoList.removeView((View)v.getParent());
        int remove = photoList.indexOfChild((View)v.getParent());
        uris.remove(remove);
    }

    public void saveImages(){
        Toast.makeText(getContext(),uris.size()+" " +e.getEstablishmentName(),Toast.LENGTH_SHORT).show();
        if(uris.size() > 0){

            StorageReference ref;
            ByteArrayOutputStream baos;
            byte[] data1 = null;
            String uploadId;

            for(int i = 0; i < uris.size(); i++){
                baos = new ByteArrayOutputStream();
                try{
                    Bitmap bitmap = decodeUri(getContext(),uris.get(i),400);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    data1 = baos.toByteArray();
                }catch (IOException e){
                    e.printStackTrace();
                }


                final int cnt = i;
                ref = storageReference.child("establishments/"+establishmentId+"/"+uris.get(i).getLastPathSegment());
                ref.putBytes(data1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(),"added "+ cnt + " " + uris.get(cnt).getLastPathSegment(),Toast.LENGTH_SHORT).show();

                    }
                });

            }


        }
        saveUrisToDatabase();

    }

    public void saveUrisToDatabase(){


        for(int i = 0; i < uris.size(); i++){
            imageRef = FirebaseDatabase.getInstance().getReference("establishment").child(establishmentId).child("picture").push();
            imageRef.setValue(uris.get(i).getLastPathSegment());
        }
    }
    public void selectImage(){
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
            LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE); //creates upload previews
            View uploadedPictureView = inflater.inflate(R.layout.preview_uploaded_image,null);
            ImageView retrievedImage = (ImageView)uploadedPictureView.findViewById(R.id.establishmentAddPhoto);
            ImageButton deletePhotoBtn = (ImageButton)uploadedPictureView.findViewById(R.id.deletePhoto);
            deletePhotoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletePhoto(v);
                }
            });
            filePath = data.getData();
            //uriList.add(filePath);
            try {
                Bitmap bitmap = decodeUri(getContext(),filePath,200); //compression
/*                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data1 = baos.toByteArray();*/
                /*retrievedImage.setImageBitmap(bitmap);*/
                GlideApp.with(getContext()) //load compressed image to image views for preview
                        .load(bitmap)
                        .centerCrop()
                        .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                        .into(retrievedImage);

                photoList.addView(uploadedPictureView);
                uris.add(filePath); //add compressed bitmap to array list for firebase storage upload
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap decodeUri(Context c, Uri uri, final int requiredSize)
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth
                , height_tmp = o.outHeight;
        int scale = 1;

        while(true) {
            if(width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
    }

    public void getPhotos(Establishment_Item e){
        if(e.picture != null){
            Toast.makeText(getContext(),"Not null",Toast.LENGTH_SHORT).show();
            Iterator entries = e.getPictures().entrySet().iterator();
            while (entries.hasNext()){
                HashMap.Entry entry = (HashMap.Entry) entries.next();
                String url = (String)entry.getValue();
                photo_items.add(url);

            }
        }
        else{
            Toast.makeText(getContext(),"null",Toast.LENGTH_SHORT).show();
        }
    }


/*    public void getPhotos(Establishment_Item e){
        if(e.picture != null){
            Iterator entries = e.getPictures().entrySet().iterator();
            while (entries.hasNext()){
                HashMap.Entry entry = (HashMap.Entry) entries.next();
                String key = (String)entry.getKey();
                String url = (String)entry.getValue();
                String establishmentId = getArguments().getString("establishmentId");
                LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View uploadedPictureView = inflater.inflate(R.layout.preview_uploaded_image,null);

                ImageView retrievedImage = (ImageView)uploadedPictureView.findViewById(R.id.establishmentAddPhoto);
                ImageButton deletePhotoBtn = (ImageButton)uploadedPictureView.findViewById(R.id.deletePhoto);
                deletePhotoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletePhoto(v);
                    }
                });
                StorageReference previewRef = storageReference.child("establishments/"+establishmentId+"/"+url);
                GlideApp.with(getActivity())
                        .load(previewRef)
                        .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                        .into(retrievedImage);
                fetchedImageContainer.addView(uploadedPictureView,fetchedImageContainer.getChildCount());

            }
        }

    }*/

}
