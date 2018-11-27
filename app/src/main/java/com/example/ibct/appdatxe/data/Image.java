package com.example.ibct.appdatxe.data;

import java.io.Serializable;

public class Image implements Serializable {
    //Khai báo các thuộc tính của hình ảnh tài xế
    //Gồm có id của ảnh
    private String id;
    //Đường dẫn ảnh
    private String path;

    //Tạo các phương thức get set cho các thuộc tính
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
