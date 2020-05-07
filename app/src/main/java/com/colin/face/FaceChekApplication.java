package com.colin.face;

import android.app.Application;
import android.app.smdt.SmdtManager;

/**
 * Created by Colin
 * on 2020/4/30
 * E-mail: hecanqi168@gmail.com
 */
public class FaceChekApplication extends Application {

    public volatile static FaceChekApplication mApplication;

    public static SmdtManager smdt;

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
        smdt = SmdtManager.create(this);
    }
}
