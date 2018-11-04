package com.example.ibct.appdatxe.network.api;

import com.example.ibct.appdatxe.data.CarResult;
import com.example.ibct.appdatxe.network.callback.ApiCallback;
import com.example.ibct.appdatxe.network.callback.getCarCallBack;
import com.example.ibct.appdatxe.network.http.Http;
import com.google.gson.Gson;

import io.reactivex.disposables.CompositeDisposable;

public class HomeApi extends BaseApi {
    public static void getCar(CompositeDisposable compositeDisposable, final ApiCallback apiCallback){
        Http.get(GET_CAR)
                .setCompositeDisposable(compositeDisposable)
                .withCallBack(new Http.CallBack() {
                    @Override
                    public void onSuccess(String data) throws Exception {
                        super.onSuccess(data);
                        CarResult carResult = new Gson().fromJson(data,CarResult.class);
                        if(apiCallback instanceof getCarCallBack){
                            ((getCarCallBack) apiCallback).onSuccess(carResult);
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        super.onFailure(error);
                        if (apiCallback != null) {
                            apiCallback.onFailure(error);
                        }
                    }
                    @Override
                    public void onComplete() {
                        super.onComplete();
                        if (apiCallback != null)
                            apiCallback.onComplete();
                    }
                }).execute();
    }

}
