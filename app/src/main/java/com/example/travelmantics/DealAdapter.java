package com.example.travelmantics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.travelmantics.InsertActivity.GS_TRAVELMANTICS_STORAGE_URI;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealHolder> {
    Context mContext;
    ArrayList<TravelDeal> mTravelDeals ;
    FirebaseManager mManager;

    public DealAdapter(Context contenxt, ArrayList<TravelDeal> deals){
        mContext = contenxt;
        mTravelDeals = new ArrayList<>();
    }

    @NonNull
    @Override
    public DealHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.view_deals, parent, false);
        return new DealHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealHolder holder, int position) {
            TravelDeal deal = mTravelDeals.get(position);
            holder.txtTitle.setText(deal.getTitle());
            holder.txtDescript.setText(deal.getDescription());
            holder.txtPrice.setText(deal.getPrice());
             Uri path = Uri.parse(deal.getUrl());
            InsertActivity.downloadImage(path.getLastPathSegment(), holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        if(mTravelDeals != null)
            return mTravelDeals.size();
        else return 0;
    }

    public void changeList(TravelDeal deals){
        mTravelDeals.add(deals);
        notifyDataSetChanged();
    }


    public class DealHolder extends RecyclerView.ViewHolder{
        ImageView thumbnail;
        TextView txtTitle, txtDescript, txtPrice;

        public DealHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.imgThumbNail);
            txtTitle = itemView.findViewById(R.id.txtDisplayTtitle);
            txtDescript = itemView.findViewById(R.id.txtDisplayDescription);
            txtPrice = itemView.findViewById(R.id.txtDisplayPrice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Log.d("ADAPTER", "Position: " + position + " was clicked");
                    TravelDeal deal = mTravelDeals.get(position);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if((user.getEmail()).equals("tebohothg@gmail.com")) {
                        Intent intent = new Intent(mContext, InsertActivity.class);
                        intent.putExtra("TravelDeal", deal);
                        mContext.startActivity(intent);
                    }
                }
            });
        }

    }
}
