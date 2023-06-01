package noh.jinil.app.sample.code.feature.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.GnssStatus
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import noh.jinil.app.sample.code.TAG

fun Context.hasLocationPermission(): Boolean {
    val coarsePermission = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val finePermission = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return  coarsePermission && finePermission
}

fun Context.isLocationProviderEnabled(): Boolean {
    (getSystemService(Context.LOCATION_SERVICE) as LocationManager).run {
        if (
            !isProviderEnabled(LocationManager.GPS_PROVIDER) &&
            !isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        ) {
            return false
        }
    }
    return true
}

@RequiresApi(Build.VERSION_CODES.R)
fun GnssStatus.toLogString(): String {
    val builder = StringBuilder()
    builder.appendLine("------------------------------------------------")
    repeat(satelliteCount) { index ->
        builder.append("| AzimuthDegrees: ${getAzimuthDegrees(index)}")
        builder.append("| Svid: ${getSvid(index)}")
        builder.append("| AzimuthDegrees: ${getAzimuthDegrees(index)}")
        builder.append("| Cn0DbHz: ${getCn0DbHz(index)}")
        builder.append("| ConstellationType: ${getConstellationType(index)}")
        builder.append("| ElevationDegrees: ${getElevationDegrees(index)}")
        builder.append("| usedInFix: ${usedInFix(index)}")
        if (hasBasebandCn0DbHz(0)) {
            builder.append("| BasebandCn0DbHz: ${getBasebandCn0DbHz(index)}")
        }
        if (hasAlmanacData(0)) {
            builder.append("| AlmanacData: ${hasAlmanacData(index)}")
        }
        if (hasEphemerisData(0)) {
            builder.append("| EphemerisData: ${hasEphemerisData(index)}")
        }
        if (hasCarrierFrequencyHz(0)) {
            builder.append("| CarrierFrequencyHz: ${getCarrierFrequencyHz(index)}")
        }
        builder.appendLine()
    }
    builder.appendLine("------------------------------------------------")
    return builder.toString()
}

fun Location.toLogString(): String {
    val builder = StringBuilder()
    builder.appendLine("location!!")
    builder.appendLine("------------------------------------------------")
    builder.appendLine("| (${this.latitude}, ${this.longitude})")
    builder.appendLine("| provider: ${this.provider}")
    builder.appendLine("| accuracy: ${this.accuracy}")
    builder.appendLine("| altitude: ${this.altitude}")
    if (hasBearing()) {
        builder.appendLine("| bearing: ${this.bearing}")
    }
    if (hasSpeed()) {
        builder.appendLine("| speed: ${this.speed}")
    }
    builder.appendLine("| time: ${this.time}")
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
        builder.appendLine("| speedAccuracyMetersPerSecond: ${this.speedAccuracyMetersPerSecond}")
        if (this.hasBearingAccuracy()) {
            builder.appendLine("| bearingAccuracyDegrees: ${this.bearingAccuracyDegrees}")
        }
    }
    builder.appendLine("------------------------------------------------")
    return builder.toString()
}