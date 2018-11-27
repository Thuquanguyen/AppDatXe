package com.example.ibct.appdatxe.network.callback;

import com.example.ibct.appdatxe.data.CarResult;

public abstract class getCarCallBack implements ApiCallback {
    //Phương thức này được gọi khi trả về kết quả danh sách các xe
    public abstract void onSuccess(CarResult carResult);
}
