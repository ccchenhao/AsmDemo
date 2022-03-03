package com.example.testgradle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;

import com.example.testgradle.utils.ScreenUtilsKt;
import com.example.testgradle.view.CircleView;

import java.lang.reflect.TypeVariable;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("chlog", "-------> onCreate : " + getClass().getSimpleName());
//        String[] dexFileNames = {"classes1.dex"};
//        Student c = new Student();
//        Log.d("chlog", c.toString());
//        for (String dexFileName : dexFileNames) {
//            DexInstaller.installDexFromAssets(BaseApplication.context, dexFileName);
//        }

        final CircleView circleView = findViewById(R.id.circle_view);
//        circleView.postInvalidate();
        ObjectAnimator.ofFloat(circleView, "radius", ScreenUtilsKt.toPx1(150f)).start();
        final Button showBtn = findViewById(R.id.origin_btn);
        Log.d("chlog", "showBtn=" + showBtn.getClass().getSimpleName());
        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("chlog", "circleView invalidate");
                        circleView.invalidate();
                    }
                }).start();
//                TelephonyManager tm = (TelephonyManager) BaseApplication.context
//                        .getSystemService(Context.TELEPHONY_SERVICE);
//                tm.getSubscriberId();
//                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//                String id = tm.getDeviceId();
            }
        });
        float dp200 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
//        Log.d("chlog", "tm=" + tm);
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
    }
}