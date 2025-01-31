package com.colin.face;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.colin.face.bean.FaceInfo;
import com.colin.face.services.DaemonService;
import com.colin.face.util.Contants;
import com.colin.face.util.NetWorkUtils;
import com.colin.face.util.PreUtils;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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
    @BindView(R.id.plotId_btn)
    Button plotIdBtn;
    @BindView(R.id.set_plotId)
    EditText setPlotId;
    @BindView(R.id.neiwang)
    RadioButton neiwang;
    @BindView(R.id.gongwang)
    RadioButton gongwang;
    @BindView(R.id.rg_iptype)
    RadioGroup radioGroup;

    private int ipType = 10;  //10内网 20公网

    private String resultMsg = "null";
    private int resultCode;

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

        //启动服务
        Intent intent = new Intent(this, DaemonService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.neiwang:
                        ipType = 10;
                        break;
                    case R.id.gongwang:
                        ipType = 20;
                        break;
                }
            }
        });
    }

    @OnClick({R.id.server_tx, R.id.plotId_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.server_tx:
                break;
            case R.id.plotId_btn:
                if (!TextUtils.isEmpty(setPlotId.getText())) {
                    deviceController();
                } else Toast.makeText(MainActivity.this, "请输入单元ID", Toast.LENGTH_SHORT).show();

                break;
        }
    }

    //设备注册
    public void deviceController() {
        FaceInfo faceInfo = FaceInfo.getInstance();
        faceInfo.setIpAddr(NetWorkUtils.getIpAddress(this));
        faceInfo.setIpType(ipType);
        faceInfo.setMac(NetWorkUtils.getMacAddressFromIp(this));
        faceInfo.setPlotDetailId(Integer.parseInt(setPlotId.getText().toString()));

        PreUtils.setInt(MainActivity.this, Contants.PLOT_ID, Integer.parseInt(setPlotId.getText().toString().trim()));

        Log.d("数据", "IP地址：" + NetWorkUtils.getIpAddress(this));
        Log.d("数据", "MAC地址：" + NetWorkUtils.getMacAddressFromIp(this));
        Log.d("数据", "ipType：" + ipType);
        Log.d("数据", "PlotId：" + Integer.parseInt(setPlotId.getText().toString()));

        EasyHttp
                .post(Contants.DEVICE_CONTROLLER)
                .baseUrl(Contants.BASE_URL)
                .upObject(faceInfo)
                .addConverterFactory(GsonConverterFactory.create())
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        Log.d("数据：访问异常：", e.toString());
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        Log.d("数据：成功", result);
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            resultCode = jsonObject.optInt("resultCode");
                            if (resultCode == 0) resultMsg = "单元ID设置成功！";
                            else
                                resultMsg = jsonObject.optString("errorMsg");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage(resultMsg);
                        builder.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }
}
