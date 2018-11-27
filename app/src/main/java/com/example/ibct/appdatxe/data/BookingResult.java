package com.example.ibct.appdatxe.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BookingResult implements Serializable {
    //Tạo phương thức và thuộc tính thể hiệ việc đặt xe thành công
    @SerializedName("error")
    @Expose
    private boolean error;
    @SerializedName("message")
    @Expose
    private boolean message;
    @SerializedName("data")
    @Expose
    private List<BookingInfor> data = null;
    //Tạo các phương thức get set cho các thuộc tính
    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isMessage() {
        return message;
    }

    public void setMessage(boolean message) {
        this.message = message;
    }

    public List<BookingInfor> getData() {
        return data;
    }

    public void setData(List<BookingInfor> data) {
        this.data = data;
    }
}
