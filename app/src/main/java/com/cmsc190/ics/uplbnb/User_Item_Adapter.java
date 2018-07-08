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

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Created by Dell on 16 Feb 2018.
 */

public class User_Item_Adapter extends RecyclerView.Adapter<User_Item_Adapter.ViewHolder> {

    public User_Item_Adapter(List<User> user_items, Context context) {
        this.user_items = user_items;
        this.context = context;
    }
    List<User> user_items;
    Context context;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User user = user_items.get(position);
        holder.userFullName.setText(user.getFullname());
        holder.userEmail.setText(user.getEmail());

        if(user.getUser_type().equals("renter")){
            holder.userType.setText("Renter");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent i = new Intent(context,User_Drilldown.class);
                    i.putExtra("userId",user.getId());
                    context.startActivity(i);
                }
            });
        }
        else if(user.getUser_type().equals("owner")){
            holder.userType.setText("Owner");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent i = new Intent(context,User_Drilldown.class);
                    i.putExtra("userId",user.getId());
                    context.startActivity(i);
                }
            });
            StorageReference profileReference = storageReference.child("user/owner/"+user.getId());
            GlideApp.with(context)
                    .load(profileReference)
                    .placeholder(R.drawable.user_male_gray_48px)
                    .apply(RequestOptions.circleCropTransform())
                    .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                    .into(holder.userPicture);
        }




    }

    @Override
    public int getItemCount() {
        return user_items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView userFullName;
        public TextView userEmail;
        public TextView userType;
        ImageView userPicture;

        public ViewHolder(View itemView) {
            super(itemView);
            userFullName = itemView.findViewById(R.id.userFullname);
            userEmail = itemView.findViewById(R.id.userEmail);
            userType = itemView.findViewById(R.id.userType);
            userPicture = itemView.findViewById(R.id.userPicture);
        }
    }
}
