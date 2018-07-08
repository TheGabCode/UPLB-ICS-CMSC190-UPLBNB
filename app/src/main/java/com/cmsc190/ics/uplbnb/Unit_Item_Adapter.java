package com.cmsc190.ics.uplbnb;

import android.app.ActionBar;
import android.content.Context;
import android.provider.Contacts;
import android.support.v7.widget.RecyclerView;
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

import java.util.List;

/**
 * Created by Dell on 16 Feb 2018.
 */

public class Unit_Item_Adapter extends RecyclerView.Adapter<Unit_Item_Adapter.ViewHolder> {

    public Unit_Item_Adapter(List<Unit_Item> unit_items, Context context, String number,int establishmentType,String establishmentId) {
        this.unit_items = unit_items;
        this.context = context;
        this.establishmentContactNumber1 = number;
        this.establishmentType = establishmentType;
        this.establishmentId = establishmentId;
    }
    List<Unit_Item> unit_items;
    Context context;
    String establishmentContactNumber1;
    int establishmentType;
    String establishmentContactNumber2;
    String establishmentId;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.unit_item_2,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        final Unit_Item unit = unit_items.get(position);
        if(unit.pictures != null){
            String firstKey = (String)unit.pictures.keySet().toArray()[0];
            String url = unit.pictures.get(firstKey);
            StorageReference thumbnailRef = storageReference.child("units/"+unit.getId()+"/"+url);
            GlideApp.with(context)
                    .load(thumbnailRef)
                    .placeholder(R.drawable.logo2)
                    .centerCrop()
                    .override(300)
                    .into(holder.unitImage);
        }

        holder.unitIdentifier.setText(unit.getUnitIdentifier());
        holder.unitRate.setText(unit.getRate() + " PHP");
        if(unit.getStatus() == 1){
            holder.unitStatus.setText("Open");
        }
        else{
            holder.unitStatus.setText("Closed");
        }
        if(unit.getSlotsAvailable() == -1){
            holder.unitAvailableSlot.setVisibility(View.INVISIBLE);
        }else{
            if(unit.getSlotsAvailable() != 1){
                holder.unitAvailableSlot.setText(unit.getSlotsAvailable() + " slots available");
            }
            else{
                holder.unitAvailableSlot.setText(unit.getSlotsAvailable() + " slot available");
            }

        }
        /*if(e.getEstablishmentType() == 1){
            e = (Apartment_Item)e;
        }
        else{
            e = (Dormitory_Item)e;
        }*/




        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                Context context = view.getContext();
                i = new Intent(context,ApartmentUnitDrilldown.class);
                i.putExtra("unit",unit);
                i.putExtra("establishmentType",establishmentType);
                i.putExtra("establishmentContactNumber",establishmentContactNumber1);
                i.putExtra("establishmentId",establishmentId);
                context.startActivity(i);//                Toast.makeText(context, "Clicked " + establishment.getEstablishmentName(), Toast.LENGTH_SHORT).show();
//               Intent i = new Intent(context,Establishment_Drilldown.class);
//                i.putExtra("establishment",establishment);
//                context.startActivity(i);

            }
        });
    }

    @Override
    public int getItemCount() {
        return unit_items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView unitIdentifier;
        public TextView unitStatus;
        public TextView unitRate;
        public TextView unitAvailableSlot;
        ImageView unitImage;
        public ViewHolder(View itemView) {
            super(itemView);
            unitIdentifier = (TextView)itemView.findViewById(R.id.unitIdentifier);
            unitStatus = (TextView)itemView.findViewById(R.id.unitStatus);
            unitRate = (TextView)itemView.findViewById(R.id.unitRate);
            unitAvailableSlot = (TextView)itemView.findViewById(R.id.unitSlotsAvailable);
            unitImage = itemView.findViewById(R.id.unitImage);
        }
    }
}
