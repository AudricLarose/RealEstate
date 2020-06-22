package com.openclassrooms.realestatemanager;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.openclassrooms.realestatemanager.Api.DI;
import com.openclassrooms.realestatemanager.Api.ExtendedServiceEstate;
import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

/**
 * Created by Philippe on 21/02/2018.
 */

public class Utils {

    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @param dollars
     * @return
     */
    public static int convertDollarToEuro(int dollars) {
        return (int) Math.round(dollars * 0.812);
    }

    public static int convertEuroToDollar(int euros) {
        return (int) Math.round(euros / 0.812);
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @return
     */

    public static String getDateFormat(Context context, Calendar c) {
        return DateUtils.formatDateTime(context, c.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
    }

    public static String getTodayDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(new Date());
    }

    public static String getDollarFormat(int number2Convert) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);

        return format.format(number2Convert);

    }

    public static String getEuroFormat(int converted) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        return format.format(converted);
    }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @param context
     * @return
     */
    public static Boolean isInternetAvailable(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    // Verify if user have GPS on
    public static void GPSOnVerify(Context context, GPSCallBAck gpsCallBAck) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertGPSInactif(context);
        } else {
            gpsCallBAck.onRetrieve();
        }
    }

    // Show Notification if GPS is off
    private static void AlertGPSInactif(Context context) {
        AlertDialog alertDialog = AlertGPS(context);
        alertDialog.show();
    }

    private static AlertDialog AlertGPS(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("You have to Set On Your GPS").setTitle("Alert GPS").setPositiveButton("J'active mon GPS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                Utils.GPSOnVerify(context, new GPSCallBAck() {
                    @Override
                    public void onRetrieve() {

                    }
                });

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder.create();
    }

    // Verify if user have Internet is on and stop if not
    public static boolean InternetOnVerify(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
//            AlertInternetInactif(context);
            return false;
        }
        return true;
    }

    // Verify if user have internet on and display just a message
    public static Boolean internetIsOn(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            Toast.makeText(context, "Message", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    // Show Notification if GPS is off
    private static void AlertInternetInactif(Context context) {
        AlertDialog alertDialog = AlertInternet(context);
        alertDialog.show();
    }

    private static AlertDialog AlertInternet(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Vous devez activer votre connexion internet pour profiter de l'application").setTitle("Alert Internet").setPositiveButton("J'ai bien activé ma connexion", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.InternetOnVerify(context);
            }
        });
        return builder.create();
    }

    public static List<RealEstate> sortedbyPriceCroissant(List<RealEstate> listeRealEstate) {
        Collections.sort(listeRealEstate, new Comparator<RealEstate>() {
            @Override
            public int compare(RealEstate o1, RealEstate o2) {
                if (Integer.valueOf(o1.getPrix()) > Integer.valueOf(o2.getPrix())) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
        return listeRealEstate;
    }

    public static List<RealEstate> sortedbyPriceDecroissant(List<RealEstate> listeRealEstate) {
        Collections.sort(listeRealEstate, new Comparator<RealEstate>() {
            @Override
            public int compare(RealEstate o1, RealEstate o2) {
                if (Integer.valueOf(o1.getPrix()) > Integer.valueOf(o2.getPrix())) {
                    return -1;
                } else {
                    return +1;
                }
            }

        });
        return listeRealEstate;
    }

    public static String linkBuilder(LatLng latLng) {
        String linkTransformed = "https://maps.googleapis.com/maps/api/staticmap?zoom=15&size=600x300&maptype=roadmap&markers=color:red%7Clabel:C%7C " + latLng.latitude + "," + latLng.longitude + "&key=" + "AIzaSyC_WkswyFXkxGjNsC4Ie_zoMbA5WuiRR68";
        return linkTransformed;
    }

    public static void findAddress(Context context, String adress, AdressGenerators retroAction) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(adress, 1);
            if (addressList != null && addressList.size() > 0) {
                retroAction.onSuccess(addressList);
            } else {
                retroAction.onEchec();
            }
        } catch (IOException e) {
            retroAction.onCrash();
        }
    }

    public static void saveDataInBDD(final CallBackInterfaceForBDD callBackInterfaceForBDD) {
        ExtendedServiceEstate servicePlace = DI.getService();
        final List<RealEstate> listeRealEstate = servicePlace.getRealEstateList();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("realestates")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<RealEstate> resultsBDD = null;
                        if (queryDocumentSnapshots != null) {
                            resultsBDD = queryDocumentSnapshots.toObjects(RealEstate.class);
                            callBackInterfaceForBDD.onFinish(resultsBDD, e);

                        } else {
                            callBackInterfaceForBDD.onFail();

                        }
                    }
                });
    }

    public static void getNEarbyList(final RealEstate resultsBDD) {
        ExtendedServiceEstate servicePlace = DI.getService();
        final List<RealEstate> listeRealEstate = servicePlace.getRealEstateList();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("estates")
                .document(String.valueOf(resultsBDD.hashCode()))
                .collection("Nearby").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        resultsBDD.getNearby().add(document.getString("nom"));
                        resultsBDD.getNearby();
                    }
                }
            }
        });
    }

    public static void sendItToMyBDDatRealEstate(RealEstate estate) {
        Map note = new HashMap();
        note.put("id", estate.getId());
        note.put("type", estate.getType());
        note.put("nomAgent", estate.getNomAgent());
        note.put("adresse", estate.getAdresse());
        note.put("ischecked", estate.getIschecked());
        note.put("postal", estate.getPostal());
        note.put("town", estate.getTown());
        note.put("description", estate.getDescription());
        note.put("chambre", estate.getChambre());
        note.put("piece", estate.getPiece());
        note.put("sdb", estate.getSdb());
        note.put("surface", estate.getSurface());
        note.put("market", estate.getMarket());
        note.put("prix", estate.getPrix());
        note.put("lattitude", estate.getLattitude());
        note.put("longitude", estate.getLongitude());
        note.put("url", estate.getUrl());
        note.put("inEuro", estate.getInEuro());
        note.put("selled", estate.getSelled());
        note.put("nearby", estate.getNearby());
        note.put("photosReal", estate.getPhotosReal());
        note.put("descriptionImage", estate.getDescriptionImage());

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("realestates").document(String.valueOf(estate.hashCode())).set(note);
//        getNearbyList(estate, firebaseFirestore);
//        getPhotosRealList(estate, firebaseFirestore);
//        for (int i = 0; i < estate.getPhotosReal().size(); i++) {
//            Map<String, Object> note4 = new HashMap<>();
//            note4.put("nom", estate.getDescriptionImage().get(i));
//            firebaseFirestore.collection("estates").document(String.valueOf(estate.hashCode())).collection("descriptionImage").add(note4);
//        }
//
//    }

//    private static void getPhotosRealList(RealEstate estate, FirebaseFirestore firebaseFirestore) {
//        for (int i = 0; i < estate.getPhotosReal().size(); i++) {
//            Map<String, Object> note3 = new HashMap<>();
//            note3.put("nom", estate.getPhotosReal().get(i));
//            firebaseFirestore.collection("estates").document(String.valueOf(estate.hashCode())).collection("photosReal").add(note3);
//        }
//    }
//
//    private static void getNearbyList(RealEstate estate, FirebaseFirestore firebaseFirestore) {
//        for (int i = 0; i < estate.getNearby().size(); i++) {
//            Map<String, Object> note2 = new HashMap<>();
//            note2.put("nom", estate.getNearby().get(i));
//            firebaseFirestore.collection("estates").document(String.valueOf(estate.hashCode())).collection("nearby").add(note2);
//        }
    }

    public static String[] uploadImage(RealEstate estate, final Context context) {
        final String[] url = {""};
        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference(String.valueOf(estate.hashCode()));
        UploadTask uploadTask = mStorageRef.putFile(Uri.parse(estate.getPhotosReal().get(0)));
        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Toast.makeText(context, "Le fichier n'est pas envoyé car la connexion est mauvaise", Toast.LENGTH_SHORT).show();
                }
                return mStorageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    url[0] = task.getResult().toString();
                }
            }
        });
        return url;
    }

    public static void downLoadImages(final RealEstate estate, final Context context, final DownloadController downloadController) {
        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference ref = mStorageRef.child("206614279");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                downloadFile(context, String.valueOf(estate.hashCode()), ".jpeg", DIRECTORY_DOWNLOADS, url);
                Toast.makeText(context, "Succes ! ", Toast.LENGTH_SHORT).show();
                downloadController.onfInish(uri);
            }
        });

    }

    private static void downloadFile(Context context, String fileName, String fileExtension, String desintationDriectory, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, desintationDriectory, fileName + fileExtension);
        downloadManager.enqueue(request);

    }

    public interface DownloadController {
        void onfInish(Uri uri);
    }

    public interface CallBackInterfaceForBDD {
        void onFinish(List<RealEstate> realEstateList, FirebaseFirestoreException e);

        void onFail();
    }


    public interface GPSCallBAck {
        void onRetrieve();
    }

    public interface AdressGenerators {
        void onSuccess(List<Address> addressList);

        void onEchec();

        void onCrash();
    }
}

