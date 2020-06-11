package com.openclassrooms.realestatemanager.Api;

import com.openclassrooms.realestatemanager.modele.MediaImage;
import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.util.List;

public class ExtendedServiceEstate implements InterfaceRealEstate{
    private List<RealEstate> realEstates = ListGenerator.getRealEstateList();
    private List<MediaImage> realEstatesImage = ListGenerator.getRealEstateListImage();

    public List<RealEstate> getRealEstateList() {
        return realEstates;
    }
    public List<MediaImage> getRealEstateImageList() {
        return realEstatesImage;
    }


}
