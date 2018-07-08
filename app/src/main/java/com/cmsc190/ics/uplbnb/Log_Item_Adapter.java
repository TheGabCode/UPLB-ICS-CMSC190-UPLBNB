package com.cmsc190.ics.uplbnb;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

/**
 * Created by Dell on 21 Apr 2018.
 */

public class Log_Item_Adapter extends RecyclerView.Adapter<Log_Item_Adapter.ViewHolder> {
    public Log_Item_Adapter(List<Log> log_items, Context context) {
        this.log_items = log_items;
        this.context = context;
    }

    List<Log> log_items;
    Context context;

    @Override
    public Log_Item_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.log_item,parent,false);
        return new Log_Item_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(Log_Item_Adapter.ViewHolder holder, int position) {

        final Log log = log_items.get(position);
        holder.logMessage.setText(log.getLogMessage());
        holder.dateTime.setText(log.getTime());

    }



    @Override
    public int getItemCount() {
        return log_items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView logMessage;
        TextView dateTime;
        public ViewHolder(View itemView) {
            super(itemView);
            logMessage = (TextView)itemView.findViewById(R.id.logMessage);
            dateTime = (TextView)itemView.findViewById(R.id.timeStamp);
        }
    }
}
