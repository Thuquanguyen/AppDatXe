package com.example.ibct.appdatxe.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ErrorModel implements Serializable {
    @SerializedName("Message")
    String Message="";
    @SerializedName("Code")
    String Code = "";

    public ErrorModel(String message, String code) {
        Message = message;
        Code = code;
    }

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
