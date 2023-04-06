package com.otcengineering.em.utils

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.otcengineering.em.R
import com.otcengineering.em.data.DiagnosticData
import com.otcengineering.em.views.components.RecyclerViewAdapter
import id.ionbit.ionalert.IonAlert

fun runOnMainThread(function: () -> Unit) {
    if (Thread.currentThread() == Looper.getMainLooper().thread) {
        try {
            function()
        } catch (ex: Exception) {
            Log.e("RunOnMainThread", "", ex)
        }
    } else {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            try {
                function()
            } catch (ex: Exception) {
                Log.e("RunOnMainThread", "", ex)
            }
        }
    }
}

fun <T: AppCompatActivity> Context.startActivity(activityClass: Class<T>) {
    startActivity(Intent(this, activityClass))
}

fun <T: AppCompatActivity> Context.startActivity(flags: Int, activityClass: Class<T>) {
    val intent = Intent(this, activityClass)
    intent.flags = flags
    startActivity(intent)
}

private var loadingPopup: IonAlert? = null
fun Context.showLoading() {
    runOnMainThread {
        if (loadingPopup != null) {
            dismissLoading()
        }
        loadingPopup = IonAlert(this, IonAlert.PROGRESS_TYPE)
            .showCancelButton(false)
            .setSpinKit("FadingCircle")
        loadingPopup?.show()
    }
}

fun Context.dismissLoading() {
    runOnMainThread {
        loadingPopup?.dismissWithAnimation()
        loadingPopup = null
    }
}

fun runDelayed(delay: Long, callback: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        callback()
    }, delay)
}
