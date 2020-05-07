package com.colin.face.server;

import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.colin.face.FaceChekApplication;
import com.colin.face.bean.PersonInfo;
import com.colin.face.util.Contants;

import java.io.File;
import java.io.IOException;
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

/**
 * Created by Colin
 * on 2020/4/29
 * E-mail: hecanqi168@gmail.com
 */
public class SimpleServer extends NanoHTTPD {

    private String checkUserId = "0";
    private int checkCount;
    private SerialPortHelper mSerialPortHelper;

    public SimpleServer(int port) {
        super(port);
    }

    public static void main(String[] args) {
        try {
            new SimpleServer(8999).start(10000, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response serve(IHTTPSession session) {

        Map<String, String> files = new HashMap<>();
        try {
            session.parseBody(files);
            String param = files.get("postData");
            /*Log.d("数据：", "header : " + session.getHeaders());
            Log.d("数据：", "files : " + session.getParms());
            Log.d("数据：", "param : " + param);*/

            PersonInfo personInfo = JSONObject.parseObject(param, PersonInfo.class);
            Log.d("数据，userId：", personInfo.getUserId());
            Log.d("数据，姓名：", personInfo.getName());
            Log.d("数据，体温：", personInfo.getTemperature());
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
        if (personTemperature > Contants.TEMPERATURE) {
            //当前ID有过超标记录
            if (personInfo.getUserId().equals(checkUserId)) {
                checkCount = checkCount + 2;
                Log.d("数据，次数---------：", checkCount + "次");
                if (checkCount >= 3) {
                    checkCount = 0;
                    checkUserId = "0";
                    Log.d("数据：", "New coronavirus waring！！！！！！！！！！");
                }
            } else {
                //第一次记录ID
                checkUserId = personInfo.getUserId();
                checkCount = 0;
            }
        }
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
            @Override
            public void onDataReceived(byte[] bytes) {
                Log.i("数据", "onDataReceived: " + Arrays.toString(bytes));
            }

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
