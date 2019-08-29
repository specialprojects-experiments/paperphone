package com.withgoogle.experiments.unplugged.data.integrations.maps

import com.google.gson.Gson
import com.withgoogle.experiments.unplugged.BuildConfig
import com.withgoogle.experiments.unplugged.model.Location
import okhttp3.HttpUrl
import okhttp3.Request
import timber.log.Timber

class GoogleDirections {
    private val gson = Gson()

    fun directionEncodedPath(origin: Location, destination: Location): String? {
        val url = HttpUrl.get("https://maps.googleapis.com/maps/api/directions/json")

        val finalUrl = url.newBuilder()
            .addEncodedQueryParameter("origin", origin.toString())
            .addEncodedQueryParameter("destination", destination.toString())
            .addQueryParameter("mode", "walking")
            .addQueryParameter("key", BuildConfig.GMAPS_API_KEY)
            .build()

        val request = Request.Builder()
            .url(finalUrl)
            .build()

        val response = GoogleHttpClient.okHttpClient.newCall(request).execute()

        return if (response.isSuccessful) {
            response.body()?.use {
                val result = gson.fromJson(it.charStream(), Result::class.java)

                val encodedPolyline = result.routes[0].overview_polyline.points

                Timber.d(encodedPolyline)

                val staticMapUrl = HttpUrl.get("https://maps.googleapis.com/maps/api/staticmap")

                staticMapUrl.newBuilder()
                    .addEncodedQueryParameter("size", "494x494")
                    .addEncodedQueryParameter("path", "weight:2|color:black|enc:$encodedPolyline")
                    .addEncodedQueryParameter("markers", "icon:https://media.designersfriend.co.uk/sps/media/uploads/work/media/aaa-58511.png|$origin")
                    .addEncodedQueryParameter("markers", "icon:https://media.designersfriend.co.uk/sps/media/uploads/work/media/bbb-85125.png|$destination")
                    .addQueryParameter("key", BuildConfig.GMAPS_API_KEY)
                    .build().toString()
            }
        } else {
            null
        }
    }
}