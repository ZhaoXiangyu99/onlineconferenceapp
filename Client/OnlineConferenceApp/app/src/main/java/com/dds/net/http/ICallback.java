package com.dds.net.http;



public interface ICallback {

    void onSuccess(String result);

    void onFailure(int code, Throwable t);
}
