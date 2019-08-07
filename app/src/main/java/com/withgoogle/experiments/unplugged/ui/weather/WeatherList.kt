package com.withgoogle.experiments.unplugged.ui.weather

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.data.integrations.weather.OpenWeatherService
import com.withgoogle.experiments.unplugged.data.integrations.weather.WeatherDataSource
import com.withgoogle.experiments.unplugged.ui.widget.ModuleView
import com.withgoogle.experiments.unplugged.util.bindView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException

@SuppressLint("MissingPermission")
class WeatherList: AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val currentLocationView by bindView<TextView>(R.id.current_location)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        with(findViewById<ModuleView>(R.id.module)) {
            setText("W", "Weather")
            isChecked = true
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            numUpdates = 1
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations){
                Timber.d(location.toString())
            }

            reverseGeocode(locationResult.locations[0])
        }
    }

    private fun reverseGeocode(location: Location) {
        CoroutineScope(Dispatchers.Main).launch {
            val forecasts = withContext(Dispatchers.IO) {
                val geocoder = Geocoder(this@WeatherList)

                var addresses: List<Address> = emptyList()
                try {
                    addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                } catch (ioException: IOException) {
                    Timber.e(ioException, "Service unavailable")
                } catch (illegalArgumentException: IllegalArgumentException) {
                    Timber.e(illegalArgumentException,"Invalid latitude/longitude")
                }

                if (addresses.isEmpty()) {
                    emptyList()
                } else {
                    val address = addresses[0]

                    Timber.d(address.toString())

                    WeatherDataSource.location = "${address.subAdminArea ?: address.adminArea},\n${address.countryName}"

                    withContext(Dispatchers.Main) {
                        currentLocationView.text = WeatherDataSource.location
                    }

                    OpenWeatherService().forecast(WeatherDataSource.location)
                }
            }
            forecasts?.let {
                WeatherDataSource.forecasts.clear()
                WeatherDataSource.forecasts.addAll(it)
            }
        }
    }
}