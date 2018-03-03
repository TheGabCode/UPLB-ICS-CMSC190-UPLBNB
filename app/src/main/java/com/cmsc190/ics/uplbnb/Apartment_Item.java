package com.cmsc190.ics.uplbnb;

import java.util.List;

/**
 * Created by Dell on 20 Feb 2018.
 */

public class Apartment_Item extends Establishment_Item {
    private int fixedPrice; //for apartments if all units have one rate if != null then price is fixed, otherwise each unit must have its own price
    private int rentYears; // for apartment
    private boolean furnished; //for apartments, true if furnished, false otherwise

    public Apartment_Item(){

    }

    public Apartment_Item(String establishmentName, String contactPerson, String contactNumber1, String getContactNumber2, int price, String address, String curfewHours, boolean visitorsAllowed, int establishmentType, boolean billsIncludedInRate, int distanceFromCampus, boolean security, List<Unit_Item> units, boolean concealContactPerson, boolean concealPrice, boolean concealAvailableUnits, int fixedPrice, int rentYears, boolean furnished, float rating, String id) {
        super(establishmentName, contactPerson, contactNumber1, getContactNumber2, price, address, curfewHours, visitorsAllowed, establishmentType, billsIncludedInRate, distanceFromCampus, security, units, concealContactPerson, concealPrice, concealAvailableUnits, rating,id);
        this.fixedPrice = fixedPrice;
        this.rentYears = rentYears;
        this.furnished = furnished;
    }

    public Apartment_Item(String establishmentName, int establishmentType, int fixedPrice, int rentYears, boolean furnished) {
        super(establishmentName, establishmentType);
        this.fixedPrice = fixedPrice;
        this.rentYears = rentYears;
        this.furnished = furnished;
    }

    public Apartment_Item(int fixedPrice, int rentYears, boolean furnished) {
        this.fixedPrice = fixedPrice;
        this.rentYears = rentYears;
        this.furnished = furnished;
    }

    public int getFixedPrice() {
        return fixedPrice;
    }

    public int getRentYears() {
        return rentYears;
    }

    public boolean isFurnished() {
        return furnished;
    }
}
