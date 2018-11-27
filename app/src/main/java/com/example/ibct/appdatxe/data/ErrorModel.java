package com.example.ibct.appdatxe.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ErrorModel implements Serializable {
    //Khai các các lỗi trả về từ server
    @SerializedName("Message")
    String Message="";
    //Mã lỗi trả về
    @SerializedName("Code")
    String Code = "";

    public ErrorModel(String message, String code) {
        Message = message;
        Code = code;
    }
    //Tạo các phương thức get set cho các thuộc tính
    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }
}
