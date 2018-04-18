package com.cmsc190.ics.uplbnb;

import android.app.ActionBar;
import android.content.Context;
import android.provider.Contacts;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Created by Dell on 16 Feb 2018.
 */

public class Photo_Item_Adapter extends RecyclerView.Adapter<Photo_Item_Adapter.ViewHolder> {

    public Photo_Item_Adapter(List<String> photo_urls, Context context,String establishmentId, String unitId, boolean establishmentAdapter) {
        this.photo_urls = photo_urls;
        this.context = context;
        this.establishmentId = establishmentId;
        this.unitId = unitId;
        this.establishmentAdapter = establishmentAdapter;


    }
    List<String> photo_urls;
    Context context;
    String establishmentId;
    String unitId;
    boolean establishmentAdapter;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference() ;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.uploaded_image,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(Photo_Item_Adapter.ViewHolder holder, int position) {
        final String photo_url = photo_urls.get(position);

        StorageReference previewRef = null;
        if(this.establishmentAdapter == true){
            previewRef = storageReference.child("establishments/"+establishmentId+"/"+photo_url);
        }
        else{
            previewRef = storageReference.child("units/"+unitId+"/"+photo_url);

        }
        GlideApp.with(context)
                .load(previewRef)
                .centerCrop()
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .into(holder.establishmentImage);





    }

    @Override
    public int getItemCount() {return photo_urls.size();}

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView establishmentImage;
        public ViewHolder(View itemView) {
            super(itemView);

            establishmentImage = (ImageView)itemView.findViewById(R.id.fetchedImage);


        }
    }
}
