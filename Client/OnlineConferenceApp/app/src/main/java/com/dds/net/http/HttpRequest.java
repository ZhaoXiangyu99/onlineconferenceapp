package com.dds.net.http;

import java.io.InputStream;
import java.util.Map;



public interface HttpRequest {

    /**
     * get请求
     */
    void get(String url, Map<String, Object> params, ICallback callback);

    /**
     * post请求
     */
    void post(String url, Map<String, Object> params, ICallback callback);

    /**
     * 设置双向证书
     */
    void setCertificate(InputStream certificate, String pwd);
}
