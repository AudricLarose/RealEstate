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
    public static final String AUTHORITY = "com.openclassrooms.realestatemanager";
    public static final String TAG = PersonContentProvider.class.getName();
    public static final String PERSON_TABLE_NAME = "bdd";
    public static final Uri URI_ITEM = Uri.parse("content://" + AUTHORITY + "/" + PERSON_TABLE_NAME);
    public static final int ID_PERSON_DATA = 1;
    public static final int ID_PERSON_DATA_ITEM = 2;
    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, PERSON_TABLE_NAME, ID_PERSON_DATA);
        uriMatcher.addURI(AUTHORITY, PERSON_TABLE_NAME + "/*", ID_PERSON_DATA_ITEM);
    }

    private EstateDao estateDao;

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        Log.d(TAG, "query");
        Cursor cursor;
        cursor = (Cursor) estateDao.selectAllEstate();
        if (getContext() != null) {
            long userId = ContentUris.parseId(uri);
            estateDao = DataBaseSQL.getInstance(getContext()).estateDao();
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        }


        throw new IllegalArgumentException
                ("Unknown URI: " + uri);

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (uriMatcher.match(uri)) {
            case ID_PERSON_DATA:
                if (getContext() != null) {

                    long id = DataBaseSQL.getInstance(getContext()).estateDao().insertEstate(RealEstate.fromContentValues(values));
                    if (id != 0) {
                        getContext().getContentResolver().notifyChange(uri, null);
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
                    int count = DataBaseSQL.getInstance(getContext()).estateDao().deleteEstate(ContentUris.parseId(uri));
                    getContext().getContentResolver().notifyChange(uri, null);
                    return count;
                }
            default:
                throw new IllegalArgumentException("Unknown URI:" + uri);
        }

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        switch (uriMatcher.match(uri)) {
            case ID_PERSON_DATA:
                if (getContext() != null) {
                    int count = estateDao.upDateEstate(RealEstate.fromContentValues(values));
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