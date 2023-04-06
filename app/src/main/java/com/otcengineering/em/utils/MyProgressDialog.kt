package com.otcengineering.white_app.utils

import android.app.ProgressDialog
import android.content.Context

object MyProgressDialog {
    private var pd: ProgressDialog? = null
    fun create(ctx: Context?) {
        pd = ProgressDialog(ctx)
    }

    fun show() {
        pd!!.show()
    }

    fun setCancelable(cancelable: Boolean) {
        pd!!.setCancelable(cancelable)
    }

    fun setTitle(title: String?) {
        pd!!.setTitle(title)
    }

    fun setMessage(mes: String?) {
        pd!!.setMessage(mes)
    }

    fun hide() {
        try {
            if (pd != null) pd!!.hide()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}