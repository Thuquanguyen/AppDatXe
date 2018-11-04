package com.example.ibct.appdatxe.network.api;

import com.example.ibct.appdatxe.data.BookingResult;
import com.example.ibct.appdatxe.data.CarResult;
import com.example.ibct.appdatxe.network.callback.ApiCallback;
import com.example.ibct.appdatxe.network.callback.getBookingCallBack;
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
    public static void bookingCar(CompositeDisposable compositeDisposable,final ApiCallback apiCallback,double kd_start,double vd_start,double kd_end,double vd_end,float tong_tien,int car_id){
        Http.post(BOOKING_CAR)
                .setCompositeDisposable(compositeDisposable)
                .putParameter("booking[kd_start]",String.valueOf(kd_start))
                .putParameter("booking[vd_start]",String.valueOf(vd_start))
                .putParameter("booking[kd_end]",String.valueOf(kd_end))
                .putParameter("booking[vd_end]",String.valueOf(vd_end))
                .putParameter("booking[tong_tien]",String.valueOf(tong_tien))
                .putParameter("booking[car_id]",String.valueOf(car_id))
                .withCallBack(new Http.CallBack() {
                    @Override
                    public void onSuccess(String data) throws Exception {
                        super.onSuccess(data);
                        BookingResult checkBookingResult = new Gson().fromJson(data,BookingResult.class);
                        if(apiCallback instanceof getBookingCallBack){
                            ((getBookingCallBack) apiCallback).onSuccess(checkBookingResult);
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        super.onFailure(error);
                        if(apiCallback !=null){
                            apiCallback.onFailure(error);
                        }
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        if(apiCallback !=null){
                            apiCallback.onComplete();
                        }
                    }
                }).execute();

    }

}
