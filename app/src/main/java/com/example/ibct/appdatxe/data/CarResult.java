package com.example.ibct.appdatxe.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CarResult implements Serializable {
    @SerializedName("error")
    @Expose
    private boolean error;
    @SerializedName("data")
    @Expose
    //Khở tạo mảng danh sách các xe
    private List<Car> data = null;
    //Tạo các phương thức get set cho các thuộc tính
    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<Car> getData() {
        return data;
    }

    public void setData(List<Car> data) {
        this.data = data;
    }
}
