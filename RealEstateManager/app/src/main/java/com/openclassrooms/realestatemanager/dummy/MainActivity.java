package com.openclassrooms.realestatemanager.dummy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.FirebaseFirestoreException;
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

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private RecyclerView recyclerView;
    private ExtendedServiceEstate serviceEstate = DI.getService();
    private List<RealEstate> listRealEstate = serviceEstate.getRealEstateList();
    private List<RealEstate> listTemp = serviceEstate.getTempList();
    private Adaptateur adapter;
    private RecyclerView.LayoutManager layoutManager;
    private boolean amIInEuro = true;
    private EstateViewModel estateViewModel;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView textNotif;

    private static void buttonInternetInfo(Context context) {
        AlertDialog alertDialog = buttonInternetInfoDialog(context);
        alertDialog.show();
    }

    private static AlertDialog buttonInternetInfoDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.conexioGPStest ).setTitle(R.string.alertinternet ).setPositiveButton(R.string.internetActivate, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.InternetOnVerify(context);
            }
        });
        return builder.create();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.Welcome);
        detailsIfTablet();
        saveDataInSQLITE();
        takeDataInBDDIfInternetIsHere();
        deployementButtonAdd();
        deployementButtonMail();
        onSwipeToRefresh();
        Utils.isInternetAvailable(this);
        Utils.internetIsOn(this);
//        Utils.GPSOnVerify(this);
        DeploytempHandler();
        deployementNotificationMail();
    }

    private void deployementButtonMail() {
        final ImageButton mailButton = initateButtonMail();
        activateButtonMail(mailButton);
    }

    private void activateButtonMail(ImageButton mailButton) {
        mailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeploytempHandler();
            }
        });
    }

    private ImageButton initateButtonMail() {
        return findViewById(R.id.noInternet);
    }


    private void onSwipeToRefresh() {
        mSwipeRefreshLayout = findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DeploytempHandler();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void takeDataInBDDIfInternetIsHere() {
            Utils.saveDataInBDD(new Utils.CallBackInterfaceForBDD() {
                @Override
                public void onFinish(List<RealEstate> realEstateList, FirebaseFirestoreException e) {
                    estateViewModel.deleteAlldata();
                    for (int i = 0; i < realEstateList.size(); i++) {
                        estateViewModel.InsertThisData(realEstateList.get(i));
                    }
                    Utils.downLoadImages(realEstateList.get(0), MainActivity.this, new Utils.DownloadController() {
                        @Override
                        public void onfInish(Uri uri) {
                            String checker= String.valueOf(uri);
                        }
                    });

                }

                @Override
                public void onFail() {
                    Toast.makeText(MainActivity.this, R.string.nonew, Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void deployRecyclerView() {
        adapter = new Adaptateur(listRealEstate, mTwoPane, this);
        recyclerView = findViewById(R.id.RecyclerviewEstate);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(MainActivity.this);
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
                searchForNotif(realEstates);
                deployRecyclerView();
            }
        });
    }

    private void searchForNotif(List<RealEstate> realEstates) {
        listTemp.clear();
        for (int i = 0; i < realEstates.size() ; i++) {
            if (realEstates.get(i).getTemp().equals("true")){
                listTemp.add(realEstates.get(i));
                majNotif();
            }
        }
    }

    private void deployementNotificationMail() {
//        dialogBoxInformaer();
        iniatiateAndActivateNotifLayout();

    }

    private void iniatiateAndActivateNotifLayout() {
        textNotif = findViewById(R.id.cart_badge);
        majNotif();
    }

    private void majNotif() {
        textNotif.setText(String.valueOf(listTemp.size()));
    }

    private void DeploytempHandler() {
        if (listTemp.size() == 0) {
            Toast.makeText(this, R.string.aucunmail , Toast.LENGTH_SHORT).show();
        } else {
            if (Utils.isInternetAvailable(this)) {
                if (listTemp.size() > 0) {
                    for (int i = 0; i < listTemp.size(); i++) {
                        Utils.sendItToMyBDDatRealEstate(listTemp.get(i));
                        try {
                            Utils.uploadImage(listTemp.get(i), this, new Utils.CallBackImage() {
                                @Override
                                public void onFinish(List<String> s) {

                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        listTemp.get(i).setTemp("False");
                        estateViewModel.UpdateThisData(listTemp.get(i));
                    }
                    Toast.makeText(this, R.string.sent, Toast.LENGTH_SHORT).show();
                    listTemp.clear();
                    majNotif();
                }
            } else {
                Toast.makeText(this, R.string.ni_internet , Toast.LENGTH_LONG).show();
            }
        }
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


    private ImageButton initiateButtonAdd() {
        return findViewById(R.id.buttonadd);
    }

    private void activateButtonAdd(ImageButton addButton) {
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
//        MenuItem menuItem = menu.findItem(R.id.action_cart);
//        menuItem.setActionView(R.layout.notification_badge);
//        View view =menuItem.getActionView();
//        TextView badgeCOunter= view.findViewById(R.id.badge_counter);
//        badgeCOunter.setText(String.valueOf(listTemp.size()));
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
//            case  R.id.action_cart:
//                DeploytempHandler();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deploySearchActivity() {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    private void deployGoogleMap() {
        Utils.GPSOnVerify(this, new Utils.GPSCallBAck() {
            @Override
            public void onRetrieve() {
                if (Utils.isInternetAvailable(MainActivity.this)) {
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(intent);
                }
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
        majNotif();
    }

    @Override
    protected void onPause() {
        super.onPause();
        majNotif();
        saveDataInSQLITE();

    }
}
