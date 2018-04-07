package com.cmsc190.ics.uplbnb;

import android.os.Parcel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dell on 20 Feb 2018.
 */

public class Dormitory_Item extends Establishment_Item implements Serializable{
    private HashMap<String, Integer> availableFurniture; //for dormitories String/Key is the item, Integer/Value is the quantity
    private boolean ratePerHead; //for dormitories, if true then rate is per head e.g 600/person
    private int capacityPerUnit;

    public Dormitory_Item(){

    }


    public Dormitory_Item(String establishmentName, String contactPerson, String contactNumber1, String getContactNumber2, String price, String address, String curfewHours, boolean visitorsAllowed, int establishmentType, boolean billsIncludedInRate, float distanceFromCampus, boolean security, boolean concealContactPerson, boolean concealPrice, boolean concealAvailableUnits,  boolean ratePerHead, int capacityPerUnit, float rating,String id,String owner_id, HashMap<String,Integer> newFurniture, HashMap<String, Review> reviews, double latitude, double longitude,PlaceInfo placeInfo) {
        super(establishmentName, contactPerson, contactNumber1, getContactNumber2, price, address, curfewHours, visitorsAllowed, establishmentType, billsIncludedInRate, distanceFromCampus, security,concealContactPerson, concealPrice, concealAvailableUnits,rating,id,owner_id,reviews,latitude,longitude,placeInfo);
        this.availableFurniture = newFurniture;
        this.ratePerHead = ratePerHead;
        this.capacityPerUnit = capacityPerUnit;
    }

    public Dormitory_Item(String establishmentName, int establishmentType, HashMap<String, Integer> availableFurniture, boolean ratePerHead, int capacityPerUnit) {
        super(establishmentName, establishmentType);
        this.availableFurniture = availableFurniture;
        this.ratePerHead = ratePerHead;
        this.capacityPerUnit = capacityPerUnit;
    }

    public Dormitory_Item(HashMap<String, Integer> availableFurniture, boolean ratePerHead, int capacityPerUnit) {
        this.availableFurniture = availableFurniture;
        this.ratePerHead = ratePerHead;
        this.capacityPerUnit = capacityPerUnit;
    }

    public HashMap<String, Integer> getAvailableFurniture() {
        return availableFurniture;
    }

    public boolean isRatePerHead() {
        return ratePerHead;
    }

    public int getCapacityPerUnit() {
        return capacityPerUnit;
    }
}