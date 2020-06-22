package com.openclassrooms.realestatemanager.Api;

import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.util.List;

public interface InterfaceRealEstate {
    List<RealEstate> getRealEstateList();
    List<RealEstate> getTempList();
}
