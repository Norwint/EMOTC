package com.otcengineering.em.views.activity

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.otcengineering.em.R
import com.otcengineering.em.databinding.ActivityBleBinding
import com.otcengineering.em.service.BluetoothService
import com.otcengineering.em.service.LocationService
import com.otcengineering.em.utils.CustomTimer
import com.otcengineering.em.utils.runOnMainThread
import com.otcengineering.otcble.ble.BleSDK
import com.otcengineering.otcble.ble.Status
import com.otcengineering.otcble.utils.Utils


class BleActivity : BaseActivity() {
    private val binding: ActivityBleBinding by lazy {
        ActivityBleBinding.inflate(layoutInflater)
    }

    private var timer: CustomTimer? = null
    private var bleTimer: CustomTimer? = null

    private var readingFile = false
    private var restartTime = 0

    private val connectedColor: Int by lazy {
        ContextCompat.getColor(this, R.color.colorBlue)
    }

    private val disconnectedColor: Int by lazy {
        ContextCompat.getColor(this, R.color.colorRed)
    }

    companion object {
        fun newInstance(ctx: Context) = ctx.startActivity(Intent(ctx, BleActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        LocationService.getService(this).getLocation()
        BluetoothService.getService(this).executeService()

        bleTimer = CustomTimer()
        bleTimer?.createAndRun(0, 250) {
            runOnMainThread {
                binding.bleImage.imageTintList = ColorStateList.valueOf(
                    if (BleSDK.isConnected())
                        connectedColor else disconnectedColor
                )
                binding.heartbeat.text = BluetoothService.getService(this).lastHeartbeat

                val status = BleSDK.getStatus()
                var txt = ""

                for (st in status.getConfig().realtimeData) {
                    txt += st.name
                    txt += ": "
                    txt += status.getByteArrayVar(st.name)?.toString()
                    txt += "\n"
                }
                binding.text.text = txt
            }
        }
    }
}