package com.cmsc190.ics.uplbnb;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dell on 16 Feb 2018.
 */

public class Establishment_Item implements Serializable{
    private String establishmentName;
    private String contactPerson;
    private String contactNumber1;
    private String contactNumber2;
    private int price; //price, fixed for dormitories
    private String address;
    private String curfewHours;
    private boolean visitorsAllowed;
    private int establishmentType; //if 1-apartment 0-dormitory
    private boolean billsIncludedInRate;
    private int distanceFromCampus;
    private boolean security;
    //private List<Unit_Item> units;
    private boolean concealContactPerson;
    private boolean concealPrice;
    private boolean concealAvailableUnits;
    private float rating;
    private String id;
    public Establishment_Item(String establishmentName, String contactPerson, String contactNumber1, String contactNumber2, int price, String address, String curfewHours, boolean visitorsAllowed, int establishmentType, boolean billsIncludedInRate, int distanceFromCampus, boolean security, List<Unit_Item> units, boolean concealContactPerson, boolean concealPrice, boolean concealAvailableUnits, float rating, String id) {
        this.establishmentName = establishmentName;
        this.contactPerson = contactPerson;
        this.contactNumber1 = contactNumber1;
        this.contactNumber2 = contactNumber2;
        this.price = price;
        this.rating = rating;
        this.address = address;
        this.curfewHours = curfewHours;
        this.visitorsAllowed = visitorsAllowed;
        this.establishmentType = establishmentType;
        this.billsIncludedInRate = billsIncludedInRate;
        this.distanceFromCampus = distanceFromCampus;
        this.security = security;
        //this.units = units;
        this.id = id;
        this.concealContactPerson = concealContactPerson;
        this.concealPrice = concealPrice;
        this.concealAvailableUnits = concealAvailableUnits;
    }

    public String getEstablishmentName() {
        return establishmentName;
    }

    public String getId(){return this.id;}
    public float getRating(){ return rating; }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getContactNumber1() {
        return contactNumber1;
    }

    public String getContactNumber2() {
        return contactNumber2;
    }

    public int getPrice() {
        return price;
    }

    public String getAddress() {
        return address;
    }

    public String getCurfewHours() {
        return curfewHours;
    }

    public boolean isVisitorsAllowed() {
        return visitorsAllowed;
    }

    public int getEstablishmentType() {
        return establishmentType;
    }

    public boolean isBillsIncludedInRate() {
        return billsIncludedInRate;
    }

    public int getDistanceFromCampus() {
        return distanceFromCampus;
    }

    public boolean isSecurity() {
        return security;
    }

  /*  public List<Unit_Item> getUnits() {
        return units;
    }*/

    public boolean isConcealContactPerson() {
        return concealContactPerson;
    }

    public boolean isConcealPrice() {
        return concealPrice;
    }

    public boolean isConcealAvailableUnits() {
        return concealAvailableUnits;
    }

    public Establishment_Item(String establishmentName, int establishmentType) {
        this.establishmentName = establishmentName;
        this.establishmentType = establishmentType;
    }

    public Establishment_Item() {
    }
}

