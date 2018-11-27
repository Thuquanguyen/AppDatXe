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
    //Phương thức này dùng để lấy tất cả danh scsh các xe
    public static void getCar(CompositeDisposable compositeDisposable, final ApiCallback apiCallback) {
        //Gọi tới API lấy danh sách xe
        Http.get(GET_CAR)
                .setCompositeDisposable(compositeDisposable)
                //Thực hiện lấy danh sách xe trong hàm CallBack
                .withCallBack(new Http.CallBack() {
                    @Override
                    //Dữ liệu trả về trong hàm ónuccess
                    public void onSuccess(String data) throws Exception {
                        super.onSuccess(data);
                        CarResult carResult = new Gson().fromJson(data, CarResult.class);
                        if (apiCallback instanceof getCarCallBack) {
                            ((getCarCallBack) apiCallback).onSuccess(carResult);
                        }
                    }

                    //Nếu không có dữ liệu trả về nó sẽ vào hàm onFailure
                    @Override
                    public void onFailure(String error) {
                        super.onFailure(error);
                        if (apiCallback != null) {
                            apiCallback.onFailure(error);
                        }
                    }

                    //Khi lấy dữ liệu hoàn tất sẽ vào hàm Complate
                    @Override
                    public void onComplete() {
                        super.onComplete();
                        if (apiCallback != null)
                            apiCallback.onComplete();
                    }
                }).execute();
    }

    //Phương thức lấy danhb sách các xe đã được đặt thành công tương tự như phương thức trên
    public static void bookingCar(CompositeDisposable compositeDisposable, final ApiCallback apiCallback, double kd_start, double vd_start, double kd_end, double vd_end, float tong_tien, int car_id) {
        Http.post(BOOKING_CAR)
                .setCompositeDisposable(compositeDisposable)
                .putParameter("booking[kd_start]", String.valueOf(kd_start))
                .putParameter("booking[vd_start]", String.valueOf(vd_start))
                .putParameter("booking[kd_end]", String.valueOf(kd_end))
                .putParameter("booking[vd_end]", String.valueOf(vd_end))
                .putParameter("booking[tong_tien]", String.valueOf(tong_tien))
                .putParameter("booking[car_id]", String.valueOf(car_id))
                .withCallBack(new Http.CallBack() {
                    @Override
                    public void onSuccess(String data) throws Exception {
                        super.onSuccess(data);
                        BookingResult checkBookingResult = new Gson().fromJson(data, BookingResult.class);
                        if (apiCallback instanceof getBookingCallBack) {
                            ((getBookingCallBack) apiCallback).onSuccess(checkBookingResult);
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
                        if (apiCallback != null) {
                            apiCallback.onComplete();
                        }
                    }
                }).execute();

    }

}
