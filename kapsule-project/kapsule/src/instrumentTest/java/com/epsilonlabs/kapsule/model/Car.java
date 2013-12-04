package com.epsilonlabs.kapsule.model;

/**
 * Created by Sandile on 12/3/13.
 */
public class Car extends Vehicle {
    int passengers;

    public Car(String make, String model, int passengers) {
        super(make, model);
        this.passengers = passengers;
    }
}
