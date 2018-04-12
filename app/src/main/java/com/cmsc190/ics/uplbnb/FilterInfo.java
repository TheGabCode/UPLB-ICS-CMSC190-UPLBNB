package com.cmsc190.ics.uplbnb;

/**
 * Created by Dell on 11 Apr 2018.
 */

public class FilterInfo {
    int establishmentType;
    double minPrice;
    double maxPrice;
    int vacancies;
    int curfew;
    int visitors;
    int security;
    int tennants;
    float rating;

    public FilterInfo(int establishmentType, double minPrice, double maxPrice, int vacancies, int curfew, int visitors, int security, int tennants, float rating) {
        this.establishmentType = establishmentType;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.vacancies = vacancies;
        this.curfew = curfew;
        this.visitors = visitors;
        this.security = security;
        this.tennants = tennants;
        this.rating = rating;
    }

    public int getEstablishmentType() {
        return establishmentType;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public int getVacancies() {
        return vacancies;
    }

    public int getCurfew() {
        return curfew;
    }

    public int getVisitors() {
        return visitors;
    }

    public int getSecurity() {
        return security;
    }

    public int getTennants() {
        return tennants;
    }

    public float getRating() {
        return rating;
    }
}
