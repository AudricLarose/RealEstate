package com.openclassrooms.realestatemanager.dummy;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.openclassrooms.realestatemanager.ActivityDetails;
import com.openclassrooms.realestatemanager.AdaptateurImage;
import com.openclassrooms.realestatemanager.AddInformationActivity;
import com.openclassrooms.realestatemanager.Api.DI;
import com.openclassrooms.realestatemanager.Api.ExtendedServiceEstate;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.Utils;
import com.openclassrooms.realestatemanager.modele.MediaImage;
import com.openclassrooms.realestatemanager.modele.RealEstate;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    private DummyContent.DummyItem mItem;
    private static RealEstate estateGrabbed;
    private TextView adresse, ville, region, pays, surfaceChiffre, priceChiffre, roomChiffre, typeCHiffre, bathroomchiffre, nearbyChiffre, chamber;
    private AdaptateurImage adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ExtendedServiceEstate serviceEstate = DI.getService();
    private List<MediaImage> listRealEstateImage = serviceEstate.getRealEstateImageList();
    private List<RealEstate> listRealEstate = serviceEstate.getRealEstateList();
    private boolean amIInEuro = true;
    private LatLng latLngRealestate;
    private  Bundle mapViewBundle=null;
    private ImageView mImageView;
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
//            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
//            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//            if (appBarLayout != null) {
//                appBarLayout.setTitle(mItem.content);
//            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        // Show the dummy content as text in a TextView.
        deployTextView(rootView);
        realStateIfExist(rootView);
        return rootView;
    }
    private void deployCarteImageView(String url, View container) {
        ImageView imageCarte = container.findViewById(R.id.mapLiteView);
        Picasso.get().load(url).into(imageCarte);
    }




    private void deployRecyclerViewDetails(View container) {
        adapter = new AdaptateurImage(estateGrabbed.getPhotosReal());
        recyclerView = container.findViewById(R.id.RecycleDetails);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL , false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void deployTextView(View container) {
        initiateTextView(container);
    }

    private void initiateTextView(View container) {
        adresse = container.findViewById(R.id.adresseDetails);
        ville = container.findViewById(R.id.villeDetails);
        region = container.findViewById(R.id.regionVille);
        pays = container.findViewById(R.id.paysDetails);
        surfaceChiffre = container.findViewById(R.id.SurfacechiffresDetails);
        priceChiffre = container.findViewById(R.id.PriceChiffresDetails);
        roomChiffre = container.findViewById(R.id.RoomechiffresDetails);
        typeCHiffre = container.findViewById(R.id.TypeChiffresDetails);
        bathroomchiffre = container.findViewById(R.id.BathroomchiffresDetails);
        nearbyChiffre = container.findViewById(R.id.NearbyChiffresDetails);
        chamber = container.findViewById(R.id.RoomschiffresDetails);
    }

    private void realStateIfExist(View container) {
        estateGrabbed = grabEstatFromMainActivity();
        if (estateGrabbed != null) {
            deployRecyclerViewDetails(container);
            shareInformationsDetails(container);
            findAddress(container);
        }
    }

    private RealEstate grabEstatFromMainActivity() {
        RealEstate estate = null;
        Intent intent = getActivity().getIntent();
        Bundle extra = intent.getExtras();
        if (extra != null) {
            estate = (RealEstate) extra.getSerializable("RealEstate");
        }
        return estate;
    }

    private void shareInformationsDetails(View container) {
        surfaceChiffre.setText(estateGrabbed.getSurface());
        priceChiffre.setText(Utils.getEuroFormat(Integer.valueOf(estateGrabbed.getPrix())));
        roomChiffre.setText(estateGrabbed.getPiece());
        typeCHiffre.setText(estateGrabbed.getType());
        bathroomchiffre.setText(estateGrabbed.getSdb());
        nearbyChiffre.setText(estateGrabbed.getNearby().toString());
        chamber.setText(estateGrabbed.getChambre());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.retour_arriere_modifier, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.convert:
                knowIfConvertToEuroOrDOllar();
            case R.id.modify:
                modifyThisEstate();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void findAddress(View container){
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addressList= geocoder.getFromLocationName(estateGrabbed.getAdresse().toString(),1);
            if (addressList!=null && addressList.size()>0){
                adresse.setText(addressList.get(0).getThoroughfare());
                ville.setText(estateGrabbed.getTown());
                pays.setText(addressList.get(0).getCountryName());
                latLngRealestate= new LatLng(addressList.get(0).getLatitude(),addressList.get(0).getLongitude());
                String url=Utils.linkBuilder(latLngRealestate);
                deployCarteImageView(url,container);

            }
        } catch (IOException e) {
            adresse.setText(estateGrabbed.getAdresse());
            ville.setText(estateGrabbed.getTown());
            pays.setText(estateGrabbed.getTown()); }
    }


    private void modifyThisEstate() {
        RealEstate estate=grabEstatFromMainActivity();
        if (estate!=null) {
            Intent intent = new Intent(getContext(), AddInformationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("RealEstate", estate);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void knowIfConvertToEuroOrDOllar() {
        if (amIInEuro) {
            convertEuroEnDollar();
            amIInEuro = false;
        } else {
            convertDollarEnEuro();
            amIInEuro = true;
        }
    }

    private void convertDollarEnEuro() {
        int converted = Utils.convertDollarToEuro(Integer.valueOf(estateGrabbed.getPrix()));
        priceChiffre.setText(converted + " Euros");
    }

    private void convertEuroEnDollar() {
        int converted = Utils.convertEuroToDollar(Integer.valueOf(estateGrabbed.getPrix()));
        priceChiffre.setText(converted + " Dollars");
    }
}
