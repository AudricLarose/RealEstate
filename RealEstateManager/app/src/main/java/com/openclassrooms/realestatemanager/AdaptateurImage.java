package com.openclassrooms.realestatemanager;

import androidx.recyclerview.widget.RecyclerView;


import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;


import com.openclassrooms.realestatemanager.modele.RealEstate;
import com.squareup.picasso.Picasso;

import java.util.List;


public class AdaptateurImage extends RecyclerView.Adapter<AdaptateurImage.LeHolder> {
    List<String> liste;


    public AdaptateurImage(List<String> liste) {
        this.liste = liste;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.roow_image, parent, false);
        return new LeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeHolder holder, int position) {
        final String estateImage = liste.get(position);
//            holder.imageRealEstate.setImageURI(Uri.parse(estateImage));
        Picasso.get().load(Uri.parse(estateImage)).into(holder.imageRealEstate);

    }

    @Override
    public int getItemCount() {
        if (liste != null) {
            return liste.size();
        } else {
            return 0;
        }
    }

    public static class LeHolder extends RecyclerView.ViewHolder {
        private ImageView imageRealEstate;

        public LeHolder(@NonNull View itemView) {
            super(itemView);
            imageRealEstate = itemView.findViewById(R.id.imageRealEstate);
        }
    }


}
