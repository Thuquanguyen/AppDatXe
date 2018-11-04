package com.example.ibct.appdatxe.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BookingInfor implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("kd_start")
    @Expose
    private String kd_start;
    @SerializedName("vd_start")
    @Expose
    private String vd_start;
    @SerializedName("kd_end")
    @Expose
    private String kd_end;
    @SerializedName("vd_end")
    @Expose
    private String vd_end;
    @SerializedName("tong_tien")
    @Expose
    private String tong_tien;
    @SerializedName("car_id")
    @Expose
    private String car_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKd_start() {
        return kd_start;
    }

    public void setKd_start(String kd_start) {
        this.kd_start = kd_start;
    }

    public String getVd_start() {
        return vd_start;
    }

    public void setVd_start(String vd_start) {
        this.vd_start = vd_start;
    }

    public String getKd_end() {
        return kd_end;
    }

    public void setKd_end(String kd_end) {
        this.kd_end = kd_end;
    }

    public String getVd_end() {
        return vd_end;
    }

    public void setVd_end(String vd_end) {
        this.vd_end = vd_end;
    }

    public String getTong_tien() {
        return tong_tien;
    }

    public void setTong_tien(String tong_tien) {
        this.tong_tien = tong_tien;
    }

    public String getCar_id() {
        return car_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }
}
