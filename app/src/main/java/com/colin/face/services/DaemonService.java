package com.colin.face.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.colin.face.R;
import com.colin.face.server.SimpleServer;
import com.colin.face.util.Contants;

import androidx.annotation.Nullable;
import fi.iki.elonen.NanoHTTPD;


/**
 * 前台Service，使用startForeground
 * 这个Service尽量要轻，不要占用过多的系统资源，否则
 * 系统在资源紧张时，照样会将其杀死
 */
public class DaemonService extends Service {
    private static final String TAG = "DaemonService";
    public static final int NOTICE_ID = 100;
    private String CHANNEL_ONE_ID = "com.primedu.cn";
    private String CHANNEL_ONE_NAME = "Channel One";
    private SimpleServer server;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Contants.DEBUG)
            Log.d(TAG, "DaemonService---->onCreate被调用，启动前台service");

        //如果API大于18，需要弹出一个可见通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("楚越科技");
            builder.setContentText("人脸检测服务运行中...");


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //修改安卓8.1以上系统报错
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_MIN);
                notificationChannel.enableLights(false);
                notificationChannel.setShowBadge(false);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.createNotificationChannel(notificationChannel);
                builder.setChannelId(CHANNEL_ONE_ID);
            }
            startForeground(NOTICE_ID, builder.build());
        } else {
            startForeground(NOTICE_ID, new Notification());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (server == null) {
                server = new SimpleServer(8999);
                server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
                Log.i("数据Httpd：", "The server started.成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("数据Httpd：", "The server started.失败\n" + e);
            sendBroadcast(new Intent(Contants.START_FAILED));
            System.exit(-1);
        }

        // 如果Service被终止
        // 当资源允许情况下，重启service
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (server != null) {
            server.closeAllConnections();
            server.stop();
            server = null;
            Log.i("数据Httpd：", "The server 销毁");
        }

        // 如果Service被杀死，干掉通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mManager.cancel(NOTICE_ID);
        }

        // 重启自己
        Intent intent = new Intent(getApplicationContext(), DaemonService.class);
        startService(intent);
    }
}
