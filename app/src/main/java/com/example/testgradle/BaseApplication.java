package com.example.testgradle;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

public class BaseApplication extends Application {

    public static Context context;
    public static boolean agree=true;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        context=base;

    }
}
