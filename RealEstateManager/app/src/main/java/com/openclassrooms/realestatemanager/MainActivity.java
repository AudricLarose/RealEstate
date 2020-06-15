package com.openclassrooms.realestatemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.openclassrooms.realestatemanager.Api.DI;
import com.openclassrooms.realestatemanager.Api.EstateViewModel;
import com.openclassrooms.realestatemanager.Api.ExtendedServiceEstate;
import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExtendedServiceEstate serviceEstate = DI.getService();
    private List<RealEstate> listRealEstate = serviceEstate.getRealEstateList();
    private Adaptateur adapter;
    private RecyclerView.LayoutManager layoutManager;
    private boolean amIInEuro=true;
    private EstateViewModel estateViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(" Bienvenue ");
        deployementButtonAdd();
        saveDataInSQLITE();
        Utils.isInternetAvailable(this);


    }

    private void saveDataInSQLITE() {
        estateViewModel= ViewModelProviders.of(this).get(EstateViewModel.class);
        estateViewModel.SelectAllThosedatas().observe(this, new Observer<List<RealEstate>>() {
            @Override
            public void onChanged(List<RealEstate> realEstates) {
                listRealEstate.clear();
                listRealEstate.addAll(realEstates);
                deployRecyclerView();
            }
        });
    }


    private void deployRecyclerView() {
//        adapter = new Adaptateur(listRealEstate);
        recyclerView = findViewById(R.id.RecyclerviewEstate);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void deployementButtonAdd() {
        final FloatingActionButton addButton = initiateButtonAdd();
        activateButtonAdd(addButton);
    }

    private FloatingActionButton initiateButtonAdd() {
        return findViewById(R.id.buttonadd);
    }

    private void activateButtonAdd(FloatingActionButton addButton) {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddInformationActivity.class);
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
                knowIfConvertToEuroOrDOllar();
                return true;
            case R.id.subcroissant:
                List<RealEstate> realEstateListcroissant = Utils.sortedbyPriceCroissant(listRealEstate);
//                adapter = new Adaptateur(realEstateListcroissant,false,this);
                recyclerView.setAdapter(adapter);
                return true;
            case R.id.subDecroissant:
                List<RealEstate> realEstateListdecroissant = Utils.sortedbyPriceDecroissant(listRealEstate);
//                adapter = new Adaptateur(realEstateListdecroissant);
                recyclerView.setAdapter(adapter);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deploySearchActivity() {
        Intent intent= new Intent(MainActivity.this,SearchActivity.class);
        startActivity(intent);
    }

    private void deployGoogleMap() {
        Intent intent= new Intent(MainActivity.this,MapsActivity.class);
        startActivity(intent);
    }

    private void knowIfConvertToEuroOrDOllar(){
        if (amIInEuro){
            convertEuroList();
            amIInEuro=false;
            changeMyEuroParameter();
        } else {
            convertDollarList();
            amIInEuro=true;
            changeMyEuroParameter();
        }
    }

    private void changeMyEuroParameter(){
        if (amIInEuro){
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
        for (int i = 0; i < listRealEstate.size(); i ++ ) {
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
