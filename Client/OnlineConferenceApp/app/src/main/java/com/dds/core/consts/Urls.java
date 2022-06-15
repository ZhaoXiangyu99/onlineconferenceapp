package com.dds.core.consts;

//用于存放用于请求的URL
public class Urls {

    /*private final static String IP = "192.168.2.111";
    public final static String IP = "42.192.40.58:5000";*/
    public final static String IP = "192.168.1.110:5000";

    private final static String HOST = "http://" + IP + "/";

    // 信令地址
    public final static String WS = "ws://" + IP + "/ws";

    // 获取用户列表
    public static String getUserList() {
        return HOST + "userList";
    }

    // 获取房间列表
    public static String getRoomList() {
        return HOST + "roomList";
    }



    //本机IP地址
    public final static String IP_Local = "192.168.1.110";

    //登录注册端口：8085
    public final static String PORT_1 = "8085";

    //登录注册URL  http://192.168.1.109:8085/
    public final static String URL_1 = "http://" + IP_Local + ":" + PORT_1 + "/re_log/";

    //登录
    public static String login(){return URL_1 + "login";}

    //注册
    public static String register(){return  URL_1 + "register";}

    //图片上传下载的端口8085
    public final static String PORT_2 = "8085";

    //图片上传下载URL http://192.168.1.109:8085/
    public final static String URL_2 = "http://" + IP_Local + ":" + PORT_2 + "/";

    //上传图片
    public static String upload() {return URL_2 + "upload";}

    //查询图片
    public static String query(){return URL_2 + "query";}

    //下载图片
    public static String download(){return URL_2 + "download";}



}
