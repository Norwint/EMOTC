package com.otcengineering.em.utils

object Constants {
    const val BASE_URL = "http://em-dev.otc010.com:80"
    const val HARDCODED_MAC = "00:60:37:71:0A:1B"
    const val EXTRA_DTC_ID = "DTCID"

    object Preferences {
        const val VEHICLE_ID = "VEHICLE_ID"
        const val PHONE_NUMBER = "PHONE_NUMBER"
        const val ERROR = "ERROR"
        const val ERROR2 = "ERROR"
        const val PASSWORD = "PASSWORD"
        const val IMEI = "IMEI"
        const val OTPCHECK = "OTPCHECK"
        const val MODEL = "MODEL"
        const val COUNTRY = "COUNTRY"
        const val COUNTRYID = "COUNTRYID"
        const val UNIT = "km/h"
        const val SERIAL_NUMBER = "SerialNumber"
        const val VIN = "Vin"
        const val FIREBASE_TOKEN = "FirebaseToken"
        const val TOKEN = "TOKEN"
        const val TOTAL_PAGES = 1
        const val CURRENT_PAGE = 1
        const val STATUSLOG_SHOULD_DOWNLOAD = "StatuslogShouldDownload"
        const val DIAGNOSISLOG_SHOULD_DOWNLOAD = "DiagnosislogShouldDownload"
        const val TRACKLOG_SHOULD_DOWNLOAD = "TracklogShouldDownload"
        const val CLOUD_REFRESH_TIME = "CloudRefreshTime"
        const val GENERAL_SETTINGS = "GENERAL_SETTINGS"
        const val URL = "URL"
    }

    object RealTimeData {
        const val TRACKLOG_0 = "Tracklog0"
        const val TRACKLOG_N = "TracklogN"
        const val SERIAL_NUMBER = "SERIAL_NUMBER"
        const val FILTER_FLAGS = "FilterAck"
        const val STATUSLOG_0 = "Statuslog0"
        const val STATUSLOG_N = "StatuslogN"
        const val DIAGNOSISLOG_0 = "Diagnosis0"
        const val DIAGNOSISLOG_N = "DiagnosisN"
        const val COUNTRYID = "0"
    }

    object MemoryMap {
        const val STATUSLOG_PTR = "STATUSLOG_PTR"
        const val STATUSLOG_00 = "STATUSLOG_00"
        const val TRACKLOG_PTR = "TRACKLOG_PTR"
        const val TRACKLOG_00 = "TRACKLOG_00"
        const val DTC_RESET = "BackendAction"
    }

    object Legal {
        const val NUM = "0"
        const val NUMTLF = "0"
    }
}