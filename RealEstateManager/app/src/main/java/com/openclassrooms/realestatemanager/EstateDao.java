package com.openclassrooms.realestatemanager;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.util.List;

    @Dao
    public interface EstateDao {

        @Query("SELECT * FROM bdd ORDER BY Id ASC")
        LiveData<List<RealEstate>> selectAllEstate();

        @Insert
        long insertEstate(RealEstate task);

        @Update
        int uploadEstate(RealEstate task);

        @Query("DELETE FROM bdd WHERE id = :itemid")
        int deleteEstate(long itemid);

        @Query("DELETE FROM bdd")
        void DeleteAllEstate();
    }

