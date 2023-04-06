package com.otcengineering.em.data

import com.otc.alice.api.model.DashboardAndStatus.KnowYourBike
import com.otcengineering.otcble.utils.DateUtils
import org.json.JSONObject

class DashboardData {

    var latitude = 0.0
    var longitude = 0.0
    var date = ""

    var soc = 0.0
    var rpm = 0.0
    var speed = 0.0
    var optionIndicator = 0
    var map = 0
    var limitationActive = 0
    var error = 0
    var tko = false
    var batteryCycle = 0.0
    var sessionTime = 0.0
    var hotBattery = 0.0
    var coldBattery = 0.0
    var lockStatus = false

    companion object {
        fun fromKnowYourBike(kyb: KnowYourBike): DashboardData {
            val json = JSONObject(kyb.vehicleDataRealTime)

            val dd = DashboardData()

            dd.soc = json.getDouble("soc")
            dd.rpm = json.getDouble("rpm")
            dd.speed = json.getDouble("speed")
            dd.optionIndicator = json.getInt("optionIndicator")
            dd.map = json.getInt("map")
            dd.limitationActive = json.getInt("limitationActive")
            dd.error = json.getInt("error")
            dd.batteryCycle = json.getDouble("batteryCycle")
            dd.sessionTime = json.getDouble("sessionTime")
            dd.hotBattery = json.getDouble("hotBattery")
            dd.coldBattery = json.getDouble("coldBattery")
            dd.lockStatus = json.getBoolean("lockStatus")
//            dd.tko = json.getBoolean("error")

//            dd.latitude = json.getDouble("latitude")
//            dd.longitude = json.getDouble("longitude")
//
            dd.date = DateUtils.serverDateParser(json.getString("date"), "dd/MM/yyyy HH:mm:ss")

            return dd
        }
    }
}