package com.openclassrooms.realestatemanager.dummy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.realestatemanager.Adaptateur;
import com.openclassrooms.realestatemanager.AddInformationActivity;
import com.openclassrooms.realestatemanager.Api.DI;
import com.openclassrooms.realestatemanager.Api.EstateViewModel;
import com.openclassrooms.realestatemanager.Api.ExtendedServiceEstate;
import com.openclassrooms.realestatemanager.MapsActivity;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.SearchActivity;
import com.openclassrooms.realestatemanager.Utils;
import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private RecyclerView recyclerView;
    private ExtendedServiceEstate serviceEstate = DI.getService();
    private List<RealEstate> listRealEstate = serviceEstate.getRealEstateList();
    private Adaptateur adapter;
    private RecyclerView.LayoutManager layoutManager;
    private boolean amIInEuro = true;
    private EstateViewModel estateViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Bienvenue !");
        detailsIfTablet();
        saveDataInSQLITE();
        deployementButtonAdd();
        deployementButtonInternet();
        Utils.isInternetAvailable(this);
        Utils.internetIsOn(this);
//        Utils.GPSOnVerify(this);


    }


    private void deployRecyclerView() {
        adapter = new Adaptateur(listRealEstate, mTwoPane, this);
        recyclerView = findViewById(R.id.RecyclerviewEstate);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(ItemListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void saveDataInSQLITE() {
        estateViewModel = ViewModelProviders.of(this).get(EstateViewModel.class);
        estateViewModel.SelectAllThosedatas().observe(this, new Observer<List<RealEstate>>() {
            @Override
            public void onChanged(List<RealEstate> realEstates) {
                listRealEstate.clear();
                listRealEstate.addAll(realEstates);
                deployRecyclerView();
            }
        });
    }

    private void detailsIfTablet() {
        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }
    }

    private void deployementButtonAdd() {
        final ImageButton addButton = initiateButtonAdd();
        activateButtonAdd(addButton);
    }

    private void deployementButtonInternet() {
        final ImageButton internetButton = initiateButtonInternet();
        activateButtonInternet(internetButton);
    }

    private static void buttonInternetInfo(Context context) {
        AlertDialog alertDialog = buttonInternetInfoDialog(context);
        alertDialog.show();
    }

    private static AlertDialog buttonInternetInfoDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Vous devez activer votre connexion internet pour profiter de l'application." +
                " Cependant vos informations sont sauvegarder sur votre téléphone et seront envoyées" +
                "lorsque vous aurez de nouveau une connection").setTitle("Alert Internet").setPositiveButton("J'ai bien activé ma connexion", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.InternetOnVerify(context);
            }
        });
        return builder.create();
    }

    private void activateButtonInternet(ImageButton internetButton) {
      if (Utils.InternetOnVerify(this)){
          internetButton.setVisibility(View.GONE);
      } else {
          internetButton.setVisibility(View.VISIBLE);
          internetButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  buttonInternetInfo(ItemListActivity.this);
              }
          });
      }
    }


    private ImageButton initiateButtonAdd() {
        return findViewById(R.id.buttonadd);
    }

    private ImageButton initiateButtonInternet() {
        return findViewById(R.id.noInternet);
    }

    private void activateButtonAdd(ImageButton addButton) {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemListActivity.this, AddInformationActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.iconebarmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.maps:
                deployGoogleMap();
                return true;
            case R.id.search:
                deploySearchActivity();
                return true;
            case R.id.conversion:
                knowIfConvertToEuroOrDollar();
                return true;
            case R.id.subcroissant:
                List<RealEstate> realEstateListcroissant = Utils.sortedbyPriceCroissant(listRealEstate);
                adapter = new Adaptateur(realEstateListcroissant, mTwoPane, this);
                recyclerView.setAdapter(adapter);
                return true;
            case R.id.subDecroissant:
                List<RealEstate> realEstateListdecroissant = Utils.sortedbyPriceDecroissant(listRealEstate);
                adapter = new Adaptateur(realEstateListdecroissant, mTwoPane, this);
                recyclerView.setAdapter(adapter);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deploySearchActivity() {
        Intent intent = new Intent(ItemListActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    private void deployGoogleMap() {
        Utils.GPSOnVerify(this, new Utils.GPSCallBAck() {
            @Override
            public void onRetrieve() {
                Intent intent = new Intent(ItemListActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

    }

    private void knowIfConvertToEuroOrDollar() {
        if (amIInEuro) {
            convertEuroList();
            amIInEuro = false;
            changeMyEuroParameter();
        } else {
            convertDollarList();
            amIInEuro = true;
            changeMyEuroParameter();
        }
    }

    private void changeMyEuroParameter() {
        if (amIInEuro) {
            for (int i = 0; i < listRealEstate.size(); i++) {
                listRealEstate.get(i).setInEuro("true");
            }
        } else {
            for (int i = 0; i < listRealEstate.size(); i++) {
                listRealEstate.get(i).setInEuro("false");
            }
        }
    }

    private void convertDollarList() {
        for (int i = 0; i < listRealEstate.size(); i++) {
            int converted = Utils.convertDollarToEuro(Integer.valueOf(listRealEstate.get(i).getPrix()));
            listRealEstate.get(i).setPrix(String.valueOf((converted)));
        }
        saveDataInSQLITE();
    }

    private void convertEuroList() {
        for (int i = 0; i < listRealEstate.size(); i++) {
            int converted = Utils.convertEuroToDollar(Integer.valueOf(listRealEstate.get(i).getPrix()));
            listRealEstate.get(i).setPrix(String.valueOf((converted)));
        }
        saveDataInSQLITE();
    }

    @Override
    protected void onResume() {
        super.onResume();
        saveDataInSQLITE();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDataInSQLITE();

    }
}
