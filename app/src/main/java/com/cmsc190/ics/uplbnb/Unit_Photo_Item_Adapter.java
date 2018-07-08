package com.cmsc190.ics.uplbnb;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Created by Dell on 23 Apr 2018.
 */

public class Unit_Photo_Item_Adapter extends RecyclerView.Adapter<Unit_Photo_Item_Adapter.ViewHolder> {
    Context context;
    List<String> urls;
    String unitId;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference() ;
    public Unit_Photo_Item_Adapter(List<String> urls, Context context,String unitId){
        this.context = context;
        this.urls = urls;
        this.unitId = unitId;
    }


    @Override
    public Unit_Photo_Item_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.uploaded_image,parent,false);
        return new Unit_Photo_Item_Adapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(Unit_Photo_Item_Adapter.ViewHolder holder, final int position) {
        final String photo_url = urls.get(position);
        StorageReference prevImage = storageReference.child("units/"+unitId+"/"+photo_url);
        holder.deletePhotoBtn.setVisibility(View.GONE);
        GlideApp.with(context)
                .load(prevImage)
                .centerCrop()
/*                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))*/
                .into(holder.establishmentImage);







    }


    @Override
    public int getItemCount() {return urls.size();}

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView establishmentImage;
        public ImageButton deletePhotoBtn;
        public ViewHolder(View itemView) {
            super(itemView);
            deletePhotoBtn = itemView.findViewById(R.id.deletePhoto);
            establishmentImage = (ImageView)itemView.findViewById(R.id.fetchedImage);

        }
    }
}
