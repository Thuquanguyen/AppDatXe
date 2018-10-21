package com.example.ibct.appdatxe.data;

import java.io.Serializable;

public class Image implements Serializable {
    private String id;
    private String path;

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
