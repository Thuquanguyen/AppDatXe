package com.example.ibct.appdatxe.Contact;

public class Contact {
    private String hoVaTen;
    private String nhanHieu;
    private String soGhe;
    private String bienSo;
    private String giaThanh;
    private String soSanhGia;
    private String trangThai;
    private String soDienThoai;

    public Contact() {
    }

    public Contact(String hoVaTen, String nhanHieu, String soGhe, String bienSo, String giaThanh, String soSanhGia, String trangThai, String soDienThoai) {
        this.hoVaTen = hoVaTen;
        this.nhanHieu = nhanHieu;
        this.soGhe = soGhe;
        this.bienSo = bienSo;
        this.giaThanh = giaThanh;
        this.soSanhGia = soSanhGia;
        this.trangThai = trangThai;
        this.soDienThoai = soDienThoai;
    }

    public String getHoVaTen() {
        return hoVaTen;
    }

    public void setHoVaTen(String hoVaTen) {
        this.hoVaTen = hoVaTen;
    }

    public String getNhanHieu() {
        return nhanHieu;
    }

    public void setNhanHieu(String nhanHieu) {
        this.nhanHieu = nhanHieu;
    }

    public String getSoGhe() {
        return soGhe;
    }

    public void setSoGhe(String soGhe) {
        this.soGhe = soGhe;
    }

    public String getBienSo() {
        return bienSo;
    }

    public void setBienSo(String bienSo) {
        this.bienSo = bienSo;
    }

    public String getGiaThanh() {
        return giaThanh;
    }

    public void setGiaThanh(String giaThanh) {
        this.giaThanh = giaThanh;
    }

    public String getSoSanhGia() {
        return soSanhGia;
    }

    public void setSoSanhGia(String soSanhGia) {
        this.soSanhGia = soSanhGia;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
}
