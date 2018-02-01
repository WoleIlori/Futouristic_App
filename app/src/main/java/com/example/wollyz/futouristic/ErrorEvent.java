package com.example.wollyz.futouristic;

/**
 * Created by Wollyz on 01/02/2018.
 */
public class ErrorEvent {
    private int errorCode;
    private String errorMsg;

    public ErrorEvent(int errorCode, String errorMsg){
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getErrorCode(){
        return errorCode;
    }

    public String getErrorMsg(){
        return errorMsg;
    }
}
