package com.otcengineering.em.utils

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import io.reactivex.rxjava3.core.ObservableEmitter
import java.util.*


private var handlerThread = HandlerThread("backgroundThread")
fun executeAPI(observable: ObservableEmitter<*>?, callback: () -> Unit) {
    if (!handlerThread.isAlive) {
        handlerThread.start()
    }
    val looper = handlerThread.looper
    val handler = Handler(looper)
    handler.post {
        try {
            callback()
        } catch (exc: Exception) {
            observable?.onError(exc)
            Log.e(handlerThread.name, "Exception:", exc)
        }
    }
}

fun getIMEI(): String {
    if (Common.sharedPreferences.contains(Constants.Preferences.IMEI)) {
        return Common.sharedPreferences.getString(Constants.Preferences.IMEI)
    }

    val uuid = UUID.randomUUID()
    Common.sharedPreferences.putString(Constants.Preferences.IMEI, uuid.toString())
    return uuid.toString()
}