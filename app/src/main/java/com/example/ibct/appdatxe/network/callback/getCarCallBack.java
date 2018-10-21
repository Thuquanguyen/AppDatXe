package com.example.ibct.appdatxe.network.callback;

import com.example.ibct.appdatxe.data.CarResult;

public abstract class getCarCallBack implements ApiCallback {
    public abstract void onSuccess(CarResult carResult);
}
