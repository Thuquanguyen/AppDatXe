package com.example.ibct.appdatxe.network.api;

public class BaseApi {
    //Link API truy cập vào để quản lý cơ sở dữ liệu các xe
    public static final String BASE_URL= "https://book-car.herokuapp.com/";
    //API lấy danh sách các xe
    public static final String GET_CAR = BASE_URL +"cars";
    //APIi lấy các xe đặt thành công
    public static final String BOOKING_CAR = BASE_URL + "{booking[kd_start]}/{booking[vd_start]}/{booking[kd_end]}/{booking[vd_end]}/{booking[tong_tien]}/{booking[car_id]}";
}
