package com.otcengineering.em.data

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.BaseObservable
import com.otcengineering.em.R
import com.otcengineering.em.utils.TimeUtils

class PushNotification(
    val ctx: Context,
    val id: Long,
    val name: String,
    val description: String,
    val date: String,
    var read: Boolean
) : BaseObservable() {
    var toDelete: Boolean = false
    var selected: Boolean = false

    var expanded = false

    fun parsedDate(): String = TimeUtils.serverTimeParse(date.split(".")[0], "dd/MM/yyyy - HH:mm:ss")

    fun isEditing() = if (expanded) View.VISIBLE else View.GONE

    fun setExpansion(exp: Boolean) {
        expanded = exp
        if (!exp) setSelection(false)
        notifyChange()
    }

    fun toggleSelected() {
        selected = !selected
        notifyChange()
    }

    fun setSelection(sel: Boolean) {
        selected = sel
        notifyChange()
    }

    fun setDeletion(deletion: Boolean) {
        toDelete = deletion
        notifyChange()
    }

    fun toggleDeletion() {
        selected = !selected
        notifyChange()
    }

    fun getTitle(): String = name

    fun getDrawables(): Drawable? {
        return ctx.getDrawable(R.drawable.baseline_notifications_24)
    }

    fun getMessage(): String = description

    fun getColor() = if (read) R.color.gray else R.color.black
}