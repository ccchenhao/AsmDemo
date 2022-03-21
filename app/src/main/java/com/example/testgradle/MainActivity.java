package com.example.testgradle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    public static boolean aa = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        Log.d("chlog", "androidID=" + Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Log.d("chlog", "serial=" + Build.getSerial());
//        }
//        Log.d("chlog", "subscriberId=" + tManager.getSubscriberId());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Log.d("chlog", "deviceId=" + tManager.getDeviceId(0));
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Log.d("chlog", "imei=" + tManager.getImei());
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            Log.d("chlog", "imei=" + tManager.getNai());
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Log.d("chlog", "meid=" + tManager.getMeid());
//        }
//        Log.d("chlog", "serialNumber=" + tManager.getSimSerialNumber());
    }

    //就比如这个方法，之前做法为了参数a的值，直接调用一个另一个方法，
    //参数是object数组，然后直接放进去了，但是int是有问题的，Integer就没问题
    public static String test() {
        return "eee";
    }

}