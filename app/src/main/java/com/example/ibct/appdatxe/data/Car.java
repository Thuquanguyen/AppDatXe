package com.example.ibct.appdatxe.data;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Car implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("ten_xe")
    @Expose
    private String tenXe;
    @SerializedName("ten_tai_xe")
    @Expose
    private String tenTaiXe;
    @SerializedName("bien_so")
    @Expose
    private String bienSo;
    @SerializedName("gia_tien")
    @Expose
    private String giaTien;
    @SerializedName("hang_xe")
    @Expose
    private String hangXe;
    @SerializedName("sdt")
    @Expose
    private String phone;
    @SerializedName("kinh_do")
    @Expose
    private double kinhDo;
    @SerializedName("vi_do")
    @Expose
    private double viDo;
    @SerializedName("trang_thai")
    @Expose
    private boolean trangThai;
    @SerializedName("images")
    @Expose
    private String arrImage;

    public Car() {
    }

    public Car(String id, String tenXe, String tenTaiXe, String bienSo, String giaTien, String hangXe, String phone, double kinhDo, double viDo, boolean trangThai, String arrImage) {
        this.id = id;
        this.tenXe = tenXe;
        this.tenTaiXe = tenTaiXe;
        this.bienSo = bienSo;
        this.giaTien = giaTien;
        this.hangXe = hangXe;
        this.phone = phone;
        this.kinhDo = kinhDo;
        this.viDo = viDo;
        this.trangThai = trangThai;
        this.arrImage = arrImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenXe() {
        return tenXe;
    }

    public void setTenXe(String tenXe) {
        this.tenXe = tenXe;
    }

    public String getTenTaiXe() {
        return tenTaiXe;
    }

    public void setTenTaiXe(String tenTaiXe) {
        this.tenTaiXe = tenTaiXe;
    }

    public String getBienSo() {
        return bienSo;
    }

    public void setBienSo(String bienSo) {
        this.bienSo = bienSo;
    }

    public String getGiaTien() {
        return giaTien;
    }

    public void setGiaTien(String giaTien) {
        this.giaTien = giaTien;
    }

    public String getHangXe() {
        return hangXe;
    }

    public void setHangXe(String hangXe) {
        this.hangXe = hangXe;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getKinhDo() {
        return kinhDo;
    }

    public void setKinhDo(float kinhDo) {
        this.kinhDo = kinhDo;
    }

    public double getViDo() {
        return viDo;
    }

    public void setViDo(float viDo) {
        this.viDo = viDo;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    public String getArrImage() {
        return arrImage;
    }

    public void setArrImage(String arrImage) {
        this.arrImage = arrImage;
    }
}
