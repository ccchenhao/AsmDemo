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
    public static boolean agree=false;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d("chlog", "111");
        context=base;
       if (true)
           return;
//        try {
//            Thread.currentThread().sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        com.example.baselib.Utils s=new com.example.baselib.Utils();
//        try {
//            getClassLoader().loadClass("com.example.baselib.Utils");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }

        File apkFile = new File(getExternalCacheDir() + "/classes1.dex");
        if (!apkFile.exists()) {
//            try (
//                    //try()这样做可以自动关闭流
//                    //输入流
//                    Source source = Okio.source(getAssets().open("apk/classes1.dex"));
//                    BufferedSink sink = Okio.buffer(Okio.sink(apkFile))
//            ) {
//                sink.writeAll(source);
//            } catch (Exception e) {
//                Log.d("chlog", "error=" + e.getMessage());
//            }
        }
        try {
//                DexClassLoader classLoader = new DexClassLoader(getExternalCacheDir() + "/hotfix.dex", getCacheDir().getPath(), null, null);
//                Class utilsClass = classLoader.loadClass("com.m.libraryapp.hotfix.Utils");
//                Constructor utilsConstructor = utilsClass.getDeclaredConstructors()[0];
//                //这个要写表示外部访问，因为现在外部访问不到了，必须要写true
//                utilsConstructor.setAccessible(true);
//                Object utilsInstance = utilsConstructor.newInstance();
//                //Class utilsClass,根据Class获取方法，不是根据类实例
//                Method showMethod = utilsClass.getDeclaredMethod("show");
//                //这个setAccessible和类没有关系，只要方法是public就可以不写，如果没有修饰符或private就必须写
//                showMethod.setAccessible(true);
//                showMethod.invoke(utilsInstance);


            ClassLoader originalLoader = getClassLoader();
            DexClassLoader classLoader = new DexClassLoader(getExternalCacheDir() + "/classes1.dex", getCacheDir().getPath(), null, null);
            Class loaderClass = BaseDexClassLoader.class;
            Field pathListField = loaderClass.getDeclaredField("pathList");
            pathListField.setAccessible(true);
            Object pathListobject = pathListField.get(classLoader);
            Class pathListClass = pathListobject.getClass();
            Field dexElementsField = pathListClass.getDeclaredField("dexElements");
            dexElementsField.setAccessible(true);
            Object dexElementsobject = dexElementsField.get(pathListobject);
            Object originalPathListobject = pathListField.get(originalLoader);
            //
            Log.d("chlog", "旧的值是" + dexElementsField.get(originalPathListobject));
            Log.d("chlog", "替换值是" + dexElementsobject);
            //全量替换crash有些class找不到，说明有些class并没有被加载，
            //而Android8 pixel上在Activity能替换，而在Android9上失败，但反射已经替换值了， 说明被替换的类已经被load过了，直接拿缓存类了，而不是findClass
//            dexElementsField.set(originalPathListobject, dexElementsobject);

            Object originalDexElementsObject = dexElementsField.get(originalPathListobject);
            int oldLength = Array.getLength(originalDexElementsObject);
            int newLength = Array.getLength(dexElementsobject);
            Object newAllDexElementsObject = Array.newInstance(dexElementsobject.getClass().getComponentType(), oldLength + newLength);
            for (int i = 0; i < newLength; i++) {
                Array.set(newAllDexElementsObject, i, Array.get(dexElementsobject, i));
            }
            for (int i = 0; i < oldLength; i++) {
                Array.set(newAllDexElementsObject, newLength + i, Array.get(originalDexElementsObject, i));
            }
            dexElementsField.set(originalPathListobject, newAllDexElementsObject);
            //新的值
            Log.d("chlog", "新的值是" + dexElementsField.get(originalPathListobject));


//                ClassLoader original=getClassLoader();
//                DexClassLoader classLoader = new DexClassLoader(apkFile.getPath(), getExternalCacheDir().getPath(), null, null);
//                Class baseDexClassLoaderClass = BaseDexClassLoader.class;
//                Field pathListField = baseDexClassLoaderClass.getDeclaredField("pathList");
//                pathListField.setAccessible(true);
//                Object pathListObject = pathListField.get(classLoader);
//                Class pathListClass = pathListObject.getClass();
//                Field dexElementsField = pathListClass.getDeclaredField("dexElements");
//                dexElementsField.setAccessible(true);
//                Object dexElementsObject = dexElementsField.get(pathListObject);
//                Log.d("chlog","dexElementsObject new="+dexElementsObject);
//
////                //注意看这里的classLoader填原来的，找到之前的pathList实例
//                Object originalPathListObject = pathListField.get(original);
//                Log.d("chlog","originalPathListObject ="+originalPathListObject);
//                dexElementsField.set(originalPathListObject, dexElementsObject);

        } catch (Exception e) {
            Log.d("chlog", "error=" + e.getMessage());
            e.printStackTrace();
        }
    }
}
