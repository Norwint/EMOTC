package com.otcengineering.em.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.otcengineering.otcble.remote.MessageService
import com.otcengineering.otcble.remote.Notification
import com.otcengineering.otcble.remote.NotificationBody
import com.otcengineering.em.R
import com.otcengineering.em.utils.Common
import com.otcengineering.em.utils.Constants
import com.otcengineering.otcble.utils.DateUtils

class CloudMessageService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Common.sharedPreferences.putString(Constants.Preferences.FIREBASE_TOKEN, token)
    }

    companion object {
        private const val CHANNELID = "EM"

        fun parseMessage(code: String, onOff: Boolean, ts: String): String {
            return String.format("%s %s at\n%s", code, if (onOff) "ON" else "OFF", DateUtils.serverDateParser(ts, "dd/MM/yyyy - HH:mm:ss"))
        }

        fun setupNotificationChannel(ctx: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNELID, CHANNELID,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                val notificationManager: NotificationManager =
                    ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val msg = message.data["message"] ?: return

        val jsonObject = Gson().fromJson(msg, Notification::class.java)
        val data = Gson().fromJson(jsonObject.data, NotificationBody::class.java)

        val title = MessageService.parseTitle(data.message)
        val body = MessageService.parseMessage(data.message, data.formattedTimestamp())

        val onOff = data.message.contains("On")
        val code = data.message.split("_")[4]
        val ts = data.message.split("_").last()

        showNotification(this, "Diagnostic Alert", parseMessage(code, onOff, ts), data.id)
    }

    private fun showNotification(ctx: Context, title: String, content: String, id: Long) {
        val builder = NotificationCompat.Builder(ctx, CHANNELID)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.icon)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(ctx)) {
            notify(id.toInt(), builder.build())
        }
    }
}