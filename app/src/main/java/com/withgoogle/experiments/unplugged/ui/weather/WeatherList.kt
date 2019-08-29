package com.withgoogle.experiments.unplugged.ui.weather

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
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

private const val PLACE_REQUEST_CODE = 0x2

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

        findViewById<View>(R.id.finish).setOnClickListener {
            finish()
        }

        currentLocationView.setOnClickListener {
            launchPlacesPicker()
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations){
                Timber.d(location.toString())
            }

            reverseGeocode(locationResult.locations[0].latitude, locationResult.locations[0].longitude)
        }
    }

    private fun launchPlacesPicker() {
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, arrayListOf(Place.Field.LAT_LNG, Place.Field.ADDRESS))
            .build(this)
        startActivityForResult(intent, PLACE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PLACE_REQUEST_CODE) {
                val result = data?.let { Autocomplete.getPlaceFromIntent(it) }
                result?.latLng?.let { location ->
                    reverseGeocode(location.latitude, location.longitude)
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun reverseGeocode(latitude: Double, longitude: Double) {
        CoroutineScope(Dispatchers.Main).launch {
            val forecasts = withContext(Dispatchers.IO) {
                val geocoder = Geocoder(this@WeatherList)

                var addresses: List<Address> = emptyList()
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1)
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