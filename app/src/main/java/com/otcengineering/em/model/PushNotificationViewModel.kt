package com.otcengineering.em.model

import android.content.Context
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import com.otc.alice.api.model.ProfileAndSettings
import com.otc.alice.api.model.Shared
import com.otcengineering.em.data.PushNotification
import com.otcengineering.em.utils.Common
import com.otcengineering.em.utils.executeAPI
import com.otcengineering.em.utils.runOnMainThread
import com.otcengineering.otcble.utils.DateUtils
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.atomic.AtomicInteger

class PushNotificationViewModel {
    var routes = ObservableArrayList<PushNotification>()

    fun splitByTokenIgnoringLast(s: String, token: String): String {
        if (!s.contains(token)) {
            return s
        }
        val split: MutableList<String> = s.split(token) as MutableList<String>
        split.removeLast()
        return split.joinToString("").removeSuffix(" ")
    }

    fun splitCamelCase(s: String): String {
        return s.replace(
            String.format(
                "%s|%s|%s",
                "(?<=[A-Z])(?=[A-Z][a-z])",
                "(?<=[^A-Z])(?=[A-Z])",
                "(?<=[A-Za-z])(?=[^A-Za-z])"
            ).toRegex(),
            " "
        )
    }

    fun getNotificationList(ctx: Context, page: Int) {
        executeAPI(null) {
            val resp = Common.network.profileApi.getNotificationList(page).execute().body() ?: return@executeAPI
            if (resp.status == Shared.OTCStatus.SUCCESS) {
                val rsp = resp.data.unpack(ProfileAndSettings.UserNotifications::class.java)
                for (dtc in rsp.notificationsList) {
                    var before = splitByTokenIgnoringLast(dtc.description, "_")
                    val title = dtc.title

                    if (before.contains("EngineOn") || before.contains("PowerOn")) {
                        before = before.replace("EngineOn", "Engine").replace("PowerOn", "Power")
                    }

                    val route = PushNotification(ctx, dtc.id, "Diagnostic Alert", splitCamelCase(before), dtc.timestamp, false)
                    runOnMainThread {
                        routes.add(route)
                    }

                }
            }
        }
    }

}