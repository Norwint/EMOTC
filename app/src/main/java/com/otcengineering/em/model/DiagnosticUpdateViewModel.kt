package com.otcengineering.em.model

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import com.otc.alice.api.model.DashboardAndStatus
import com.otc.alice.api.model.DashboardAndStatus.DtcList
import com.otc.alice.api.model.General
import com.otc.alice.api.model.ProfileAndSettings
import com.otc.alice.api.model.Shared
import com.otcengineering.em.R
import com.otcengineering.em.data.DiagnosticData
import com.otcengineering.em.utils.Common
import com.otcengineering.em.utils.Constants
import com.otcengineering.em.utils.executeAPI
import com.otcengineering.em.utils.runOnMainThread
import io.reactivex.rxjava3.core.Observable
import okhttp3.internal.format
import java.io.IOException

class DiagnosticUpdateViewModel() {
    val controll = ObservableArrayList<DiagnosticData>()
    val battery = ObservableArrayList<DiagnosticData>()

    fun getDTC(ctx: Context): Observable<DtcList> {
        return Observable.create { observable ->
            executeAPI(observable) {
                val resp = Common.network.dashboardApi.getDTCHistory(Common.sharedPreferences.getString(
                    Constants.Preferences.VEHICLE_ID).toLong(), "en").execute().body()
                if (resp == null) {
                    observable.onError(IOException("Response is null"))
                    return@executeAPI
                }

                if (resp.status == Shared.OTCStatus.SUCCESS) {
                    val rsp = resp.data.unpack(DtcList::class.java)
                    if (rsp.batteryCount == 0){
                        battery.add(ctx.getDrawable(R.drawable.done)
                            ?.let { DiagnosticData(1, "All good here", it) })
                    } else {
                        for (dtc in rsp.batteryList) {
                            fun image() : Drawable? {
                                return if (dtc.dtcState == General.DtcState.WARNING) {
                                    ctx.getDrawable(R.drawable.triangle_red)
                                } else {
                                    ctx.getDrawable(R.drawable.triangle)
                                }
                            }

                            val batteries = image()?.let { DiagnosticData(dtc.id, dtc.code + " - " + dtc.description, it) }
                            runOnMainThread {
                                battery.add(batteries)
                            }
                        }
                    }
                    if (rsp.controllerCount == 0){
                        controll.add(ctx.getDrawable(R.drawable.done)
                            ?.let { DiagnosticData(1, "All good here", it) })
                    } else {
                        for (controller in rsp.controllerList) {
                            fun image() : Drawable? {
                                return if (controller.dtcState == General.DtcState.WARNING) {
                                    ctx.getDrawable(R.drawable.triangle_red)
                                } else {
                                    ctx.getDrawable(R.drawable.triangle)
                                }
                            }

                            val controllers = image()?.let { DiagnosticData(controller.id, controller.code + " - " + controller.description, it) }
                            runOnMainThread {
                                controll.add(controllers)
                            }
                        }
                    }
                    observable.onNext(rsp)
                } else {
                    observable.onError(OtcException(resp.status))
                }
            }
        }
    }


}