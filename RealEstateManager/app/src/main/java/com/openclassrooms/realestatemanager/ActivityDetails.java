package com.openclassrooms.realestatemanager;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.openclassrooms.realestatemanager.Api.DI;
import com.openclassrooms.realestatemanager.Api.ExtendedServiceEstate;
import com.openclassrooms.realestatemanager.modele.MediaImage;
import com.openclassrooms.realestatemanager.modele.RealEstate;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ActivityDetails extends AppCompatActivity {
    private static RealEstate estateGrabbed;
    private TextView adresse, ville, region, pays, surfaceChiffre, priceChiffre, roomChiffre, typeCHiffre, bathroomchiffre, nearbyChiffre, chamber;
    private AdaptateurImage adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ExtendedServiceEstate serviceEstate = DI.getService();
    private List<MediaImage> listRealEstateImage = serviceEstate.getRealEstateImageList();
    private List<RealEstate> listRealEstate = serviceEstate.getRealEstateList();
    private boolean amIInEuro = true;
    private static RealEstate estate;
    private LatLng latLngRealestate;
    private  Bundle mapViewBundle=null;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        deployTextView();
        realStateIfExist();
        actionfleche();
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle("AIzaSyC_WkswyFXkxGjNsC4Ie_zoMbA5WuiRR68");
        }
    }

    private void deployCarteImageView(String url) {
        ImageView imageCarte = findViewById(R.id.mapLiteView);
        Picasso.get().load(url).into(imageCarte);
    }

    private void actionfleche() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void deployRecyclerViewDetails() {
        adapter = new AdaptateurImage(estateGrabbed.getPhotosReal());
        recyclerView = findViewById(R.id.RecycleDetails);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(ActivityDetails.this, RecyclerView.HORIZONTAL , false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void deployTextView() {
        initiateTextView();
    }

    private void initiateTextView() {
        adresse = findViewById(R.id.adresseDetails);
        ville = findViewById(R.id.villeDetails);
        region = findViewById(R.id.regionVille);
        pays = findViewById(R.id.paysDetails);
        surfaceChiffre = findViewById(R.id.SurfacechiffresDetails);
        priceChiffre = findViewById(R.id.PriceChiffresDetails);
        roomChiffre = findViewById(R.id.RoomechiffresDetails);
        typeCHiffre = findViewById(R.id.TypeChiffresDetails);
        bathroomchiffre = findViewById(R.id.BathroomchiffresDetails);
        nearbyChiffre = findViewById(R.id.NearbyChiffresDetails);
        chamber = findViewById(R.id.RoomschiffresDetails);
    }

    private void realStateIfExist() {
        estateGrabbed = grabEstatFromMainActivity();
        if (estateGrabbed != null) {
            deployRecyclerViewDetails();
            shareInformationsDetails();
            findAddress();
        }
    }

    private RealEstate grabEstatFromMainActivity() {
        RealEstate estate = null;
        Intent intent = this.getIntent();
        Bundle extra = intent.getExtras();
        if (extra != null) {
            estate = (RealEstate) extra.getSerializable("RealEstate");
        }
        return estate;
    }

    private void shareInformationsDetails() {
        surfaceChiffre.setText(estateGrabbed.getSurface());
        priceChiffre.setText(Utils.getEuroFormat(Integer.valueOf(estateGrabbed.getPrix())));
        roomChiffre.setText(estateGrabbed.getPiece());
        typeCHiffre.setText(estateGrabbed.getType());
        bathroomchiffre.setText(estateGrabbed.getSdb());
        nearbyChiffre.setText(estateGrabbed.getNearby().toString());
        chamber.setText(estateGrabbed.getChambre());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.retour_arriere_modifier, menu);
        return true;
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

    public void findAddress(){
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressList= geocoder.getFromLocationName(estateGrabbed.getAdresse().toString(),1);
            if (addressList!=null && addressList.size()>0){
                adresse.setText(addressList.get(0).getThoroughfare());
                ville.setText(addressList.get(0).getCountryName());
                pays.setText(addressList.get(0).getCountryName());
                latLngRealestate= new LatLng(addressList.get(0).getLatitude(),addressList.get(0).getLongitude());
                String url=Utils.linkBuilder(latLngRealestate);
                deployCarteImageView(url);

            }
        } catch (IOException e) {
            adresse.setText(estateGrabbed.getAdresse());
            ville.setText(estateGrabbed.getTown());
            pays.setText(estateGrabbed.getTown()); }
    }


    private void modifyThisEstate() {
        RealEstate estate=grabEstatFromMainActivity();
        if (estate!=null) {
            Intent intent = new Intent(ActivityDetails.this, AddInformationActivity.class);
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
