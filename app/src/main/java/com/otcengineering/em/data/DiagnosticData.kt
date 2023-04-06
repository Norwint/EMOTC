package com.otcengineering.em.data

import android.graphics.drawable.Drawable
import androidx.databinding.BaseObservable

class DiagnosticData(
var id : Long,
var name: String,
var image: Drawable
) : BaseObservable() {

}