package com.otcengineering.em.utils

import android.content.Context
import com.otcengineering.em.BuildConfig
import com.otcengineering.otcble.remote.NetworkSDK
import com.otcengineering.otcble.utils.MySharedPreferences

object Common {
    fun loadColors(ctx: Context) {
//        colorRed = ContextCompat.getColor(ctx, R.color.colorRed)
    }

    fun crashlyticsDetector() {
        // WARNING: L'app fa kaboom si ho fas servir...
        if (BuildConfig.DEBUG) {
            val zero = 1
            for (i in 2 downTo 0) {
                val x = zero / i
            }
        }
    }

    lateinit var network: NetworkSDK
    lateinit var sharedPreferences: MySharedPreferences
    var serialNumber = ""
    var macAddress = ""

    var colorRed = 0
    var colorGreen = 0
    var colorBlue = 0
    var colorOrange = 0
    var colorYellow = 0
}