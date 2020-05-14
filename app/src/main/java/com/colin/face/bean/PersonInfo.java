package com.colin.face.bean;

import java.io.Serializable;

/**
 * Created by Colin
 * on 2020/4/29
 * E-mail: hecanqi168@gmail.com
 * Copyright (C) 2018 SSZB, Inc.
 */
public class PersonInfo implements Serializable {
    private static volatile PersonInfo personInfo;
    public String mac;
    public String userId;
    public long checkTime;
    public String name;
    public int type;
    public String cardNo;
    public String idCardNo;
    public String temperature;
    public int mask;
    public String checkPic;
    public String extra;
    public int id;
    public int plotId;
    public int visitId;

    public static PersonInfo getInstance() {
        if (personInfo == null) {
            synchronized (PersonInfo.class) {
                if (personInfo == null) {
                    personInfo = new PersonInfo();
                }
            }
        }
        return personInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlotId() {
        return plotId;
    }

    public void setPlotId(int plotId) {
        this.plotId = plotId;
    }

    public int getVisitId() {
        return visitId;
    }

    public void setVisitId(int visitId) {
        this.visitId = visitId;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(long checkTime) {
        this.checkTime = checkTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
    }

    public String getCheckPic() {
        return checkPic;
    }

    public void setCheckPic(String checkPic) {
        this.checkPic = checkPic;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
