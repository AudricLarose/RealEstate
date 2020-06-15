package com.openclassrooms.realestatemanager.Api;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.util.List;


public class EstateViewModel extends AndroidViewModel {
        private LiveData<List<RealEstate>> allNotes;
        private RepositoryEstate repositoryEstate;

        public EstateViewModel(@NonNull Application application) {
            super(application);
            repositoryEstate= new RepositoryEstate(application);
            allNotes=repositoryEstate.selectData();
        }

        public void InsertThisData (RealEstate realEstate){
            repositoryEstate.InsertData(realEstate);
        }
        public void UpdateThisData(RealEstate realEstate){
            repositoryEstate.upDateData(realEstate);
        }
        public void deleteThisData (RealEstate realEstate){
            repositoryEstate.deleteData(realEstate);
        }
        public void deleteAlldata (){
            repositoryEstate.DeleteAllData();
        }
        public LiveData<List<RealEstate>> SelectAllThosedatas (){
            return allNotes;
        }

    }
