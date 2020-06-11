package com.openclassrooms.realestatemanager.Api;

public class DI {
    private static ExtendedServiceEstate extendedServiceEstate= new ExtendedServiceEstate();
    public static ExtendedServiceEstate getService(){return extendedServiceEstate;}
}
