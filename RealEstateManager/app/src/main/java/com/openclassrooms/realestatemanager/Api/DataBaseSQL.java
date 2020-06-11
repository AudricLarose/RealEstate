package com.openclassrooms.realestatemanager.Api;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.openclassrooms.realestatemanager.EstateDao;
import com.openclassrooms.realestatemanager.modele.RealEstate;

@Database(entities = {RealEstate.class}, version = 8)
public abstract class DataBaseSQL extends RoomDatabase {
    private static DataBaseSQL instance;
    private static RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateNoteAsyncTask(instance).execute();
        }
    };

    public static synchronized DataBaseSQL getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    DataBaseSQL.class, "bdd")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallBack)
                    .build();
        }
        return instance;
    }

    public abstract EstateDao estateDao();

    private static class PopulateNoteAsyncTask extends AsyncTask<Void, Void, Void> {
        private EstateDao taskDao;

        private PopulateNoteAsyncTask(DataBaseSQL db) {
            taskDao = db.estateDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}




