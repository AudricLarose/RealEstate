package com.openclassrooms.realestatemanager;

import androidx.recyclerview.widget.RecyclerView;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.openclassrooms.realestatemanager.modele.RealEstate;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class AdaptateurImage extends RecyclerView.Adapter<AdaptateurImage.LeHolder> {
    static List<String> liste;
    Context context;
    public static List<String> listDescription= new ArrayList<>();

    public AdaptateurImage(List<String> liste, Context context, List<String> listDescription) {
        this.liste = liste;
        notifyDataSetChanged();
        this.context= context;
        this.listDescription= listDescription;
    }

    @NonNull
    @Override
    public LeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.roow_image, parent, false);
        return new LeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeHolder holder, final int position) {
        final String estateImage = liste.get(position);
//            holder.imageRealEstate.setImageURI(Uri.parse(estateImage));
        Picasso.get().load(Uri.parse(estateImage)).into(holder.imageRealEstate);
        holder.imageRealEstate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                erAseImage(context, estateImage,position);
            }
        });
        holder.details_description.setText(listDescription.get(position));
    }


    private void erAseImage(Context context, String estateImage, int position) {
        AlertDialog alertDialog = eraseImageAlertDIalg(context,estateImage,position);
        alertDialog.show();
    }
    interface eraseEvent{
        void onFinish();
    }

    private AlertDialog eraseImageAlertDIalg(final Context context, final String estateImage, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Souhaitez vous effacer l'image").setTitle("Effacer Image").setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                liste.remove(estateImage);
                listDescription.remove(position);
                notifyDataSetChanged();


            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        return builder.create();
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
        private RelativeLayout relativeLayoutImage;
        private TextView details_description;
        public LeHolder(@NonNull View itemView) {
            super(itemView);
            imageRealEstate = itemView.findViewById(R.id.imageRealEstate);
            relativeLayoutImage= itemView.findViewById(R.id.RelativeLayoutRoowImage);
            details_description= itemView.findViewById(R.id.details_description);
        }
    }


}
