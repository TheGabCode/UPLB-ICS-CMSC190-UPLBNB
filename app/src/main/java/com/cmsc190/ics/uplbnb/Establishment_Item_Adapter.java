package com.cmsc190.ics.uplbnb;

import android.app.ActionBar;
import android.content.Context;
import android.os.Parcelable;
import android.provider.Contacts;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Dell on 16 Feb 2018.
 */

public class Establishment_Item_Adapter extends RecyclerView.Adapter<Establishment_Item_Adapter.ViewHolder> {

    public Establishment_Item_Adapter(List<Establishment_Item> establishment_items, Context context, int use) {
        this.establishment_items = establishment_items;
        this.context = context;
        this.use = use;
    }

    List<Establishment_Item> establishment_items;
    Context context;
    StorageReference headerReference;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    int use; //1 - non admin view 0 - admin view owner profile

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(use == 1){
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.establishment_item_2,parent,false);
        }
        else{
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.establishment_item_simple,parent,false);
        }

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Establishment_Item establishment = establishment_items.get(position);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        if(use == 1){
            headerReference = storageReference.child("establishments/"+establishment.getId()+"/"+establishment.getHeaderUrl());
            GlideApp.with(context)
                    .load(headerReference)
                    .placeholder(R.drawable.logo2)
                    .centerCrop()
                    .override(300)
/*                    .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))*/
                    .into(holder.thumbnail);

            holder.textViewEstablishmentName.setText(establishment.getEstablishmentName());
            if(establishment.getEstablishmentType() == 1){
                holder.establishmentCategory.setText("Apartment");
            }
            else if(establishment.getEstablishmentType() == 0){
                holder.establishmentCategory.setText("Dormitory");
            }

            float rating = establishment.getRating();
            holder.ratingBarEstablishment.setRating(rating);
            holder.establishmentPrice.setText(establishment.getPrice()+ "PHP");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Context context = view.getContext();
                    Intent i = new Intent(context,Establishment_Drilldown.class);
                    i.putExtra("establishment",establishment);
                    context.startActivity(i);

                }
            });
        }
        else{
            holder.textViewEstablishmentName.setText(establishment.getEstablishmentName());
            if(establishment.getEstablishmentType() == 1){
                holder.establishmentCategory.setText("Apartment");
            }
            else{
                holder.establishmentCategory.setText("Dormitory");
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Context context = view.getContext();
                    Intent i = new Intent(context,Establishment_Drilldown.class);
                    i.putExtra("establishment",establishment);
                    context.startActivity(i);

                }
            });

        }

    }

    public float computeRating(Establishment_Item e){
        float totalSum = 0f;

        if(e.getReviews() != null){
            Log.d("Wow","wowee");
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
            Log.d("Wow","wow");
            return totalSum;
        }
    }


    @Override
    public int getItemCount() {
        return establishment_items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

         public TextView textViewEstablishmentName;
         public TextView establishmentCategory;
         public TextView establishmentPrice;
         public RatingBar ratingBarEstablishment;
         ImageView thumbnail;

         public ViewHolder(View itemView) {
            super(itemView);
            if(use == 1){
                thumbnail = (ImageView)itemView.findViewById(R.id.establishmentThumbnail);
                textViewEstablishmentName = (TextView) itemView.findViewById(R.id.establishment_item_name);
                establishmentCategory = (TextView) itemView.findViewById(R.id.establishment_item_category);
                ratingBarEstablishment = (RatingBar) itemView.findViewById(R.id.establishment_item_rating);
                establishmentPrice = (TextView) itemView.findViewById(R.id.establishment_item_price);
            }
            else{
                textViewEstablishmentName = (TextView)itemView.findViewById(R.id.establishmentName);
                establishmentCategory = (TextView)itemView.findViewById(R.id.establishmentType);
            }

         }
    }
}
