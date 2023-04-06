package com.otcengineering.em

import android.app.Application
import com.otcengineering.em.service.CloudMessageService
import com.otcengineering.em.utils.Common
import com.otcengineering.em.utils.Constants.BASE_URL
import com.otcengineering.otcble.ble.BleSDK
import com.otcengineering.otcble.remote.NetworkSDK
import com.otcengineering.otcble.utils.MySharedPreferences

class EmApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        BleSDK.init(this)
        NetworkSDK.BASE_URL = BASE_URL
        Common.network = NetworkSDK(this, BASE_URL)
        Common.sharedPreferences = MySharedPreferences.create(this)

        CloudMessageService.setupNotificationChannel(this)
    }
}