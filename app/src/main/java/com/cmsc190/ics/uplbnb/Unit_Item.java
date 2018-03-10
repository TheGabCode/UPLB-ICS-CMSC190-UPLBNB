package com.cmsc190.ics.uplbnb;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Dell on 16 Feb 2018.
 */

public class Unit_Item implements Serializable{
    private Long unitIdentifier;
    private int condition; //for apartments
    private int status; //1-open 0-closed
    private int slotsAvailable; //for dormitories //-1 for Apartments
    private int capacity; //for dorms
    private int rate; //for apartments if not fixed
    private boolean ratePerHead; //for dormitories
    private String id;
    private HashMap<String,Integer> furniture; //for dormitories

    public Unit_Item(){

    }

    public Unit_Item(Long unitIdentifier, int status, int slotsAvailable, int rate, String id, int condition, HashMap<String,Integer> furniture,boolean ratePerHead, int capacity) {
        this.unitIdentifier = unitIdentifier;
        this.status = status;
        this.slotsAvailable = slotsAvailable;
        this.rate = rate;
        this.id = id;
        this.condition = condition;
        this.furniture = furniture;
        this.ratePerHead = ratePerHead;
        this.capacity = capacity;
    }

    public Long getUnitIdentifier() {
        return unitIdentifier;
    }

    public int getCapacity(){
        return capacity;
    }
    public HashMap<String,Integer> getFurniture(){
        return this.furniture;
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

    public int getCondition() { return condition; }

    public String getId(){
        return this.id;
    }

    public boolean isRatePerHead(){
        return ratePerHead;
    }
}
