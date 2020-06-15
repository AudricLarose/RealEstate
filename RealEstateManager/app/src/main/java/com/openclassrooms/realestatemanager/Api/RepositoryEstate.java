package com.openclassrooms.realestatemanager.Api;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.openclassrooms.realestatemanager.EstateDao;
import com.openclassrooms.realestatemanager.modele.RealEstate;

public class RepositoryEstate {
        private EstateDao estateDao;
        private LiveData<List<RealEstate>> allData;

        public RepositoryEstate(Application application){
            DataBaseSQL dataBaseSQL= DataBaseSQL.getInstance(application);
            estateDao=dataBaseSQL.estateDao();
            allData=estateDao.selectAllEstate();
        }

        public void InsertData(RealEstate estate) {
            new InsertAsynchrone(estateDao).execute(estate);
        }
        public void deleteData(RealEstate estate) {
            new DeleteAsynchrone(estateDao).execute(estate);
        }
        public void upDateData(RealEstate estate) {
            new upDateAsynchrone(estateDao).execute(estate);
        }
        public void DeleteAllData() {
            new deleteAllAsynchrone(estateDao).execute();
        }

        public LiveData<List<RealEstate>> selectData (){
            return allData;
        }

        public static class InsertAsynchrone extends AsyncTask<RealEstate,Void, Void> {
            private EstateDao estateDao ;

            private InsertAsynchrone(EstateDao estateDao) {
                this.estateDao = estateDao;
            }

            @Override
            protected Void doInBackground(RealEstate... estate) {
                estateDao.insertEstate(estate[0]);
                return null;
            }
        }
        public static class DeleteAsynchrone extends AsyncTask<RealEstate,Void, Void>{
            private EstateDao estateDao ;

            private DeleteAsynchrone(EstateDao estateDao) {
                this.estateDao = estateDao;
            }

            @Override
            protected Void doInBackground(RealEstate... estate) {
                estateDao.deleteEstate(Long.valueOf(estate[0].getId()));
                return null;
            }
        }
        public static class upDateAsynchrone extends AsyncTask<RealEstate,Void, Void>{
            private EstateDao estateDao ;

            private upDateAsynchrone(EstateDao estateDao) {
                this.estateDao = estateDao;
            }

            @Override
            protected Void doInBackground(RealEstate... estate) {
                estateDao.upDateEstate(estate[0]);
                return null;
            }
        }
        public static class deleteAllAsynchrone extends AsyncTask<Void,Void, Void>{
            private EstateDao estateDao ;

            private deleteAllAsynchrone(EstateDao estateDao) {
                this.estateDao = estateDao;
            }

            @Override
            protected Void doInBackground(Void... voids) {
                estateDao.DeleteAllEstate();
                return null;
            }
        }
    }
    
