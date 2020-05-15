package com.colin.face;

import android.app.Application;
import android.app.smdt.SmdtManager;

import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.cache.converter.SerializableDiskConverter;

/**
 * Created by Colin
 * on 2020/4/30
 * E-mail: hecanqi168@gmail.com
 */
public class FaceChekApplication extends Application {

    public volatile static FaceChekApplication mApplication;

    public FaceChekApplication() {
    }

    public static FaceChekApplication getInstance() {
        if (mApplication == null) {
            synchronized (FaceChekApplication.class) {
                if (mApplication == null)
                    mApplication = new FaceChekApplication();
            }
        }
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        EasyHttp.init(this);
        EasyHttp.getInstance()
                .debug("RxEasyHttp", true)
                .setReadTimeOut(60 * 1000)
                .setWriteTimeOut(60 * 1000)
                .setConnectTimeout(60 * 1000)
                .setRetryCount(3)//默认网络不好自动重试3次
                .setRetryDelay(500)//每次延时500ms重试
                .setRetryIncreaseDelay(500)//每次延时叠加500ms
                .setCacheDiskConverter(new SerializableDiskConverter())//默认缓存使用序列化转化
                .setCacheMaxSize(50 * 1024 * 1024)//设置缓存大小为50M
                .setCacheVersion(1)//缓存版本为1
                .setCertificates();//信任所有证书
    }
}
