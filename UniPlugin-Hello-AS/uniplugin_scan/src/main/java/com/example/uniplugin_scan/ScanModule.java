package com.example.uniplugin_scan;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.example.uniplugin_scan.printer.Base64Utils;
import com.example.uniplugin_scan.printer.ConstantDefine;
import com.example.uniplugin_scan.printer.DeviceDetail;
import com.example.uniplugin_scan.printer.PrinterUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;

public class ScanModule extends UniModule {

    String TAG = "TestModule";
    public static int REQUEST_CODE = 1000;
    public String response = "";

    @UniJSMethod(uiThread = true)
    public void gotoScanPage(UniJSCallback callback) {
        if(mUniSDKInstance != null && mUniSDKInstance.getContext() instanceof Activity) {
            Intent intent = new Intent(mUniSDKInstance.getContext(), ScanTestActivity.class);
            ((Activity)mUniSDKInstance.getContext()).startActivityForResult(intent, REQUEST_CODE);
        }
        if(callback != null) {
            long start = System.currentTimeMillis();
            long end = start + 20 * 1000;
            while (System.currentTimeMillis() < end) {
                if (response.length() >= 1) {
                    JSONObject data = new JSONObject();
                    data.put("code", response);
                    callback.invoke(data);
                    return;
                }
            }
            JSONObject data = new JSONObject();
            data.put("code", "超时,请重新扫描");
            callback.invoke(data);
            //callback.invokeAndKeepAlive(data);
        }
    }

    @UniJSMethod(uiThread = true)
    public void print(JSONObject options, UniJSCallback callback) {
        Log.e(TAG, "print: 1");
        if(callback != null) {
            if (options.get("code") != null) {
                Context context = this.mUniSDKInstance.getContext();
                String res = printLabel(options.get("code").toString());
                JSONObject data = new JSONObject();
                data.put("res", res);
                data.put("suc", true);
                callback.invoke(data);
            }
            Log.e(TAG, "print: 2");
            //callback.invokeAndKeepAlive(data);
        }
    }

    @UniJSMethod(uiThread = true)
    public void search(UniJSCallback callback) {
        Log.e(TAG, "search: 1");
        if(callback != null) {
            JSONObject data = new JSONObject();
            List<DeviceDetail> res = new ArrayList<>();
            Set<BluetoothDevice> devices = PrinterUtils.getDefault().getBondedDevices();
            if (devices.isEmpty()) {
                res.add(new DeviceDetail(0, "none", "0:0:0:0:0:0"));
            } else {
                int id = 0;
                for (BluetoothDevice device: devices) {
                    res.add(new DeviceDetail(id++, device.getName(), device.getAddress()));
                }
            }
            data.put("res", devices);
            data.put("suc", true);
            callback.invoke(data);

            Log.e(TAG, "search: 2");
            //callback.invokeAndKeepAlive(data);
        }
    }

    @UniJSMethod(uiThread = true)
    public void connect(JSONObject options, UniJSCallback callback) {
        Log.e(TAG, "connect: 1");
        if(callback != null) {
            if (options.get("code") != null) {
                String address = options.get("code").toString();
                JSONObject data = new JSONObject();
                if (address.equals(PrinterUtils.getDefault().getCurAddress())) {
                    data.put("code", 1);
                    data.put("suc", true);
                    data.put("address", address);
                } else {
                    Boolean flag = PrinterUtils.getDefault().testConnect(address);
                    if (flag) {
                        data.put("code", 1);
                        data.put("suc", true);
                        data.put("address", address);
                    } else {
                        data.put("code", 2);
                        data.put("suc", false);
                        data.put("address", address);
                    }
                }
                callback.invoke(data);
            }
            Log.e(TAG, "connect: 2");
            //callback.invokeAndKeepAlive(data);
        }
    }

    @UniJSMethod(uiThread = true)
    public void getAddress(UniJSCallback callback) {
        Log.e(TAG, "get: 1");
        if(callback != null) {
            JSONObject data = new JSONObject();
            List<DeviceDetail> res = new ArrayList<>();
            Set<BluetoothDevice> devices = PrinterUtils.getDefault().getBondedDevices();
            if (devices.isEmpty() || PrinterUtils.getDefault().isConnectPrinter()) {
                data.put("suc", false);
            } else {
                for (BluetoothDevice device: devices) {
                    if (PrinterUtils.getDefault().getCurAddress().equals(device.getAddress())) {
                        data.put("suc", true);
                        data.put("name", device.getName());
                    }
                }
            }
            callback.invoke(data);

            Log.e(TAG, "get: 2");
            //callback.invokeAndKeepAlive(data);
        }
    }

    private static String printLabel(String label){
        int errorCode;
        Bitmap bitmap;
        try {
            bitmap = Base64Utils.base64ToBitmap(label);
        } catch (Exception e) {
            return "base64 wrong";
        }
        if (bitmap == null){
            return "bitmap is null, failed";
        } else {
            //PrinterUtils.getDefault().testConnect(bitmap);
            errorCode = PrinterUtils.getDefault().testPrint(bitmap);
            return "返回值:" + errorCode;

//            if (errorCode == ConstantDefine.ERR_NOT_CONNECTED) {
//                PrinterUtils.getDefault().resetCurAddress();
//                PrinterUtils.getDefault().connectDevice(this);
//            } else if (errorCode != ConstantDefine.POS_SUCCESS) {
//                PrinterUtils.getDefault().resetCurAddress();
//                PrinterUtils.getDefault().connectDevice(this);
//            }
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE && data.hasExtra("respond")) {
            response = data.getStringExtra("respond");
            Log.e("TestModule", "原生页面返回----"+data.getStringExtra("respond"));
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
