package com.otcengineering.em.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.otcengineering.em.R
import org.w3c.dom.Text

fun Context.showAlertDialogCancel(positiveButtonText: String, message: String, onPositiveClick: () -> Unit) {
    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
    builder.setMessage(message)
    builder.setCancelable(false)
    builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
    builder.setPositiveButton(positiveButtonText) { dialog, _ ->
        dialog.dismiss()
        onPositiveClick()
    }
    val alert: AlertDialog = builder.create()
    alert.show()
}

fun Context.showAlertDialog(inflater: LayoutInflater, title: String, subTitle: String) {
    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
    val dialogView: View = inflater.inflate(R.layout.alert_layout, null)
    builder.setView(dialogView)
    builder.setCancelable(false)

    val titleText = dialogView.findViewById<TextView>(R.id.title)
    val subTitleText = dialogView.findViewById<TextView>(R.id.subtitle)
    val close = dialogView.findViewById<ConstraintLayout>(R.id.ok)

    titleText.text = title
    subTitleText.text = subTitle


    val alert: AlertDialog = builder.create()
    alert.show()

    close.setOnClickListener {
        alert.dismiss()
    }
}

