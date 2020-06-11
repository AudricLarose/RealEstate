package com.openclassrooms.realestatemanager.Api;

import com.openclassrooms.realestatemanager.modele.MediaImage;
import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public  abstract class ListGenerator {

    public static List<RealEstate> realEstateList = Arrays.asList(

    );
    public static List<RealEstate> getRealEstateList() {
        return new ArrayList<>(realEstateList);
    }

    public static List<MediaImage> realEstateListImage = Arrays.asList(

            );
    public static List<MediaImage> getRealEstateListImage() {
        return new ArrayList<>(realEstateListImage);
    }
}

