package com.openclassrooms.realestatemanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.realestatemanager.dummy.ItemDetailActivity;
import com.openclassrooms.realestatemanager.dummy.DetailFragment;
import com.openclassrooms.realestatemanager.dummy.MainActivity;
import com.openclassrooms.realestatemanager.modele.RealEstate;
import com.squareup.picasso.Picasso;

import java.util.List;


public class Adaptateur extends RecyclerView.Adapter<Adaptateur.LeHolder> {
    public List<RealEstate> liste;
    public Boolean mTwoPane;
    public MainActivity mParentActivity;
    public RealEstate estate;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private void goToItemDetailsActivity(View view, RealEstate estate1) {
        Intent intent = new Intent(view.getContext(), ItemDetailActivity.class);
        //                    intent.putExtra(DetailFragment.ARG_ITEM_ID, item.id);

        Bundle bundle = new Bundle();
        bundle.putSerializable("RealEstate", estate1);
        intent.putExtras(bundle);
        view.getContext().startActivity(intent);
    }


    public Adaptateur(List<RealEstate> liste, Boolean mTwoPane, MainActivity mParentActivity) {
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
    public void onBindViewHolder(@NonNull final LeHolder holder, final int position) {
        estate = liste.get(position);
        holder.type.setText(estate.getType());
        if (Boolean.valueOf(estate.getInEuro())) {
            holder.prix.setText(Utils.getEuroFormat(Integer.parseInt(estate.getPrix())));
        } else {
            holder.prix.setText(Utils.getDollarFormat(Integer.parseInt(estate.getPrix())));

        }
        holder.ville.setText(estate.getTown());
        if (estate.getPhotosReal()!=null && estate.getPhotosReal().size()>0) {
            if (estate.getUrlFireBase()!=null && estate.getUrlFireBase().size()>0){
                Picasso.get().load(estate.getUrlFireBase().get(0)).into(holder.imageRealestate);
            } else {
                Picasso.get().load(Uri.parse(estate.getPhotosReal().get(0))).into(holder.imageRealestate);
            }
        }
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                holder.relativeLayout.setBackgroundColor(R.color.colorAccent);
                Toast.makeText(mParentActivity, ""+liste.get(position), Toast.LENGTH_SHORT).show();
//                DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
                if (mParentActivity != null) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
//                    arguments.putSerializable("RealEstate", estate);
                        arguments.putSerializable("RealEstate", liste.get(position));
                       int dfdf= liste.get(position).getId();
                        arguments.putString(DetailFragment.ARG_ITEM_ID, String.valueOf(liste.get(position).getId()));
                        DetailFragment fragment = new DetailFragment();
                        fragment.setArguments(arguments);
                        mParentActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        goToItemDetailsActivity(v,liste.get(position));
                    }
                }else {
                    goToItemDetailsActivity(v,liste.get(position));
                }
            }
        });
        checkLiestener(holder);
//        verifyIfitisTemp(holder);
    }
//
//    @SuppressLint("ResourceAsColor")
//    private void verifyIfitisTemp(LeHolder holder) {
//        if (estate.getTemp()!=null && estate.getTemp().contains("true")) {
//            holder.relativeLayout.setBackgroundColor(R.color.colorSecondary);
//        }
//
//    }

    private void checkLiestener(@NonNull LeHolder holder) {
        if (estate.getIschecked()!=null && estate.getSelled()!=null) {
            if (!Boolean.valueOf(estate.getIschecked()) && estate.getSelled().equals("date")) {
                holder.selled.setText(" Disponible !");
            } else {
                holder.selled.setText(" Vendu !");

            }
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
