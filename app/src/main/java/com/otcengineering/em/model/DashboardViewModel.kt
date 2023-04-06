package com.otcengineering.em.model

import com.otc.alice.api.model.DashboardAndStatus
import com.otc.alice.api.model.Shared
import com.otc.alice.api.model.Vehicle
import com.otcengineering.em.data.DashboardData
import com.otcengineering.em.data.DashboardDataJoin
import com.otcengineering.em.utils.Common
import com.otcengineering.em.utils.Constants
import com.otcengineering.em.utils.executeAPI
import com.otcengineering.otcble.remote.NetworkSDK
import io.reactivex.rxjava3.core.Observable
import java.io.IOException

class DashboardViewModel {
    fun getDashboardData(): Observable<DashboardDataJoin> {
        return Observable.create { observable ->
            executeAPI(observable) {
                val resp = Common.network.dashboardApi.knowYourBike(Common.sharedPreferences.getString(
                    Constants.Preferences.VEHICLE_ID).toLong()).execute().body()
                if (resp == null) {
                    observable.onError(IOException("Response is null"))
                    return@executeAPI
                }

                if (resp.status == Shared.OTCStatus.SUCCESS) {
                    val rsp = resp.data.unpack(DashboardAndStatus.KnowYourBike::class.java)
                    val dd = DashboardData.fromKnowYourBike(rsp)

                    observable.onNext(DashboardDataJoin(dd))
                } else {
                    observable.onError(OtcException(resp.status))
                }
            }
        }
    }
    fun getGeneralDashboard(): Observable<Vehicle.VehicleData> {
        return Observable.create { observable ->
            executeAPI(observable) {
                val response = Common.network.vehicleApi.getVehicles().execute().body()

                if (response == null) {
                    observable.onError(IOException("Cannot execute API"))
                    return@executeAPI
                }

                if (response.status == Shared.OTCStatus.SUCCESS) {
                    val rsp = response.data.unpack(Vehicle.VehicleData::class.java)
                    observable.onNext(rsp)
                    rsp.vin
                } else {
                    observable.onError(OtcException(response.status))
                }
            }
        }
    }
}