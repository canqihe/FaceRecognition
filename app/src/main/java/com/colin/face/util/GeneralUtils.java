package com.colin.face.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Colin
 * on 2020/4/30
 * E-mail: hecanqi168@gmail.com
 */
public class GeneralUtils {

    public String transforTime(long time) {
        Date date2 = new Date();
        date2.setTime(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        return simpleDateFormat.format(date2);
    }


    /**
     * 返回当前程序版本
     */
    public static int getAppVersionCode(Context context) {
        int versionCode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
            if (versionCode == 0) {
                return 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 返回当前程序版本
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null) {
                return "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

}
