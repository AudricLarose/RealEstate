package com.openclassrooms.realestatemanager.modele;

import java.io.Serializable;

public class MediaImage implements Serializable {
    private int id;
    private int realEstateId;
    private String photo;

    public MediaImage(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRealEstateId() {
        return realEstateId;
    }

    public void setRealEstateId(int realEstateId) {
        this.realEstateId = realEstateId;
    }
}
