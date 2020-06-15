package com.openclassrooms.realestatemanager;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.openclassrooms.realestatemanager.Api.DataBaseSQL;
import com.openclassrooms.realestatemanager.modele.RealEstate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.shadows.ShadowNetworkInfo;

import java.util.ArrayList;
import java.util.List;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.robolectric.Shadows.shadowOf;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleUnitTest {
    // DATA SET FOR TEST
    private static long USER_ID = 1;
    List<String> resultsValidatedByUser = new ArrayList<>();
    List<RealEstate> resultsRealEstate = new ArrayList<>();
    List<String> listPhotoRealistetate = new ArrayList<>();
    List<String> listDescriptionRealistetate = new ArrayList<>();
    private RealEstate estate1;
    private RealEstate estate2;
    private RealEstate estate3;
    private RealEstate estate4;
    private RealEstate estate5;
    // FOR DATA
    private ContentResolver mContentResolver;
    private PersonContentProvider personContentProvider;

    @Before
    public void setUp() {
        Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                DataBaseSQL.class)
                .allowMainThreadQueries()
                .build();
        mContentResolver = ApplicationProvider.getApplicationContext().getContentResolver();

        resultsValidatedByUser.add("ecoles");
        estate1 = new RealEstate(String.valueOf(true), String.valueOf(true), "appartement", "Myrtis Jackson", resultsValidatedByUser, "5 rue henrio cardinaud",
                "1", "Super", "20/03/2019", "94000", "5", "95000000", "1", "1"
                , "creteil", "26/02/2020", 20000.20, 2000.23, "google.ccom", listPhotoRealistetate, listDescriptionRealistetate);
        estate5 = new RealEstate(String.valueOf(true), String.valueOf(true), "Maison", "Gilles de SaintPatu", resultsValidatedByUser, "5 rue henrio cardinaud",
                "1", "Super", "20/03/2019", "87000", "7", "85000000", "1", "1"
                , "Croatie", "26/02/2020", 14520.20, 2000.23, "google.ccom", listPhotoRealistetate, listDescriptionRealistetate);
        estate2 = new RealEstate(String.valueOf(true), String.valueOf(true), "appartement", "Benjamin Doteil", resultsValidatedByUser, "5 rue henrio cardinaud",
                "1", "Super", "20/03/2019", "59000", "1", "75000000", "1", "1"
                , "Saint-JEan-Luz", "26/02/2020", 4547.20, 5000.83, "google.ccom", listPhotoRealistetate, listDescriptionRealistetate);
        estate3 = new RealEstate(String.valueOf(true), String.valueOf(true), "appartement", "Myrtis Jackson", resultsValidatedByUser, "5 rue henrio cardinaud",
                "1", "Super", "20/03/2019", "15000", "5", "65000000", "1", "1"
                , "issy-les_mouilneaux", "26/02/2020", 11400.20, 700.23, "google.ccom", listPhotoRealistetate, listDescriptionRealistetate);
        estate4 = new RealEstate(String.valueOf(true), String.valueOf(true), "appartement", "Myrtis Jackson", resultsValidatedByUser, "5 rue henrio cardinaud",
                "1", "Super", "20/03/2019", "89500", "8", "55000000", "1", "1"
                , "bonneuil", "26/02/2020", 147500.20, 45000.23, "google.ccom", listPhotoRealistetate, listDescriptionRealistetate);
    }

    @Test
    public void test (){
        final Cursor cursor = personContentProvider.query(ContentUris.withAppendedId(PersonContentProvider.URI_ITEM, USER_ID), null, null, null, null);
    }
    //
    @Test
    public void getItemsWhenNoItemInserted() {
//         final Cursor cursor = mContentResolver.query(ContentUris.withAppendedId(PersonContentProvider.URI_ITEM, USER_ID), null, null, null, null);
         personContentProvider= new PersonContentProvider();

        final Cursor cursor = personContentProvider.query(ContentUris.withAppendedId(PersonContentProvider.URI_ITEM, USER_ID), null, null, null, null);
        assertThat(cursor, notNullValue());
        assertThat(cursor.getCount(), is(0));
        cursor.close();
    }

    @Test
    public void insertAndGetItem() {
        // BEFORE : Adding demo item
        final Uri userUri = mContentResolver.insert(PersonContentProvider.URI_ITEM, generateItem());
        // TEST
        final Cursor cursor = mContentResolver.query(ContentUris.withAppendedId(PersonContentProvider.URI_ITEM, USER_ID), null, null, null, null);
        assertThat(cursor, notNullValue());
        assertThat(cursor.getCount(), is(1));
        assertThat(cursor.moveToFirst(), is(true));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("text")), is("Visite cet endroit de rêve !"));
    }

    // ---

    private ContentValues generateItem() {
        final ContentValues values = new ContentValues();
        values.put("text", "Visite cet endroit de rêve !");
        values.put("category", "0");
        values.put("isSelected", "false");
        values.put("userId", "1");
        return values;
    }


    @Test
    public void test_az_comparator() {
        resultsRealEstate.add(estate1);
        resultsRealEstate.add(estate2);
        resultsRealEstate.add(estate3);
        resultsRealEstate.add(estate4);
        resultsRealEstate.add(estate5);
        List<RealEstate> esperance = Utils.sortedbyPriceCroissant(resultsRealEstate);
        assertSame(esperance.get(0), estate4);
        assertSame(esperance.get(1), estate3);
        assertSame(esperance.get(2), estate2);
    }

    @Test
    public void test_za_comparator() {
        resultsRealEstate.add(estate1);
        resultsRealEstate.add(estate2);
        resultsRealEstate.add(estate3);
        resultsRealEstate.add(estate4);
        resultsRealEstate.add(estate5);
        List<RealEstate> esperance = Utils.sortedbyPriceDecroissant(resultsRealEstate);
        assertSame(esperance.get(0), estate1);
        assertSame(esperance.get(1), estate5);
        assertSame(esperance.get(2), estate2);
    }

    @Test
    public void convertDollarToEuroTest() {
        resultsRealEstate.add(estate1);
        resultsRealEstate.add(estate2);
        resultsRealEstate.add(estate3);
        resultsRealEstate.add(estate4);
        resultsRealEstate.add(estate5);
        List<RealEstate> esperance = Utils.sortedbyPriceDecroissant(resultsRealEstate);
        assertSame(esperance.get(0), estate1);
        assertSame(esperance.get(1), estate5);
        assertSame(esperance.get(2), estate2);
    }

    @Test
    public void convertEuroToDollar() {
        int prixModified = Utils.convertDollarToEuro(10000);
        assertEquals(prixModified, 8120);
    }

    @Test
    public void convertDollarToEuro() {
        int prixModified = Utils.convertEuroToDollar(10000);
        int prixEsperer = 12315;
        assertEquals(prixModified, prixEsperer);
    }

    @Test
    public void getEuroFormatTest() {
        String prixModified = Utils.getEuroFormat(10000);
        String prixEsperer = "10 000,00 €";
        assertEquals(prixModified, prixEsperer);
    }

    @Test
    public void getDollarFormatTest() {
        String prixModified = Utils.getDollarFormat(10000);
        String prixEsperer = "$10,000.00";
        assertEquals(prixModified, prixEsperer);
    }

    @Test
    public void checkIFInternetIsOff() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        ShadowNetworkInfo shadowOfActiveNetworkInfo = shadowOf(connectivityManager.getActiveNetworkInfo());
        shadowOfActiveNetworkInfo.setConnectionStatus(NetworkInfo.State.DISCONNECTED);
        assertFalse(Utils.isInternetAvailable(getApplicationContext()));
    }

    @Test
    public void checkIFInternetIsOn() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        ShadowNetworkInfo shadowOfActiveNetworkInfo = shadowOf(connectivityManager.getActiveNetworkInfo());
        shadowOfActiveNetworkInfo.setConnectionStatus(NetworkInfo.State.CONNECTED);
        assertTrue(Utils.isInternetAvailable(getApplicationContext()));
    }

}