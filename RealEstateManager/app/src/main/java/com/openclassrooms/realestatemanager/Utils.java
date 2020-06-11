package com.openclassrooms.realestatemanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.format.DateUtils;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Philippe on 21/02/2018.
 */

public class Utils {

    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param dollars
     * @return
     */
    public static int convertDollarToEuro(int dollars){
        return (int) Math.round(dollars * 0.812);
    }

    public static int convertEuroToDollar(int euros){
        return (int) Math.round(euros / 0.812);
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @return
     */

    public static String getDateFormat(Context context,Calendar c) {
        return DateUtils.formatDateTime(context, c.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
    }
    public static String getTodayDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(new Date());
    }
    public static String getDollarFormat(int number2Convert){
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
     * @param context
     * @return
     */
    public static Boolean isInternetAvailable(Context context){
        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    // Verify if user have GPS on
    public static void GPSOnVerify(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertGPSInactif(context);
        }
    }

    // Show Notification if GPS is off
    private static void AlertGPSInactif(Context context) {
        AlertDialog alertDialog = AlertGPS(context);
        alertDialog.show();
    }

    private static AlertDialog AlertGPS(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Message").setTitle("Alert GPS").setPositiveButton("Message", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.GPSOnVerify(context);
            }
        });
        return builder.create();
    }

    // Verify if user have Internet is on and stop if not
    public static boolean InternetOnVerify(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            AlertInternetInactif(context);
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

    public static String linkBuilder(LatLng latLng){
        String linkTransformed = "https://maps.googleapis.com/maps/api/staticmap?zoom=15&size=600x300&maptype=roadmap&markers=color:red%7Clabel:C%7C " + latLng.latitude + "," + latLng.longitude + "&key=" + "AIzaSyC_WkswyFXkxGjNsC4Ie_zoMbA5WuiRR68";
        return linkTransformed;
    }


}
