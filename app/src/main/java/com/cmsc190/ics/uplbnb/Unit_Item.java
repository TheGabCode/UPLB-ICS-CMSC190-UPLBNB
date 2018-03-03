package com.cmsc190.ics.uplbnb;

/**
 * Created by Dell on 16 Feb 2018.
 */

public class Unit_Item {
    private String unitIdentifier;
    private int status; //1-open 0-closed
    private int slotsAvailable; //for dormitories //-1 for Apartments
    private int rate; //for apartments if not fixed
    private String id;

    public Unit_Item(String unitIdentifier, int status, int slotsAvailable, int rate, String id) {
        this.unitIdentifier = unitIdentifier;
        this.status = status;
        this.slotsAvailable = slotsAvailable;
        this.rate = rate;
        this.id = id;
    }

    public String getUnitIdentifier() {
        return unitIdentifier;
    }

    public int getStatus() {
        return status;
    }

    public int getSlotsAvailable() {
        return slotsAvailable;
    }

    public int getRate() {
        return rate;
    }

    public String getId(){
        return this.id;
    }
}
