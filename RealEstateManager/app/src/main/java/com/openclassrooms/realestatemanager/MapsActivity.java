package com.openclassrooms.realestatemanager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.openclassrooms.realestatemanager.Api.DI;
import com.openclassrooms.realestatemanager.Api.ExtendedServiceEstate;
import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.location.GpsStatus.GPS_EVENT_SATELLITE_STATUS;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static FusedLocationProviderClient fusedLocationProviderClient;
    private Button btnLocaliser;
    private List<String> globalResult = new ArrayList<>();
    private ExtendedServiceEstate serviceEstate = DI.getService();
    private List<RealEstate> listRealEstate = serviceEstate.getRealEstateList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        deployOnMapReady();
//        checkGPSStatus();
    }

//    private void checkGPSStatus() {
//        LocationManager lm = (LocationManager) getSystemService(MapsActivity. LOCATION_SERVICE ) ;
//        boolean gps_enabled = false;
//        boolean network_enabled = false;
//        try {
//            gps_enabled = lm.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;
//        } catch (Exception e) {
//            e.printStackTrace() ;
//        }
//        try {
//            network_enabled = lm.isProviderEnabled(LocationManager. NETWORK_PROVIDER ) ;
//        } catch (Exception e) {
//            e.printStackTrace() ;
//        }
//        if (!gps_enabled && !network_enabled) {
//            new AlertDialog.Builder(MapsActivity.this)
//                    .setMessage("GPS Enable")
//                    .setPositiveButton("Settings", new
//                            DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                                }
//                            })
//                    .setNegativeButton("Cancel", null)
//                    .show();
//        }
//    }

    private void deployOnMapReady() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        searchMe(googleMap);
        deployFindMeButton(googleMap);
        verifyIfPlaceCanBePlaced(googleMap);
    }

    private void deployFindMeButton(final GoogleMap googleMap) {
        btnLocaliser = findViewById(R.id.localiser);
        btnLocaliser.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                searchMe(googleMap);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void searchMe(GoogleMap googleMap) {
        askPermission(googleMap);
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askPermission(final GoogleMap googleMap) {
        if ((MapsActivity.this.checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (MapsActivity.this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{ACCESS_FINE_LOCATION}, 1);
        } else {
            placeMeOnMap(googleMap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = null;
            LocationListener locationListener = null;
            if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }

        }
    }
    private void placeMeOnMap(final GoogleMap googleMap) {
        Utils.GPSOnVerify(MapsActivity.this, new Utils.GPSCallBAck() {
            @Override
            public void onRetrieve() {
                retievedPosition(googleMap);

            }
        });
    }

    private void retievedPosition(final GoogleMap googleMap) {
        try {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.getResult() != null) {
                        double latitude = task.getResult().getLatitude();
                        double longitude = task.getResult().getLongitude();
                        LatLng latLng = new LatLng(latitude, longitude);
                        googleMap.addMarker(new MarkerOptions().position(latLng).title("Me"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MapsActivity.this, "Votre Position n'a pas été trouvé, " +e.getLocalizedMessage()+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    e.getCause();
                    e.fillInStackTrace();
                }
            });
        } catch (Exception e) {
            Toast.makeText(MapsActivity.this, "Votre Position n'a pas été trouvé, " +e.getLocalizedMessage()+ e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            e.getCause();
            e.fillInStackTrace();
        }
    }

    private void verifyIfPlaceCanBePlaced(GoogleMap googleMap) {
        for (int i = 0; i < listRealEstate.size(); i++) {
            searchAdressIfExist(listRealEstate.get(i),googleMap);
        }
    }

    private void searchAdressIfExist(RealEstate estate, final GoogleMap Map) {
        if (estate.getAdresse()!=null){
            Utils.findAddress(MapsActivity.this, estate.getAdresse(), new Utils.AdressGenerators() {
                @Override
                public void onSuccess(List<Address> addressList) {
                    searchAndPlacePlaces(Map,addressList);
                }

                @Override
                public void onEchec() {
                    Toast.makeText(MapsActivity.this, "Aucune Place n'a été trouvé", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCrash() {
                    Toast.makeText(MapsActivity.this, "Aucune Place n'a été trouvé", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
    private void searchAndPlacePlaces(GoogleMap googleMap, List<Address> addressList) {
        try {
            for (int i = 0; i < addressList.size(); i++) {
                LatLng latLngRealestate = new LatLng(addressList.get(i).getLatitude(), addressList.get(i).getLongitude());
                googleMap.addMarker(new MarkerOptions().position(latLngRealestate).title("here"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLngRealestate));
            }
        } catch (Exception e) {
            Toast.makeText(this, "L'adresse indiquée n'a pas été trouvé sur notre banque de donnée", Toast.LENGTH_SHORT).show();
        }
    }

}
