package com.withgoogle.experiments.unplugged.ui.maps

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.withgoogle.experiments.unplugged.ui.widget.ModuleView
import com.withgoogle.experiments.unplugged.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteActivity
import android.app.Activity
import com.withgoogle.experiments.unplugged.model.Location
import com.withgoogle.experiments.unplugged.ui.AppState
import com.withgoogle.experiments.unplugged.util.bindView

class MapsActivity: AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val originView by bindView<TextView>(R.id.current_location)
    private val destinationView by bindView<TextView>(R.id.destination_location)

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        with(findViewById<ModuleView>(R.id.module)) {
            setText("M", "Maps")
            isChecked = true
        }

        destinationView.setOnClickListener {
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, arrayListOf(Place.Field.LAT_LNG, Place.Field.ADDRESS))
                .build(this)
            startActivityForResult(intent, 2)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            numUpdates = 1
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

        val destination = AppState.destination.value

        if (destination != null) {
            reverseGeocode(destination, destinationView)
        } else {
            destinationView.text = "Tap here to search for a place"
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations){
                Timber.d(location.toString())
            }

            val location = locationResult.locations[0]

            AppState.origin.value = Location(location.latitude, location.longitude)

            reverseGeocode(Location(location.latitude, location.longitude), originView)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data?.let { Autocomplete.getPlaceFromIntent(it) }
                result?.let { place ->
                    AppState.destination.value = place.latLng?.let { Location(it.latitude, it.longitude) }
                    destinationView.text = "${place.address}"
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = data?.let { Autocomplete.getStatusFromIntent(it) }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun reverseGeocode(location: Location, target: TextView) {
        CoroutineScope(Dispatchers.Main).launch {
            val address = withContext(Dispatchers.IO) {
                val geocoder = Geocoder(this@MapsActivity)

                var addresses: List<Address> = emptyList()
                try {
                    addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                } catch (ioException: IOException) {
                    Timber.e(ioException, "Service unavailable")
                } catch (illegalArgumentException: IllegalArgumentException) {
                    Timber.e(illegalArgumentException, "Invalid latitude/longitude")
                }

                if (addresses.isEmpty()) {
                    emptyList()
                } else {
                    addresses
                }

            }.firstOrNull()

            address?.let {
                target.text = it.getAddressLine(0)
            }
        }
    }
}