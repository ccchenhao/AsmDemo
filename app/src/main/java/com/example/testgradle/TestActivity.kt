package com.example.testgradle

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.testgradle.privacy.PrivacyProxy
import com.example.testgradle.view.CircleView
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.internal.operators.single.SingleCreate
import io.reactivex.internal.operators.single.SingleToObservable
import javassist.runtime.Desc
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal
import java.text.DecimalFormat

class TestActivity : AppCompatActivity() {


    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val circleView = findViewById<CircleView>(R.id.circle_view)
        testview.setOnClickListener {

        }
//        if (BaseApplication.agree) {
//            testAsm();
//        } else {
//            System.out.println("not agree11");
//        }
        //插桩前
        val androidID =
            Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
        //插桩后,将上面这个方法替换成
//        PrivacyProxy.privacyRejectMethod("android.provider.Settings$Secure", "getString",
//                        //参数形参Class数组
//            null, Desc.getParams("(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;"),
//            //参数实参值数组
//            Object[]{contentResolver, "android_id"})));

        Log.d("chlog","androidID="+androidID)
        tv.text= changeF1Y(9900)
    }

    private fun getData(): Int {
        throw Throwable("1")
        return 1
    }

    fun testAsm():String{
        return "www"
    }


    override fun onResume() {
        super.onResume()
        Log.d("chlog","111")
    }
    fun changeF1Y(amount: Long): String? {
        val bd: BigDecimal = BigDecimal(amount / 100.0)
        val df = DecimalFormat("0.0") //去除0结尾
        return df.format(bd.setScale(1, BigDecimal.ROUND_DOWN)) //直接截取小数点后一位（不四舍五入)

//        return String .format("%.1f",amount / 100.0)
    }

}