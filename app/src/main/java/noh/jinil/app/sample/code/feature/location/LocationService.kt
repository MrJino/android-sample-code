package noh.jinil.app.sample.code.feature.location

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import noh.jinil.app.sample.code.R
import noh.jinil.app.sample.code.TAG
import noh.jinil.app.sample.code.feature.location.client.FusedLocationClient
import noh.jinil.app.sample.code.feature.location.client.LegacyLocationClient
import noh.jinil.app.sample.code.feature.location.client.LocationClient

class LocationService: Service() {

    private val tag = LocationService::class.simpleName

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = FusedLocationClient.create(applicationContext) // LegacyLocationClient.create(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.w(TAG, "$tag onStartCommand() action: ${intent?.action}")
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun start() {
        Log.d(TAG, "$tag start()")
        val notification = NotificationCompat.Builder(this, NOTIFICATION_LOCATION_CHANNEL_ID)
            .setContentTitle("Tracking location...")
            .setContentText("Location: start detecting...")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient.getLocationUpdates(5000L)
            .catch {e ->
                e.printStackTrace()
            }
            .onEach { location ->
                val lat = location.latitude.toString()
                val lng = location.longitude.toString()
                Log.v(TAG, " -> ${location.toLogString()}")
                val updatedNotification = notification.setContentText("Location: ($lat, $lng)")
                notificationManager.notify(1, updatedNotification.build())
            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }


    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}