package com.colin.face;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.colin.face.bean.PersonInfo;
import com.colin.face.services.DaemonService;
import com.colin.face.util.Constant;
import com.colin.face.util.Contants;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.server_tx)
    TextView serverTx;
    @BindView(R.id.device_info)
    TextView deviceInfo;

    private static final int EXTERNAL_STORAGE = 7;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Contants.START_FAILED))
                serverTx.setTextColor(Color.BLACK);
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Contants.START_FAILED);
        registerReceiver(broadcastReceiver, intentFilter);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        permissionStorage();//权限管理

        Intent intent = new Intent(this, DaemonService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }


    @OnClick({R.id.server_tx})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.server_tx:
                getDeviceInfo();
                break;
        }
    }

    public void getDeviceInfo() {
//        StringBuffer sb = new StringBuffer();
//        try {
//            sb.append("系统信息：" + FaceChekApplication.getInstance().smdt.smdtGetAPIVersion() + "\n");
//            sb.append("设备硬件存储大小：" + FaceChekApplication.getInstance().smdt.getInternalStorageMemory() + "\n");
//            sb.append("设备以太网IP地址：" + FaceChekApplication.getInstance().smdt.smdtGetEthIPAddress() + "\n");
//            sb.append("设备SD卡路径：" + FaceChekApplication.getInstance().smdt.smdtGetSDcardPath(this) + "\n");
//        } catch (Exception e) {
//            sb.append(e.toString());
//            e.printStackTrace();
//        }
//        deviceInfo.setText(sb.toString());

        PersonInfo personInfo = PersonInfo.getInstance();
        personInfo.setCheckTime(System.currentTimeMillis());
        personInfo.setId(2);
        personInfo.setMask(1);
        personInfo.setName("王小五");
        personInfo.setPlotId(4);
        personInfo.setTemperature("36.8");
        personInfo.setUserId("3");
        personInfo.setVisitId(1);

        EasyHttp
                .post(Constant.ADD_TEMPRATURE)
                .baseUrl(Constant.BASE_URL)
                .upObject(personInfo)
                .addConverterFactory(GsonConverterFactory.create())
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        Toast.makeText(MainActivity.this, "访问异常", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(String result) {
                        Log.d("数据：", result);
                    }
                });

    }

    /*private void permissionStorage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 8);
        }
    }

    //权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case EXTERNAL_STORAGE: {
                permissionStorage();
                return;
            }
        }
    }*/


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }
}
