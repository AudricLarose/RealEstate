package com.openclassrooms.realestatemanager;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ApplicationProvider;

import com.openclassrooms.realestatemanager.Api.DataBaseSQL;
import com.openclassrooms.realestatemanager.modele.RealEstate;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class DataProvider {

    // DATA SET FOR TEST
    private static long USER_ID = 1;
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

    }

    //
    @Test
    public void getItemsWhenNoItemInserted() {
         final Cursor cursor = mContentResolver.query(ContentUris.withAppendedId(PersonContentProvider.URI_ITEM, USER_ID), null, null, null, null);
        personContentProvider= new PersonContentProvider();

//        final Cursor cursor = personContentProvider.query(ContentUris.withAppendedId(PersonContentProvider.URI_ITEM, USER_ID), null, null, null, null);
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
        assertThat(cursor.getCount(), is(0));
        assertThat(cursor.moveToFirst(), is(false));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("description")), is("desc"));
    }

    // ---

    private ContentValues generateItem() {
        final ContentValues values = new ContentValues();
        values.put("description", "desc");
        values.put("type", "0");
        values.put("postal", "94000");
        values.put("chambre", "1");
        return values;
    }

}
