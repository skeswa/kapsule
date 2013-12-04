package com.epsilonlabs.kapsule.model;

/**
 * Created by Sandile on 12/3/13.
 */
public class Truck extends Vehicle {
    String company;

    public Truck(String make, String model, String company) {
        super(make, model);
        this.company = company;
    }
}
