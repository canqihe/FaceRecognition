package com.colin.face.server;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.colin.face.FaceChekApplication;
import com.colin.face.bean.PersonInfo;
import com.colin.face.util.Contants;
import com.colin.face.util.PreUtils;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import me.f1reking.serialportlib.SerialPortHelper;
import me.f1reking.serialportlib.entity.DATAB;
import me.f1reking.serialportlib.entity.FLOWCON;
import me.f1reking.serialportlib.entity.PARITY;
import me.f1reking.serialportlib.entity.STOPB;
import me.f1reking.serialportlib.listener.IOpenSerialPortListener;
import me.f1reking.serialportlib.listener.ISerialPortDataListener;
import me.f1reking.serialportlib.listener.Status;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Colin
 * on 2020/4/29
 * E-mail: hecanqi168@gmail.com
 */
public class SimpleServer extends NanoHTTPD {

    private String checkUserId = "0";
    private int checkCount;
    private SerialPortHelper mSerialPortHelper;
    private Context mContext;

    public SimpleServer(int port, Context context) {
        super(port);
        this.mContext = context;
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> files = new HashMap<>();
        try {
            session.parseBody(files);
            String param = files.get("postData");

            PersonInfo personInfo = JSONObject.parseObject(param, PersonInfo.class);
            Log.d("数据", "userId：" + personInfo.getUserId()
                    + " 姓名：" + personInfo.getName()
                    + " 体温：" + personInfo.getTemperature());
            Double personTemperature = Double.parseDouble(personInfo.getTemperature());
            //体温检测
            checkTemperature(personTemperature, personInfo);

        } catch (Exception e) {
            e.printStackTrace();
        }
        String msg = "{\"code\":0,\"message\":\"成功\"}";
        return newFixedLengthResponse(msg);
    }


    /***
     * 体温检测
     * @param personTemperature
     * @param personInfo
     */
    public void checkTemperature(Double personTemperature, PersonInfo personInfo) {
        //温度超标
        if (personTemperature > Contants.TEMPERATURE) {
            //当前ID有过超标记录
            if (personInfo.getUserId().equals(checkUserId)) {
                checkCount = checkCount + 2;
                Log.d("数据，次数-------：", checkCount + "次");
                if (checkCount >= 3) {
                    checkCount = 0;
                    checkUserId = "0";
                    reportInfo(personInfo);
                }
            } else {
                //第一次记录ID
                checkUserId = personInfo.getUserId();
                checkCount = 0;
            }
        } else {
            faceCOntroller(personInfo);
        }
    }

    //体温报警
    public void reportInfo(PersonInfo p) {
        PersonInfo personInfo = PersonInfo.getInstance();
        personInfo.setCheckTime(p.getCheckTime());
        personInfo.setId(p.getId());
        personInfo.setMask(p.getMask());
        personInfo.setName(p.getName());
        personInfo.setPlotId(PreUtils.getInt(mContext, Contants.PLOT_ID, 0));
        personInfo.setTemperature(p.getTemperature());
        personInfo.setUserId(p.getUserId());
        personInfo.setVisitId(p.getVisitId());

        EasyHttp
                .post(Contants.ADD_TEMPRATURE)
                .baseUrl(Contants.BASE_URL)
                .upObject(personInfo)
                .addConverterFactory(GsonConverterFactory.create())
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        Log.d("数据：访问异常：", e.toString());
                    }

                    @Override
                    public void onSuccess(String result) {
                        Log.d("数据：已上报体温预警", result);
                    }
                });
    }


    //人脸控制类
    public void faceCOntroller(PersonInfo p) {
        EasyHttp
                .post(Contants.FACE_CONTROLLER)
                .baseUrl(Contants.BASE_URL)
                .params("userId", p.getUserId())
                .accessToken(true)
                .timeStamp(true)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        Log.d("数据：访问异常：", e.toString());
                    }

                    @Override
                    public void onSuccess(String result) {
                        Log.d("数据：成功", result);
                    }
                });
    }


    /***
     * 发送串口通信
     */
    public void sendPortMsg() {
        if (mSerialPortHelper != null) {
            mSerialPortHelper.sendTxt("");
        }
    }


    /***
     * 打开串口
     */
    public void openPort() {
        mSerialPortHelper = new SerialPortHelper();
        mSerialPortHelper.setPort("/dev/ttys3");
        mSerialPortHelper.setBaudRate(115200);
        mSerialPortHelper.setStopBits(STOPB.getStopBit(STOPB.B1));
        mSerialPortHelper.setDataBits(DATAB.getDataBit(DATAB.CS8));
        mSerialPortHelper.setParity(PARITY.getParity(PARITY.NONE));
        mSerialPortHelper.setFlowCon(FLOWCON.getFlowCon(FLOWCON.NONE));

        mSerialPortHelper.setIOpenSerialPortListener(new IOpenSerialPortListener() {
            @Override
            public void onSuccess(final File device) {
                Toast.makeText(FaceChekApplication.getInstance(), device.getPath() + " :串口打开成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(final File device, final Status status) {
                switch (status) {
                    case NO_READ_WRITE_PERMISSION:
                        Toast.makeText(FaceChekApplication.getInstance(), device.getPath() + " :没有读写权限", Toast.LENGTH_SHORT).show();
                        break;
                    case OPEN_FAIL:
                    default:
                        Toast.makeText(FaceChekApplication.getInstance(), device.getPath() + " :串口打开失败", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });


        mSerialPortHelper.setISerialPortDataListener(new ISerialPortDataListener() {
            //接收数据回调
            @Override
            public void onDataReceived(byte[] bytes) {
                Log.i("数据", "onDataReceived: " + Arrays.toString(bytes));
            }

            //发送数据回调
            @Override
            public void onDataSend(byte[] bytes) {
                Log.i("数据", "onDataSend: " + Arrays.toString(bytes));
            }
        });
        Log.i("数据", "open: " + mSerialPortHelper.open());
    }


    /***
     * 关闭串口
     */
    private void closePort() {
        if (mSerialPortHelper != null) {
            mSerialPortHelper.close();
        }
    }

}
