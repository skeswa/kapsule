package com.epsilonlabs.kapsule;

import android.app.Activity;
import android.test.AndroidTestCase;
import android.util.Log;

import com.epsilonlabs.kapsule.model.Car;
import com.epsilonlabs.kapsule.model.Truck;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Sandile on 12/3/13.
 */
public class BasicTestCase extends AndroidTestCase {
    public void testBaseCase() {
        Kapsule.context(new Activity()); // TODO replace this for actual db related calls
        Kapsule.put("car1", new Car("Honda", "Civic", 3)).into("test").then(new Put.Callback() {
            @Override
            public void success() {
                Log.d("test", "car1 put() successfully");
            }

            @Override
            public void failure(Throwable e) {
                Log.d("test", "car1 put() failed: " + e);
                e.printStackTrace();
            }
        });
        Kapsule.put("truck1", new Truck("Volvo", "Bravo", "Giant Foods")).into("test").synchronously();
        Log.d("test", "truck1 put() successfully");

        Car car1 = Kapsule.get("car1", Car.class).from("test").synchronously();
        Log.d("test", "car1 get() successfully: " + car1);
        Kapsule.get("truck1", Truck.class).from("test").then(new Get.Callback<Truck>() {
            @Override
            public void success(Truck result) {
                Log.d("test", "truck1 get() successfully: " + result);
            }

            @Override
            public void failure(Throwable e) {
                Log.d("test", "truck1 get() failed: " + e);
                e.printStackTrace();
            }
        });
    }

    public void testCollectionCase() {
        Kapsule.context(new Activity()); // TODO replace this for actual db related calls
        Kapsule.put("cars", Arrays.asList(new Car("Toyota", "Camry", 4),
                new Car("Toyota", "Camry", 4),
                new Car("Toyota", "Camry", 4),
                new Car("Toyota", "Camry", 4),
                new Car("Toyota", "Camry", 4),
                new Car("Toyota", "Camry", 4),
                new Car("Toyota", "Camry", 4),
                new Car("Toyota", "Camry", 4),
                new Car("Toyota", "Camry", 4),
                new Car("Toyota", "Camry", 4),
                new Car("Toyota", "Camry", 4))).synchronously();
        Log.d("kapsuletest", "cars put() successfully");

        Kapsule.get("cars", Car.class).collection().then(new GetCollection.Callback<Car>() {
            @Override
            public void success(Collection<Car> result) {
                Log.d("kapsuletest", "cars get() successfully: " + result.size());
            }

            @Override
            public void failure(Throwable e) {
                Log.e("kapsuletest", "cars get() failed: " + e);
                e.printStackTrace();
            }
        });

        Log.d("kapsuletest", "attempting to wait for a photo finish");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void testCollectionCase2() {
        Kapsule.context(new Activity()); // TODO replace this for actual db related calls
        Kapsule.put("cars1", Arrays.asList(new Car("Toyota", "Camry", 4),
                new Car("Toyota", "Camry", 4),
                new Car("Toyota", "Camry", 4),
                new Car("Toyota", "Camry", 4),
                new Car("Toyota", "Camry", 4),
                new Car("Toyota", "Camry", 4),
                new Car("Toyota", "Camry", 4),
                new Car("Toyota", "Camry", 4),
                new Car("Toyota", "Camry", 4),
                new Car("Toyota", "Camry", 4),
                new Car("Toyota", "Camry", 4))).synchronously();
        Log.d("kapsuletest", "cars1 put() successfully");

        Long stamp = System.currentTimeMillis();
        try {
            Kapsule.get("cars1", Car.class).collection().synchronously().size();
            Log.d("kapsuletest", "cars1 get() successfully");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("kapsuletest", "cars1 get() failed: " + e);
        }
        Log.d("kapsuletest", "How long was it? " + (System.currentTimeMillis() - stamp));
    }
}
