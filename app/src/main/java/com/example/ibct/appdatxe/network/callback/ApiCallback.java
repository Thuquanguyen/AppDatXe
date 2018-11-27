package com.example.ibct.appdatxe.network.callback;

public interface ApiCallback {

    //Phương thức này trả về khi request api bị lỗi
    void onFailure(String error);
    //Phương thức này trả về khi request api thành công
    void onComplete();
}
