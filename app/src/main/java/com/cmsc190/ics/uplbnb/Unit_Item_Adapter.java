package com.cmsc190.ics.uplbnb;

import android.app.ActionBar;
import android.content.Context;
import android.provider.Contacts;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Dell on 16 Feb 2018.
 */

public class Unit_Item_Adapter extends RecyclerView.Adapter<Unit_Item_Adapter.ViewHolder> {

    public Unit_Item_Adapter(List<Unit_Item> unit_items, Context context, String number) {
        this.unit_items = unit_items;
        this.context = context;
        this.establishmentContactNumber1 = number;
    }

    List<Unit_Item> unit_items;
    Context context;
    String establishmentContactNumber1;
    String establishmentContactNumber2;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.unit_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Unit_Item unit = unit_items.get(position);

        holder.unitIdentifier.setText("Unit " + unit.getUnitIdentifier());
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
            holder.unitAvailableSlot.setText(unit.getSlotsAvailable() + " slots available");
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
                i.putExtra("establishmentContactNumber",establishmentContactNumber1);
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

        public ViewHolder(View itemView) {
            super(itemView);
            unitIdentifier = (TextView)itemView.findViewById(R.id.unitIdentifier);
            unitStatus = (TextView)itemView.findViewById(R.id.unitStatus);
            unitRate = (TextView)itemView.findViewById(R.id.unitRate);
            unitAvailableSlot = (TextView)itemView.findViewById(R.id.unitSlotsAvailable);

        }
    }
}
