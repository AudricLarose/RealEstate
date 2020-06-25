package com.openclassrooms.realestatemanager.modele;

import android.content.ContentValues;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Entity(tableName = "bdd")
public class RealEstate implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String type;
    private String adresse;
    private String postal;
    private String town;
    private String description;
    private String nomAgent;

    @TypeConverters(Converters.class)
    private List<String> descriptionImage;

    @TypeConverters(Converters.class)
    private List<String> nearby;
    private String chambre;
    private String piece;
    private String sdb;
    private String surface;
    private String market;
    private String prix;

    @TypeConverters(Converters.class)
    private List<String> photosReal;
    private String ischecked;
    private Double lattitude;
    private Double longitude;
    private String url;
    private String inEuro;
    private String selled;
    private String temp = "false";

    @TypeConverters(Converters.class)
    private List<String> urlFireBase;

    public RealEstate() {
    }

    public RealEstate(String inEuro, String ischecked, String type, String nomAgent, List<String> nearby, String adresse, String chambre, String description, String market, String postal, String piece, String prix, String sdb, String surface, String town, String selled, Double latitude, Double longitude, String url, List<String> photosReal, List<String> descriptionImage) {
        this.type = type;
        this.adresse = adresse;
        this.postal = postal;
        this.town = town;
        this.description = description;
        this.nomAgent = nomAgent;
        this.nearby = nearby;
        this.chambre = chambre;
        this.piece = piece;
        this.sdb = sdb;
        this.surface = surface;
        this.market = market;
        this.prix = prix;
        this.ischecked = ischecked;
        this.lattitude = latitude;
        this.longitude = longitude;
        this.url = url;
        this.photosReal = photosReal;
        this.inEuro = inEuro;
        this.selled = selled;
        this.descriptionImage = descriptionImage;
    }

    public static RealEstate fromContentValues(ContentValues contentValues) {
        RealEstate estate = new RealEstate();
        if (contentValues.containsKey("id")) {
            estate.setId(contentValues.getAsInteger("id"));
        }
        if (contentValues.containsKey("type")) {
            estate.setType(contentValues.getAsString("type"));
        }
        if (contentValues.containsKey("adresse")) {
            estate.setAdresse(contentValues.getAsString("adresse"));
        }
        if (contentValues.containsKey("postal")) {
            estate.setTown(contentValues.getAsString("town"));
        }
        if (contentValues.containsKey("description")) {
            estate.setDescription(contentValues.getAsString("description"));
        }
        if (contentValues.containsKey("nomAgent")) {
            estate.setNomAgent(contentValues.getAsString("nomAgent"));
        }
        if (contentValues.containsKey("nearby")) {
            estate.setNearby(Collections.singletonList(contentValues.getAsString("nearby")));
        }
        if (contentValues.containsKey("chambre")) {
            estate.setChambre(contentValues.getAsString("chambre"));
        }
        if (contentValues.containsKey("piece")) {
            estate.setPiece(contentValues.getAsString("piece"));
        }
        if (contentValues.containsKey("sdb")) {
            estate.setSdb(contentValues.getAsString("sdb"));
        }
        if (contentValues.containsKey("surface")) {
            estate.setSurface(contentValues.getAsString("surface"));
        }
        if (contentValues.containsKey("market")) {
            estate.setMarket(contentValues.getAsString("market"));
        }
        if (contentValues.containsKey("selled")) {
            estate.setMarket(contentValues.getAsString("selled"));
        }
        if (contentValues.containsKey("prix")) {
            estate.setPrix(contentValues.getAsString("prix"));
        }
        if (contentValues.containsKey("photosReal")) {
            estate.setPhotosReal(Collections.singletonList(contentValues.getAsString("photosReal")));
        }
        if (contentValues.containsKey("ischecked")) {
            estate.setIschecked((contentValues.getAsString("ischecked")));
        }
        if (contentValues.containsKey("lattitude")) {
            estate.setLattitude(contentValues.getAsDouble("lattitude"));
        }
        if (contentValues.containsKey("longitude")) {
            estate.setLattitude(contentValues.getAsDouble("longitude"));
        }
        if (contentValues.containsKey("url")) {
            estate.setMarket(contentValues.getAsString("url"));
        }
        if (contentValues.containsKey("inEuro")) {
            estate.setPrix(contentValues.getAsString("inEuro"));
        }
        return estate;

    }

    public List<String> getUrlFireBase() {
        return urlFireBase;
    }

    public void setUrlFireBase(List<String> urlFireBase) {
        this.urlFireBase = urlFireBase;
    }

    public List<String> getDescriptionImage() {
        return descriptionImage;
    }

    public void setDescriptionImage(List<String> descriptionImage) {
        this.descriptionImage = descriptionImage;
    }

    public String getSelled() {
        return selled;
    }

    public void setSelled(String selled) {
        this.selled = selled;
    }

    public List<String> getPhotosReal() {
        return photosReal;
    }

    public void setPhotosReal(List<String> photosReal) {
        this.photosReal = photosReal;
    }

    public String getInEuro() {
        return inEuro;
    }

    public void setInEuro(String inEuro) {
        this.inEuro = inEuro;
    }

    public Double getLattitude() {
        return lattitude;
    }

    public void setLattitude(Double lattitude) {
        this.lattitude = lattitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNomAgent() {
        return nomAgent;
    }

    public void setNomAgent(String nomAgent) {
        this.nomAgent = nomAgent;
    }

    public String getIschecked() {
        return ischecked;
    }

    public void setIschecked(String ischecked) {
        this.ischecked = ischecked;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getNearby() {
        return nearby;
    }

    public void setNearby(List<String> nearby) {
        this.nearby = nearby;
    }

    public String getChambre() {
        return chambre;
    }

    public void setChambre(String chambre) {
        this.chambre = chambre;
    }

    public String getPiece() {
        return piece;
    }

    public void setPiece(String piece) {
        this.piece = piece;
    }

    public String getSdb() {
        return sdb;
    }

    public void setSdb(String sdb) {
        this.sdb = sdb;
    }

    public String getSurface() {
        return surface;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }
}
