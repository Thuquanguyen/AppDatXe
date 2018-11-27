package com.example.ibct.appdatxe;

import android.util.Log;
import android.widget.Toast;

import com.example.ibct.appdatxe.data.Car;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
//Class này dùng để tính khoảng cách từ xe tới người dùng và lọc ra các xe có bán kính nhỏ hơn ban kính người dùng đã thiết đặt
public class Until {
    public  ArrayList<Car> CalculationByDistance(ArrayList<Car> arrCar, LatLng StartP, double distance) {
        //Khởi tạo 1 mảng các xe
        ArrayList<Car> arrCarNear = new ArrayList<>();
        //duyệt mảng các xe chạy từ xe đầu tiên tới xe cuối cùng
        for (Car car : arrCar) {
            int Radius = 6371;// thiết đặt bán kính cố định của trái đất
            double lat1 = StartP.latitude; // lấy kinh độ hiện tại của người dùng
            double lon1 = StartP.longitude; // lấy vĩ độ hiện tại của người dùng
            double lat2 = car.getViDo(); // lấy kinh độ hiện tại của xe trong thông tin xe
            double lon2 = car.getKinhDo(); // lấy vĩ độ hiện tại của xe trong thông tin xe
            double dLat = Math.toRadians(lat2 - lat1); //tính khoảng cách giữa 2 điểm
            double dLon = Math.toRadians(lon2 - lon1); //tính khoảng cách giữa 2 điểm
            //tính khoảng cách giữa 2 điểm
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.cos(Math.toRadians(lat1))
                    * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                    * Math.sin(dLon / 2);
            //tính khoảng cách giữa 2 điểm
            double c = 2 * Math.asin(Math.sqrt(a));
            double valueResult = Radius * c;
            double meter = valueResult / 1000;
            Log.d("km",meter+"");
            Log.d("khoangcach ",distance+"");
            // Lấy được các xe có khoảng cách gần người dùng nhất cách người dùng distance km
            if (meter <= distance) {
                arrCarNear.add(car);
            }
        }
        //trả về danh sách các xe thỏa mãn điều kiện
        return arrCarNear;
    }
}
