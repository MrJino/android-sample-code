package noh.jinil.app.sample.code

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import noh.jinil.app.sample.code.feature.location.LocationService
import noh.jinil.app.sample.code.ui.theme.AndroidSampleCodeTheme

class MainActivity : ComponentActivity() {

    private val tag = MainActivity::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "$tag onCreate()")
        ActivityCompat.requestPermissions(this, getPermissionNeeds(), 0)
        setContent {
            AndroidSampleCodeTheme {
                Column(modifier = Modifier.fillMaxSize()) {
                    Button(onClick = {
                        val intent = Intent(applicationContext, LocationService::class.java).apply {
                            action = LocationService.ACTION_START
                        }
                        startService(intent)
                    }) {
                        Text(text = "Start")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        val intent = Intent(applicationContext, LocationService::class.java).apply {
                            action = LocationService.ACTION_STOP
                        }
                        startService(intent)
                    }) {
                        Text(text = "Stop")
                    }
                }
            }
        }
    }

    private fun getPermissionNeeds() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS,
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }
}