package com.openclassrooms.realestatemanager.Api;

import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.util.List;

public class ExtendedServiceEstate implements InterfaceRealEstate{
    private List<RealEstate> realEstates = ListGenerator.getRealEstateList();
    private List<RealEstate> realtemplist = ListGenerator.getTempList();
    public List<RealEstate> getRealEstateList() {
        return realEstates;
    }

    @Override
    public List<RealEstate> getTempList() {
        return realtemplist;
    }


}
