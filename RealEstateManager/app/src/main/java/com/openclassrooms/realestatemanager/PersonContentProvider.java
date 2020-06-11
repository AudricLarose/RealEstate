package com.openclassrooms.realestatemanager;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.openclassrooms.realestatemanager.Api.DataBaseSQL;
import com.openclassrooms.realestatemanager.modele.RealEstate;

public class PersonContentProvider extends ContentProvider {

    public static final String TAG = PersonContentProvider.class.getName();

    private EstateDao estateDao;

    /**
     * Authority of this content provider
     */
    public static final String AUTHORITY =    " com.openclassrooms.realestatemanager";

    public static final String PERSON_TABLE_NAME = "bdd";

    /**
     * The match code for some items in the Person table
     */
    public static final int ID_PERSON_DATA = 1;

    /**
     * The match code for an item in the PErson table
     */
    public static final int ID_PERSON_DATA_ITEM = 2;

    public static final UriMatcher uriMatcher = new UriMatcher
            (UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY,PERSON_TABLE_NAME,ID_PERSON_DATA);
        uriMatcher.addURI(AUTHORITY,PERSON_TABLE_NAME +"/*", ID_PERSON_DATA_ITEM);
    }

    @Override
    public boolean onCreate() {
        estateDao = DataBaseSQL.getInstance(getContext()).estateDao();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        Log.d(TAG, "query");
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case ID_PERSON_DATA:
                cursor = (Cursor) estateDao.selectAllEstate();
                if (getContext() != null) {
                    cursor.setNotificationUri(getContext()
                            .getContentResolver(), uri);
                    return cursor;
                }

            default:
                throw new IllegalArgumentException
                        ("Unknown URI: " + uri);

        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri,
                      @Nullable ContentValues values) {
        Log.d(TAG, "insert");
        switch (uriMatcher.match(uri)) {
            case ID_PERSON_DATA:
                if (getContext() != null) {
                    long id = estateDao.insertEstate(RealEstate.fromContentValues(values));
                    if (id != 0) {
                        getContext().getContentResolver()
                                .notifyChange(uri, null);
                        return ContentUris.withAppendedId(uri, id);
                    }
                }
            case ID_PERSON_DATA_ITEM:
                throw new IllegalArgumentException
                        ("Invalid URI: Insert failed" + uri);
            default:
                throw new IllegalArgumentException
                        ("Unknown URI: " + uri);
        }

    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        Log.d(TAG, "delete");
        switch (uriMatcher.match(uri)) {
            case ID_PERSON_DATA:
                throw new IllegalArgumentException
                        ("Invalid uri: cannot delete");
            case ID_PERSON_DATA_ITEM:
                if (getContext() != null) {
                    int count = estateDao.deleteEstate(ContentUris.parseId(uri));
                    getContext().getContentResolver().notifyChange(uri, null);
                    return count;
                }
            default:
                throw new IllegalArgumentException ("Unknown URI:" + uri);
        }

    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues values,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        Log.d(TAG, "update");
        switch (uriMatcher.match(uri)) {
            case ID_PERSON_DATA:
                if (getContext() != null) {
                    int count = estateDao.uploadEstate(RealEstate.fromContentValues(values));
                    if (count != 0) {
                        getContext().getContentResolver().notifyChange(uri, null);
                        return count;
                    }
                }
            case ID_PERSON_DATA_ITEM:
                throw new IllegalArgumentException("Invalid URI:  cannot update");
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

    }
}