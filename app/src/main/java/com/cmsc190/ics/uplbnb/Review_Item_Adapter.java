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

public class Review_Item_Adapter extends RecyclerView.Adapter<Review_Item_Adapter.ViewHolder> {

    public Review_Item_Adapter(List<Review> review_items, Context context) {
        this.review_items = review_items;
        this.context = context;
    }

    List<Review> review_items;
    Context context;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Review review = review_items.get(position);

        holder.reviewAuthorName.setText(review.getAuthorName());
        holder.reviewRating.setRating(review.getRating());
        holder.reviewComment.setText(review.getComment());
        /*if(e.getEstablishmentType() == 1){
            e = (Apartment_Item)e;
        }
        else{
            e = (Dormitory_Item)e;
        }*/




        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context context = view.getContext();
//                Toast.makeText(context, "Clicked " + establishment.getEstablishmentName(), Toast.LENGTH_SHORT).show();
//               Intent i = new Intent(context,Establishment_Drilldown.class);
//                i.putExtra("establishment",establishment);
//                context.startActivity(i);

            }
        });
    }

    @Override
    public int getItemCount() {
        return review_items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView reviewAuthorName;
        public RatingBar reviewRating;
        public TextView reviewComment;


        public ViewHolder(View itemView) {
            super(itemView);
            reviewAuthorName = itemView.findViewById(R.id.reviewAuthorName);
            reviewRating = itemView.findViewById(R.id.reviewRating);
            reviewComment = itemView.findViewById(R.id.reviewComment);

        }
    }
}
