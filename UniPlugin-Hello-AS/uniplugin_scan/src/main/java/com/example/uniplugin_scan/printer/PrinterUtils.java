package com.example.uniplugin_scan.printer;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;



import org.apache.commons.lang.StringUtils;

import java.util.Set;

import POSAPI.POSBluetoothAPI;
import POSAPI.POSInterfaceAPI;
import POSSDK.POSSDK;

/**
 * @author kobe
 * @date 2019/5/15
 */
public class PrinterUtils {
    private static volatile PrinterUtils defaultInstance;
    private POSSDK posBlue = null;
    private POSBluetoothAPI bluetoothAPI;
    private POSInterfaceAPI interfaceBlue;
    private String curAddress = null;

    private String testAddress = "00:15:83:03:2D:63";

    public PrinterUtils() {
        Context context = ApplicationAccessor.instance().get();
        bluetoothAPI = POSBluetoothAPI.getInstance(context);
        interfaceBlue = POSBluetoothAPI.getInstance(context);
        if (!bluetoothAPI.isBTSupport()) {
            Toast.makeText(context, "This machine is not support for bluetooth.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 单例
     */
    public static PrinterUtils getDefault() {
        PrinterUtils printerUtils = defaultInstance;
        if (defaultInstance == null) {
            synchronized (PrinterUtils.class) {
                printerUtils = defaultInstance;
                if (defaultInstance == null) {
                    printerUtils = new PrinterUtils();
                    defaultInstance = printerUtils;
                }
            }
        }
        return printerUtils;
    }

    public void stopSearch() {
        if (bluetoothAPI.isBTSupport() && bluetoothAPI.isDiscovery()) {
            bluetoothAPI.cancelDiscovery();
        }
        if (null != interfaceBlue) {
            interfaceBlue = null;
        }
        if (null != posBlue) {
            posBlue = null;
        }
    }

    public Set<BluetoothDevice> getBondedDevices() {
        if (bluetoothAPI.getState() == POSBluetoothAPI.STATE_OFF) {
            return null;
        }
        return bluetoothAPI.getBondedDevices();
    }

    public boolean isConnectPrinter() {
        return (bluetoothAPI.getConnectState() == POSBluetoothAPI.STATE_CONNECTED || bluetoothAPI.getConnectState() == POSBluetoothAPI.STATE_CONNECTING) && StringUtils.isNotEmpty(curAddress);
    }

    public int testPrint(Bitmap bitmap) {
        if (isConnectPrinter()) {
            return printBitmap(bitmap);
        } else {
            int a =  connectDevice(testAddress);
            if (a == 203)
                return printBitmap(bitmap);
            else
                return a + 10000;
        }
    }

    public Boolean testConnect(String address) {
        resetCurAddress();
        return connectDevice(address) == 203 ? true : false;
    }


    /**
     * 连接蓝牙地址
     *
     * @param address
     * @return
     */
    private int connectDevice(String address) {
        stopSearch();

        Context context = ApplicationAccessor.instance().get();
        interfaceBlue = POSBluetoothAPI.getInstance(context);

        if (StringUtils.isEmpty(address)) {
            return POSBluetoothAPI.STATE_NONE;
        }
        // connect devices
        if (address.equalsIgnoreCase(curAddress) && bluetoothAPI.getConnectState() == POSBluetoothAPI.STATE_CONNECTED) {
            return POSBluetoothAPI.STATE_CONNECTED;
        } else {
            if (interfaceBlue.OpenDevice(address) == ConstantDefine.POS_SUCCESS) {
                curAddress = address;
                posBlue = new POSSDK(interfaceBlue);
                return POSBluetoothAPI.STATE_CONNECTED;
            } else {
                interfaceBlue.CloseDevice();
                curAddress = null;
                return POSBluetoothAPI.STATE_NONE;
            }
        }
    }

    /**
     * 打印Bitmap
     *
     * @param bitmap
     * @return
     */
    public int printBitmap(Bitmap bitmap){
        if (isConnectPrinter()) {
            try{
                int result;
                result = posBlue.imageStandardModeRasterPrint(bitmap,600);
                if(result != ConstantDefine.POS_SUCCESS){
                    return result;
                }
                result = posBlue.systemFeedLine(1);
                if(result != ConstantDefine.POS_SUCCESS){
                    return result;
                }
                result = posBlue.systemCutPaper(66, 0);
                return result;
            }catch (Exception e){
                e.printStackTrace();
                return ConstantDefine.ERR_SYSTEM_FEED_LINE;
            }
        } else {
            return ConstantDefine.ERR_NOT_CONNECTED;
        }
    }

    public String getCurAddress() {
        return curAddress;
    }

    public void resetCurAddress() {
        curAddress = null;
    }
}
