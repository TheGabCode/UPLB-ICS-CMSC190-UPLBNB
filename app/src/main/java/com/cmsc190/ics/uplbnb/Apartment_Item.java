package com.cmsc190.ics.uplbnb;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dell on 20 Feb 2018.
 */

public class Apartment_Item extends Establishment_Item implements Serializable {
    //private int fixedPrice; //for apartments if all units have one rate if != null then price is fixed, otherwise each unit must have its own price
    private int rentYears; // for apartment
    private boolean furnished; //for apartments, true if furnished, false otherwise
    private boolean isFixedPrice;
    public Apartment_Item(){

    }

    public Apartment_Item(String establishmentName, String contactPerson, String contactNumber1, String getContactNumber2, String price, String address, String curfewHours, boolean visitorsAllowed, int establishmentType, boolean billsIncludedInRate, float distanceFromCampus, boolean security, boolean concealContactPerson, boolean concealPrice, boolean concealAvailableUnits, int rentYears, boolean furnished, float rating, String id, String owner_id, boolean isFixedPrice, HashMap<String, Review> reviews, double latitude, double longitude, PlaceInfo placeInfo) {
        super(establishmentName, contactPerson, contactNumber1, getContactNumber2, price, address, curfewHours, visitorsAllowed, establishmentType, billsIncludedInRate, distanceFromCampus, security, concealContactPerson, concealPrice, concealAvailableUnits, rating,id,owner_id,reviews,latitude,longitude,placeInfo);
       //this.fixedPrice = fixedPrice;
        this.rentYears = rentYears;
        this.furnished = furnished;
        this.isFixedPrice = isFixedPrice;
    }

    public Apartment_Item(String establishmentName, int establishmentType, int fixedPrice, int rentYears, boolean furnished) {
        super(establishmentName, establishmentType);
        //this.fixedPrice = fixedPrice;
        this.rentYears = rentYears;
        this.furnished = furnished;
    }

    public Apartment_Item(int fixedPrice, int rentYears, boolean furnished) {
        //this.fixedPrice = fixedPrice;
        this.rentYears = rentYears;
        this.furnished = furnished;
    }

    public boolean getIsFixedPrice(){
        return isFixedPrice;
    }

    /*public int getFixedPrice() {
        return fixedPrice;
    }*/

    public int getRentYears() {
        return rentYears;
    }

    public boolean isFurnished() {
        return furnished;
    }


}
