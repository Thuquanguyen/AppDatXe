package com.example.ibct.appdatxe.network.callback;

import com.example.ibct.appdatxe.data.BookingResult;

public abstract class getBookingCallBack {
    //Phương thức dnayf được gọi khi trả về kết quả danh sách các xe đã đặt thành công
    public abstract void onSuccess(BookingResult bookingResult);
}
