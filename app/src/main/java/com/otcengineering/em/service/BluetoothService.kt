package com.otcengineering.em.service

import android.Manifest
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.protobuf.ByteString
import com.otc.alice.api.model.LocationAndSecurity
import com.otc.alice.api.model.ProfileAndSettings
import com.otcengineering.em.data.DashboardData
import com.otcengineering.em.utils.*
import com.otcengineering.otcble.ble.BleSDK
import com.otcengineering.otcble.ble.Status
import com.otcengineering.otcble.utils.Utils
import pub.devrel.easypermissions.EasyPermissions
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class BluetoothService private constructor(ctx: Context) {
    private var data = DashboardData()
    companion object {
        private lateinit var service: BluetoothService
        var onlyConnect = false

        fun getService(ctx: Context) : BluetoothService {
            if (!this::service.isInitialized) {
                service = BluetoothService(ctx)
            }

            return service
        }

        fun checkPermissions(activity: AppCompatActivity): Boolean {
            val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                )
            } else {
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }
            return if (!EasyPermissions.hasPermissions(activity, *perms)) {
                EasyPermissions.requestPermissions(activity, "Bluetooth", 1002, *perms)
                false
            } else {
                true
            }
        }
    }

    enum class ResetBits(val value: Int) {
        AirFilterReset(0x2223),
        EngineOilReset(0x2222),
        FuelFilterReset(0x2221),
        MaintenanceReset(0x2220)
    }

    private var mTimer = Timer()
    init {
        BleSDK.init(ctx)
    }

    private var seconds = 0
    private var scanning = false
    fun executeService() {
        mTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runService()
            }

        }, 0, 1000)
    }

    private var resetBits: Byte = 0
    private fun runService() {
        if (checkConnection()) {
            if (seconds == 0) {
                executeAPI(null) {
                    Common.network.profileApi.putBleConnection(
                        ProfileAndSettings.BleConnection.newBuilder()
                            .setDongleSerialNumber(Common.serialNumber)
                            .setTimestamp(TimeUtils.getTimeForServer())
                            .build()
                    ).execute()
                }
            }
            ++seconds

            if (onlyConnect) {
                return
            }

            BleSDK.readRealTime {
                println("Received Real Time!")
                val data = ByteArray(24)
                // resetBits = it.getByteVar(Constants.RealTimeData.FILTER_FLAGS) ?: 0
                val ch9 = it.getCharacteristic(9) ?: return@readRealTime
                val chA = it.getCharacteristic(10) ?: return@readRealTime
                for (i in 0 until 15) {
                    data[i] = ch9[i]
                }
                for (i in 0 until 9) {
                    data[i + 15] = chA[i]
                }
                setDashboardData(it)
                val location = LocationService.getService().getLocation()

                var cloudRefreshTime = Common.sharedPreferences.getFloat(Constants.Preferences.CLOUD_REFRESH_TIME, 2.0F).toInt()
                if (cloudRefreshTime == 0) {
                    cloudRefreshTime = 2
                    Common.sharedPreferences.putFloat(Constants.Preferences.CLOUD_REFRESH_TIME, 2.0F)
                }
                if (seconds % cloudRefreshTime == 0) {
                    val proto = LocationAndSecurity.DeviceLiveData.newBuilder()
                        .setRawData(ByteString.copyFrom(data))
                        .setSerialNumber(Common.serialNumber)
                        .build()
                    executeAPI(null) {
                        Common.network.locationApi.dongleLiveData(proto).execute().body()
                    }
                }
            }

            val prefs = Common.sharedPreferences
            BleSDK.setFileDownloading(
                !prefs.getBoolean(Constants.Preferences.TRACKLOG_SHOULD_DOWNLOAD),
                !prefs.getBoolean(Constants.Preferences.STATUSLOG_SHOULD_DOWNLOAD),
                !prefs.getBoolean(Constants.Preferences.DIAGNOSISLOG_SHOULD_DOWNLOAD)
            )

            if (seconds % 5 == 0) {
                if (!prefs.getBoolean(Constants.Preferences.TRACKLOG_SHOULD_DOWNLOAD)) {
                    BleSDK.readTracklogs()
                }

                if (!prefs.getBoolean(Constants.Preferences.STATUSLOG_SHOULD_DOWNLOAD)) {
                    BleSDK.readStatuslogs()
                }

                if (!prefs.getBoolean(Constants.Preferences.DIAGNOSISLOG_SHOULD_DOWNLOAD)) {
                    BleSDK.readDiagnosis()
                }
            }

            sendHeartbeat(resetBits)
        } else {
            seconds = 0
        }
    }

    class LogTransfer(val tracklog0: Int, val tracklogN: Int,
                      val statuslog0: Int, val statuslogN: Int,
                      val diagnosislog0: Int, val diagnosislogN: Int)

    fun getLogTransfer(): LogTransfer {
        val status = BleSDK.getStatus()
        val tracklog0 = status.getByteVar(Constants.RealTimeData.TRACKLOG_0)?.toInt() ?: 0
        val statuslog0 = status.getByteVar(Constants.RealTimeData.STATUSLOG_0)?.toInt() ?: 0
        val diagnosislog0 = status.getByteVar(Constants.RealTimeData.DIAGNOSISLOG_0)?.toInt() ?: 0
        var tracklogN = status.getByteVar(Constants.RealTimeData.TRACKLOG_N)?.toInt() ?: 0
        var statuslogN = status.getByteVar(Constants.RealTimeData.STATUSLOG_N)?.toInt() ?: 0
        var diagnosislogN = status.getByteVar(Constants.RealTimeData.DIAGNOSISLOG_N)?.toInt() ?: 0

        if (tracklogN == 127 && tracklog0 == 0) {
            tracklogN = 0
        }

        if (statuslogN == 127 && statuslog0 == 0) {
            statuslogN = 0
        }

        if (diagnosislogN == 127 && diagnosislog0 == 0) {
            diagnosislogN = 0
        }

        return LogTransfer(
            tracklog0,
            tracklogN,
            statuslog0,
            statuslogN,
            diagnosislog0,
            diagnosislogN
        )
    }

    fun sendReset(resetBits: ResetBits) {
        BleSDK.writeMemory(resetBits.value, byteArrayOf(1), false) {

        }
    }

    var lastHeartbeat = ""
    private var heartbeat: Byte = 0
    fun sendHeartbeat(resetBits: Byte) {
        val location = LocationService.getService().getLocation()
        val time = LocalDateTime.now(ZoneOffset.UTC)

        val latitudeBytes = Utils.getInstance().floatToBytes(location.latitude.toFloat())
        val longitudeBytes = Utils.getInstance().floatToBytes(location.longitude.toFloat())

        ++heartbeat

        val data = ByteArray(20)
        data[0] = 0
        data[1] = heartbeat


        latitudeBytes.copyInto(data, 8)
        longitudeBytes.copyInto(data, 12)
        data[18] = resetBits
        lastHeartbeat = Utils.getInstance().byteToString(data)
        try {
            BleSDK.writeCharacteristicRaw("99", data) { writen ->
                Log.d("BluetoothService", "Heartbeat send $writen")
            }
        } catch (exception: Exception) {
            Log.e("BluetoothService", "Exception", exception)
        }
    }

    fun getData(): DashboardData = data

    fun isConnected(): Boolean = BleSDK.isConnected()

    private var flags = 0

    private fun setDashboardData(status: Status) {

        data.speed =  (status.getUIntVar("Speed") ?: 0u).toDouble() / 100
        data.rpm = (status.getUIntVar("Rpm") ?: 0u).toDouble()
        data.soc = (status.getUIntVar("Soc") ?: 0u).toDouble() / 100
        data.optionIndicator = (status.getUIntVar("OptionIndicator") ?: 0u).toInt()
        data.map = (status.getUIntVar("Map") ?: 0u).toInt()
        data.limitationActive = (status.getUIntVar("LimitationActive") ?: 0u).toInt()
        data.error = (status.getUIntVar("LimitationActive") ?: 0u).toInt()

        val location = LocationService.getService().getLocation()
        val time = LocalDateTime.now(ZoneOffset.UTC)

        data.latitude = location.latitude
        data.longitude = location.longitude
        data.date = time.format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss"))
    }

    private var mScanTimer: Timer? = null

    fun clearDTC() {
        BleSDK.writeTag(Constants.MemoryMap.DTC_RESET, byteArrayOf(1)) {}
    }

    fun bluetoothClear() {
        val status = BleSDK.getStatus()
        BleSDK.writeTag(Constants.MemoryMap.STATUSLOG_00, byteArrayOf(status.getByteVar(Constants.RealTimeData.STATUSLOG_N) ?: 0)) {
            BleSDK.writeTag(Constants.MemoryMap.TRACKLOG_00, byteArrayOf(status.getByteVar(Constants.RealTimeData.TRACKLOG_N) ?: 0)) {

            }
        }
    }

    fun bluetoothRemoveSerialNumber() {
        Common.serialNumber = ""
    }

    fun bluetoothDisconnect() {
        BleSDK.disconnect()
    }

    fun getFirmwareVersion(): String? {
        if (!BleSDK.isConnected()) {
            return null
        }

        val status = BleSDK.getStatus()

        val major = status.getIntVar("FwMajor") ?: return null
        val minor = status.getIntVar("FwMinor") ?: return null

        return "${major}.${minor}"
    }

    private var hardcodedTries = 0
    private fun checkConnection() : Boolean {
        if (BleSDK.isConnected()) {
            scanning = false
            return true
        }

        if (com.otcengineering.em.BuildConfig.BLE) {
            if (Constants.HARDCODED_MAC.isNotEmpty()) {
                if (hardcodedTries % 3 == 0) {
                    /*runOnMainThread {
                        Toast.makeText(ctx, "Connecting to ${Constants.HARDCODED_MAC}", Toast.LENGTH_LONG).show()
                    }*/
                    Log.d("BluetoothGatt", "Connecting with hardcoded MAC")
                    BleSDK.connect(Constants.HARDCODED_MAC) {}
                }
                hardcodedTries++
                return false
            }
        }

        if (Common.serialNumber.isEmpty() || Common.macAddress.isEmpty()) {
            if (scanning) {
                runOnMainThread {
                    // BleSDK.stopScanning()
                }
                scanning = false
            }
            return false
        }

        if (!scanning) {
            mScanTimer = Timer()
            mScanTimer?.schedule(object: TimerTask() {
                override fun run() {
                    scanning = false
                    mScanTimer = null
                }

            }, 15000)
            BleSDK.scanDevices { device ->
                //println(device.name)

                val manufacturing = device.scanRecord.manufacturerSpecificData
                if (manufacturing == null || manufacturing.size() == 0) {
                    return@scanDevices
                }
                val data = manufacturing[manufacturing.keyAt(0)]
                val serialNumber = Utils.getInstance().byteToString(data)

                if (serialNumber.uppercase() == Common.serialNumber.uppercase()) {
                    BleSDK.connect(device.mac) {}
                    BleSDK.stopScanning()
                }
            }


            //BleSDK.connect(Common.macAddress) {}
            scanning = true
        }

        return false
    }
}