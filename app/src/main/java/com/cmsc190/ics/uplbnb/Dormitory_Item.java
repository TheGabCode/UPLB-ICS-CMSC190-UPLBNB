package com.cmsc190.ics.uplbnb;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Dell on 20 Feb 2018.
 */

public class Dormitory_Item extends Establishment_Item {
    private HashMap<String, Integer> availableFurniture; //for dormitories String/Key is the item, Integer/Value is the quantity
    private boolean ratePerHead; //for dormitories, if true then rate is per head e.g 600/person
    private int capacityPerUnit;

    public Dormitory_Item(){

    }

    public Dormitory_Item(String establishmentName, String contactPerson, String contactNumber1, String getContactNumber2, int price, String address, String curfewHours, boolean visitorsAllowed, int establishmentType, boolean billsIncludedInRate, int distanceFromCampus, boolean security, List<Unit_Item> units, boolean concealContactPerson, boolean concealPrice, boolean concealAvailableUnits, HashMap<String, Integer> availableFurniture, boolean ratePerHead, int capacityPerUnit, float rating,String id) {
        super(establishmentName, contactPerson, contactNumber1, getContactNumber2, price, address, curfewHours, visitorsAllowed, establishmentType, billsIncludedInRate, distanceFromCampus, security, units, concealContactPerson, concealPrice, concealAvailableUnits,rating,id);
        this.availableFurniture = availableFurniture;
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