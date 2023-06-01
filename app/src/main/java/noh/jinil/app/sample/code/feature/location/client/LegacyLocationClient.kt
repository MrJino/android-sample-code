package noh.jinil.app.sample.code.feature.location.client

import android.annotation.SuppressLint
import android.content.Context
import android.location.GnssStatus
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import noh.jinil.app.sample.code.TAG
import noh.jinil.app.sample.code.feature.location.hasLocationPermission
import noh.jinil.app.sample.code.feature.location.isLocationProviderEnabled

class LegacyLocationClient(
    private val context: Context,
    private val manager: LocationManager
): LocationClient {

    companion object {
        fun create(context: Context) = LegacyLocationClient(
            context,
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        )
    }

    private val tag = LegacyLocationClient::class.simpleName

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        Log.d(TAG, "$tag getLocationUpdates() interval: $interval")
        return callbackFlow {
            if (!context.hasLocationPermission()) {
                throw LocationClient.LocationException("Missing location permission")
            }
            if (!context.isLocationProviderEnabled()) {
                throw LocationClient.LocationException("Location detectors are disabled")
            }

            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    Log.i(TAG, " => onLocationChanged!!")
                    launch {
                        send(location)
                    }
                }

                override fun onLocationChanged(locations: MutableList<Location>) {
                    super.onLocationChanged(locations)
                }

                override fun onFlushComplete(requestCode: Int) {
                    super.onFlushComplete(requestCode)
                    Log.i(TAG, " => onFlushComplete!!")
                }

                override fun onProviderDisabled(provider: String) {
                    super.onProviderDisabled(provider)
                    Log.i(TAG, " => onProviderDisabled!! $provider")
                }

                override fun onProviderEnabled(provider: String) {
                    super.onProviderEnabled(provider)
                    Log.i(TAG, " => onProviderEnabled!! $provider")
                }
            }

            val gnssStatusCallback = object : GnssStatus.Callback() {
                override fun onFirstFix(ttffMillis: Int) {
                    super.onFirstFix(ttffMillis)
                    Log.i(TAG, " => onFirstFix!!")
                }

                @RequiresApi(Build.VERSION_CODES.R)
                override fun onSatelliteStatusChanged(status: GnssStatus) {
                    super.onSatelliteStatusChanged(status)
//                    Log.i(TAG, " => onSatelliteStatusChanged")
//                    Log.v(TAG, status.toLogString())
                }

                override fun onStarted() {
                    Log.i(TAG, " => onStarted!!")
                    super.onStarted()
                }

                override fun onStopped() {
                    Log.i(TAG, " => onStopped!!")
                    super.onStopped()
                }
            }

            Log.v(TAG, " -> providers: ${manager.allProviders}")

            manager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                interval,
                0f,
                locationListener,
                Looper.getMainLooper()
            )
            manager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                interval,
                0f,
                locationListener,
                Looper.getMainLooper()
            )

            manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.apply {
                Log.v(TAG, "gps location: $this")
            }
            manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)?.apply {
                Log.v(TAG, " -> location(network): $this")
            }

            manager.registerGnssStatusCallback(gnssStatusCallback, Handler(Looper.getMainLooper()))

            awaitClose {
                Log.i(TAG, " => awaitClose")
                manager.removeUpdates(locationListener)
            }
        }
    }
}