package com.example.uniplugin_scan.printer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Base64Utils {

    public static Bitmap base64ToBitmap(String label){
        byte[] decode = Base64.decode(label,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decode,0,decode.length);
    }
}
