package noh.jinil.app.sample.code.feature.location

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

const val NOTIFICATION_LOCATION_CHANNEL_ID = "location"

class LocationApp: Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).run {
                val channel = NotificationChannel(
                    NOTIFICATION_LOCATION_CHANNEL_ID,
                    "Location",
                    NotificationManager.IMPORTANCE_LOW
                )
                createNotificationChannel(channel)
            }
        }
    }
}