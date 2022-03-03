package com.example.testgradle.privacy;

import android.content.ContentResolver;
import android.util.Log;

import com.example.testgradle.BuildConfig;

public class PrivacyProxy {
//    private static boolean allowVisit = false;
//
//    public static boolean isAllowVisit() {
//        return allowVisit;
//    }
//
//    public static void setAllowVisit(boolean allow) {
//        allowVisit = allow;
//    }

    //    private static boolean invoke = true;
    private static boolean isLog = BuildConfig.DEBUG;

    public static Object privacyRejectMethod(String clzName, String methodName, Object obj, Class[] paramsClasses, Object[] paramsValues) {
        Log.d("chlog","2222");
        if (isLog) {
            Log.d("alvin", "-----------------------------------------");
        }

        Object result = null;
        String mLongName = clzName + "." + methodName;

        try {
//            if (mLongName.equals(PrivacyConfigInApp.getRunningAppProcesses)) {
//
//            } else if (mLongName.equals(PrivacyConfigInApp.getIpAddressByWifiInfo)) {
//                result = Integer.valueOf(0);
//            } else
            if (mLongName.equals(PrivacyConfigInApp.getSubscriberId)
                    || mLongName.equals(PrivacyConfigInApp.getSubscriberIdGemini)) {
                result = "invalid_SubscriberId";
            } else if (mLongName.equals(PrivacyConfigInApp.getDeviceId)) {
                result = "invalid_deviceId";
                Log.d("chlog","3333");
            } else if (mLongName.equals(PrivacyConfigInApp.getImei)) {
                result = "invalid_imei";
            } else if (mLongName.equals(PrivacyConfigInApp.getNai)) {
                result = "invalid_nai";
            } else if (mLongName.equals(PrivacyConfigInApp.getMeid)) {
                result = "invalid_meid";
            } else if (mLongName.equals(PrivacyConfigInApp.Secure_getString)
                    || mLongName.equals(PrivacyConfigInApp.System_getString)) {
                if (paramsValues != null && paramsValues.length == 2 && (paramsValues[1]).equals("android_id")) {
                    result = "111";
                } else {
                    result = PrivacyRefInvoke.invokeStaticMethod(clzName, methodName, paramsClasses, paramsValues);
                }

                if (isLog) {
                    Object param1 = "unknown";
                    if (paramsValues != null && paramsValues.length == 2) {
                        param1 = paramsValues[1];
                    }
                    Log.d("alvin", "SettingGetString param1:(" + param1 + ") result:" + result);
                }
//            } else if (mLongName.equals(PrivacyConfigInApp.getSSID)) {
//                result = "<unknown ssid>";
            } else if (mLongName.equals(PrivacyConfigInApp.getMacAddress)) {
                result = "02:00:00:00:00:00";
            } else if (mLongName.equals(PrivacyConfigInApp.getHardwareAddress)) {
                result = null;
            } else if (mLongName.equals(PrivacyConfigInApp.getSerial)) {
//                result = "unknown1serial";
                result = "unknown";
            }
//            if (isLog) {
            Log.d("alvin", "privacyInvalidValue:" + mLongName + " result:" + result);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Object privacyRejectField(String longName) {

        Object result = null;
        try {
            if (longName.equals(PrivacyConfigInApp.Field_Serial)) {
//                result = "unknown2serial";
                result = "unknown";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    public static String getString(ContentResolver resolver, String name) {
         Log.d("chlog","getString");
        return "444";
    }

    public static void privacyLog(boolean isAllow, String longName) {
        if (isLog) {
            String tag = (isAllow ? "alvinPrivacyAllow" : "alvinPrivacyReject");
            Log.d(tag, Log.getStackTraceString(new Throwable(longName)));
        }
    }


}
