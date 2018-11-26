package com.example.ibct.appdatxe;

import android.util.Log;
import android.widget.Toast;

import com.example.ibct.appdatxe.data.Car;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Until {
    public  ArrayList<Car> CalculationByDistance(ArrayList<Car> arrCar, LatLng StartP, double distance) {
        ArrayList<Car> arrCarNear = new ArrayList<>();
        for (Car car : arrCar) {
            int Radius = 6371;// radius of earth in Km
            double lat1 = StartP.latitude;
            double lon1 = StartP.longitude;
            double lat2 = car.getViDo();
            double lon2 = car.getKinhDo();
            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.cos(Math.toRadians(lat1))
                    * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                    * Math.sin(dLon / 2);
            double c = 2 * Math.asin(Math.sqrt(a));
            double valueResult = Radius * c;
            double meter = valueResult / 1000;
            Log.d("km",meter+"");
            Log.d("khoangcca ",distance+"");
            if (meter <= distance) {
                arrCarNear.add(car);
            }
        }
        return arrCarNear;
    }
}
