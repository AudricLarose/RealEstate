package com.openclassrooms.realestatemanager.dummy;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.openclassrooms.realestatemanager.AdaptateurImage;
import com.openclassrooms.realestatemanager.AddInformationActivity;
import com.openclassrooms.realestatemanager.Api.DI;
import com.openclassrooms.realestatemanager.Api.ExtendedServiceEstate;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.Utils;
import com.openclassrooms.realestatemanager.modele.MediaImage;
import com.openclassrooms.realestatemanager.modele.RealEstate;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    private static RealEstate estateGrabbed;
    private DummyContent.DummyItem mItem;
    private TextView adresse, ville, region, pays, surfaceChiffre, priceChiffre, roomChiffre, typeCHiffre, bathroomchiffre, nearbyChiffre, vendu, chamber, description, agent;
    private AdaptateurImage adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ExtendedServiceEstate serviceEstate = DI.getService();
    private List<RealEstate> listRealEstate = serviceEstate.getRealEstateList();
    private boolean amIInEuro = true;
    private LatLng latLngRealestate;
    private Bundle mapViewBundle = null;
    private ImageView mImageView;
    private RelativeLayout relativeLayout;

    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            for (int i = 0; i <listRealEstate.size() ; i++) {
                if (String.valueOf(listRealEstate.get(i).getId()).contains(getArguments().getString(ARG_ITEM_ID))) {
                    estateGrabbed = listRealEstate.get(i);
                }
            }

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
        deployTextView(rootView);
        realStateIfExist(rootView);
        return rootView;
    }


    private void deployCarteImageView(String url, View container) {
        ImageView imageCarte = container.findViewById(R.id.mapLiteView);
        Picasso.get().load(url).into(imageCarte);
    }

    private void deployRecyclerViewDetails(View container) {
        adapter = new AdaptateurImage(estateGrabbed.getPhotosReal(), getContext(), estateGrabbed.getDescriptionImage());
        recyclerView = container.findViewById(R.id.RecycleDetails);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void deployTextView(View container) {
        initiateTextView(container);
    }

    private void initiateTextView(View container) {
        adresse = container.findViewById(R.id.adresseDetails);
        description = container.findViewById(R.id.descirptionInputDetails);
        ville = container.findViewById(R.id.villeDetails);
//        region = container.findViewById(R.id.regionVille);
        pays = container.findViewById(R.id.paysDetails);
        surfaceChiffre = container.findViewById(R.id.SurfacechiffresDetails);
        priceChiffre = container.findViewById(R.id.PriceChiffresDetails);
        roomChiffre = container.findViewById(R.id.RoomechiffresDetails);
        typeCHiffre = container.findViewById(R.id.TypeChiffresDetails);
        bathroomchiffre = container.findViewById(R.id.BathroomchiffresDetails);
        nearbyChiffre = container.findViewById(R.id.NearbyChiffresDetails);
        chamber = container.findViewById(R.id.RoomschiffresDetails);
        agent = container.findViewById(R.id.agentDetails);
    }


    private void realStateIfExist(View container) {
        estateGrabbed = grabEstatFromMainActivity();
        if (estateGrabbed != null) {
            deployRecyclerViewDetails(container);
            shareInformationsDetails(container);
            try2FindAddress(container);
            initiateSwitchSell(container);

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
        if (estateGrabbed.getNearby()!=null) {
            nearbyChiffre.setText(estateGrabbed.getNearby().toString());
        }
        chamber.setText(estateGrabbed.getChambre());
        description.setText(estateGrabbed.getDescription());
        agent.setText(estateGrabbed.getNomAgent());
    }

    private void initiateSwitchSell(View rootView) {
        relativeLayout = rootView.findViewById(R.id.RelativeSelledDetails);
        if (estateGrabbed.getIschecked()!=null && estateGrabbed.getSelled()!=null) {
            if (!Boolean.valueOf(estateGrabbed.getIschecked()) && estateGrabbed.getSelled().equals("date")) {
                relativeLayout.setVisibility(View.INVISIBLE);
            } else {
                relativeLayout.setVisibility(View.VISIBLE);
                appearDateSell(rootView);
            }
        }
    }

    private void appearDateSell(View rootView) {
        vendu = rootView.findViewById(R.id.dateSelledDetails);
        vendu.setText(estateGrabbed.getSelled());
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


    public void try2FindAddress(final View container) {
        Utils.findAddress(getContext(), estateGrabbed.getAdresse(), new Utils.AdressGenerators() {
            @Override
            public void onSuccess(List<Address> addressList) {
                findTheRightAdress(container,addressList);
            }

            @Override
            public void onEchec() {
                findTheDefaultAdress();
            }

            @Override
            public void onCrash() {
                findTheDefaultAdress();
            }
        });
    }

    private void findTheDefaultAdress() {
        adresse.setText(estateGrabbed.getAdresse());
        ville.setText(estateGrabbed.getTown());
        pays.setText(estateGrabbed.getTown());
    }

    private void findTheRightAdress(View container, List<Address> addressList) {
        adresse.setText(addressList.get(0).getThoroughfare());
        ville.setText(estateGrabbed.getTown());
        pays.setText(addressList.get(0).getCountryName());
        latLngRealestate = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
        String url = Utils.linkBuilder(latLngRealestate);
        deployCarteImageView(url, container);
    }


    private void modifyThisEstate() {
        RealEstate estate = grabEstatFromMainActivity();
        if (estate != null) {
            Intent intent = new Intent(getContext(), AddInformationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("RealEstate", estate);
            intent.putExtras(bundle);
            startActivity(intent);
            getActivity().finish();
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
