package com.colin.face.util;

/**
 * Created by Colin
 * on 2020/4/30
 * E-mail: hecanqi168@gmail.com
 */
public class Contants {

    public static final boolean DEBUG = true;

    public static Double TEMPERATURE = 37.2;//正常体温

    public static String START_SUCCESSFUL = "START_SUCCESSFUL";

    public static String START_FAILED = "START_FAILED";

    public static String PLOT_ID = "PLOT_ID";
    //baseurl
    public static final String BASE_URL = "http://device.titigo.top";
    //提交温度预警
    public static final String ADD_TEMPRATURE = "/elevator-web/base/temperature/add";
    //设备控制器类
    public static final String DEVICE_CONTROLLER = "/elevator-web/device/regist";
    //人脸控制器类
    public static final String FACE_CONTROLLER = "/elevator-web/base/face/call";
}
