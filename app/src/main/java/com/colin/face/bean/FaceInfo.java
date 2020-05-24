package com.colin.face.bean;

import java.io.Serializable;

/**
 * Created by Colin
 * on 2020/5/23
 * E-mail: hecanqi168@gmail.com
 */
public class FaceInfo implements Serializable {


    private static volatile FaceInfo faceInfo;

    public static FaceInfo getInstance() {
        if (faceInfo == null) {
            synchronized (FaceInfo.class) {
                if (faceInfo == null) {
                    faceInfo = new FaceInfo();
                }
            }
        }
        return faceInfo;
    }

    public String ipAddr;
    public int ipType;
    public String mac;
    public int plotDetailId;

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public int getIpType() {
        return ipType;
    }

    public void setIpType(int ipType) {
        this.ipType = ipType;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getPlotDetailId() {
        return plotDetailId;
    }

    public void setPlotDetailId(int plotDetailId) {
        this.plotDetailId = plotDetailId;
    }
}
