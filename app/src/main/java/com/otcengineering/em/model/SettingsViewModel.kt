package com.otcengineering.em.model

import com.otc.alice.api.model.General
import com.otc.alice.api.model.Shared
import com.otc.alice.api.model.Vehicle
import com.otc.alice.api.model.VehicleSettings
import com.otc.alice.api.model.VehicleSettings.GeneralSettings
import com.otc.alice.api.model.VehicleSettings.MapSettings
import com.otc.alice.api.model.VehicleSettings.Settings
import com.otc.alice.api.model.VehicleSettings.SettingsRange
import com.otc.alice.api.model.VehicleSettings.VehicleSettingsData
import com.otc.alice.api.model.VehicleSettings.VehicleSettingsResponse
import com.otc.alice.api.model.Welcome
import com.otcengineering.em.utils.*
import com.otcengineering.otcble.remote.NetworkSDK
import io.reactivex.rxjava3.core.Observable
import java.io.IOException

class SettingsViewModel {

    fun getGeneralSettings(): Observable<VehicleSettingsResponse> {
        return Observable.create { observable ->
            executeAPI(observable) {
                val resp = Common.network.vehicleApi.getVehicleSettings(Common.sharedPreferences.getString(
                    Constants.Preferences.VEHICLE_ID).toLong()).execute().body()
                if (resp == null) {
                    observable.onError(IOException("Response is null"))
                    return@executeAPI
                }

                if (resp.status == Shared.OTCStatus.SUCCESS) {
                    val rsp = resp.data.unpack(VehicleSettingsResponse::class.java)
                    observable.onNext(rsp)
                } else {
                    observable.onError(OtcException(resp.status))
                }
            }
        }
    }

//    fun putGeneralSettings(): Observable<VehicleSettingsResponse> {
//        return Observable.create { observable ->
//
//            val linkVehicle = GeneralSettings.newBuilder()
//                .setSettings()
//                .build()
//
//            executeAPI(observable) {
//                val resp = Common.network.vehicleApi.updateVehicleGeneralSettings(Common.sharedPreferences.getString(
//                    Constants.Preferences.VEHICLE_ID).toLong(), linkVehicle).execute().body()
//                if (resp == null) {
//                    observable.onError(IOException("Response is null"))
//                    return@executeAPI
//                }
//
//                if (resp.status == Shared.OTCStatus.SUCCESS) {
//                    val rsp = resp.data.unpack(VehicleSettingsResponse::class.java)
//                    observable.onNext(rsp)
//                } else {
//                    observable.onError(OtcException(resp.status))
//                }
//            }
//        }
//    }
//
//    fun putMapSettings(map: Long): Observable<VehicleSettingsResponse> {
//        return Observable.create { observable ->
//
//            val linkVehicle = MapSettings.newBuilder()
//                .setSettings()
//                .build()
//
//            executeAPI(observable) {
//                val resp = Common.network.vehicleApi.updateVehicleMapSettings(Common.sharedPreferences.getString(
//                    Constants.Preferences.VEHICLE_ID).toLong(), map, linkVehicle).execute().body()
//                if (resp == null) {
//                    observable.onError(IOException("Response is null"))
//                    return@executeAPI
//                }
//
//                if (resp.status == Shared.OTCStatus.SUCCESS) {
//                    val rsp = resp.data.unpack(VehicleSettingsResponse::class.java)
//                    observable.onNext(rsp)
//                } else {
//                    observable.onError(OtcException(resp.status))
//                }
//            }
//        }
//    }

}