package com.openclassrooms.realestatemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.realestatemanager.dummy.DummyContent;
import com.openclassrooms.realestatemanager.dummy.ItemDetailActivity;
import com.openclassrooms.realestatemanager.dummy.ItemDetailFragment;
import com.openclassrooms.realestatemanager.dummy.ItemListActivity;
import com.openclassrooms.realestatemanager.modele.RealEstate;
import com.squareup.picasso.Picasso;

import java.util.List;


public class Adaptateur extends RecyclerView.Adapter<Adaptateur.LeHolder> {
    public List<RealEstate> liste;
    public Boolean mTwoPane;
    public ItemListActivity mParentActivity;
    public RealEstate estate;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
            if (mParentActivity != null) {
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.id);
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    goToItemDetailsActivity(view);
                }
            }else {
                goToItemDetailsActivity(view);
            }
        }
    };

    private void goToItemDetailsActivity(View view) {
        Intent intent = new Intent(view.getContext(), ItemDetailActivity.class);
        //                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.id);

        Bundle bundle = new Bundle();
        bundle.putSerializable("RealEstate", estate);
        intent.putExtras(bundle);
        view.getContext().startActivity(intent);
    }


    public Adaptateur(List<RealEstate> liste, Boolean mTwoPane, ItemListActivity mParentActivity) {
        this.liste = liste;
        this.mTwoPane = mTwoPane;
        this.mParentActivity = mParentActivity;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new LeHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull LeHolder holder, int position) {
        estate = liste.get(position);
        holder.type.setText(estate.getType());
        if (Boolean.valueOf(estate.getInEuro())) {
            holder.prix.setText(Utils.getEuroFormat(Integer.parseInt(estate.getPrix())));
        } else {
            holder.prix.setText(Utils.getDollarFormat(Integer.parseInt(estate.getPrix())));

        }
        holder.ville.setText(estate.getTown());
        if (estate.getPhotosReal()!=null && estate.getPhotosReal().size()>0) {
            Picasso.get().load(Uri.parse(estate.getPhotosReal().get(0))).into(holder.imageRealestate);
        }
        holder.relativeLayout.setOnClickListener(mOnClickListener);
        if (!Boolean.valueOf(estate.getIschecked())) {
            holder.selled.setText(" Vendu !");
        } else {
            holder.selled.setText(" Disponible !");

        }
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
        private TextView type, prix, ville, selled;
        private ImageView imageRealestate;
        private RelativeLayout relativeLayout;


        public LeHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.Row);
            imageRealestate = itemView.findViewById(R.id.imageRow);
            type = itemView.findViewById(R.id.TextTypeRow);
            prix = itemView.findViewById(R.id.TextPrixRow);
            ville = itemView.findViewById(R.id.TextTownRow);
            selled = itemView.findViewById(R.id.selledText);
        }
    }


}
