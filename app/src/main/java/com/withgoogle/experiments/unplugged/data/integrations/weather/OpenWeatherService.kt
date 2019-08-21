package com.withgoogle.experiments.unplugged.data.integrations.weather

import com.google.gson.Gson
import com.withgoogle.experiments.unplugged.BuildConfig
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.data.integrations.maps.GoogleHttpClient
import com.withgoogle.experiments.unplugged.model.ThreeHourForecast
import com.withgoogle.experiments.unplugged.ui.AppState
import okhttp3.HttpUrl
import okhttp3.Request
import timber.log.Timber
import java.time.Instant

class OpenWeatherService {
   private val gson = Gson()

   fun forecast(city: String): List<ThreeHourForecast>? {
      val url = HttpUrl.get("https://api.openweathermap.org/data/2.5/forecast")

      val finalUrl = url.newBuilder()
         .addEncodedQueryParameter("q", city)
         .addQueryParameter("units", "metric")
         .addQueryParameter("appid", BuildConfig.WEATHER_APP_ID)
         .build()

      val request = Request.Builder()
         .url(finalUrl)
         .build()

      val response = GoogleHttpClient.okHttpClient.newCall(request).execute()

      return if (response.isSuccessful) {
         response.body()?.use {
            val result = gson.fromJson(response.body()?.charStream(), ForecastResult::class.java)

            return result.list.filter {
               !it.dt_txt.endsWith("03:00:00") and
                   !it.dt_txt.endsWith("06:00:00") and
                   !it.dt_txt.endsWith("00:00:00") and
                   !it.dt_txt.endsWith("15:00:00") and
                   !it.dt_txt.endsWith("21:00:00") and
                   it.dt_txt.startsWith(AppState.currentDate.value!!.toString())
            }.map {
               Timber.d(it.dt_txt)
               ThreeHourForecast(
                  timestamp = Instant.ofEpochSecond(it.dt),
                  temperature = it.main.temp.toInt(),
                  iconRes = mapIcon(it.weather[0].icon),
                  weather = it.weather[0].main
               )}
         }
      } else {
         null
      }
   }

   private fun mapIcon(icon: String): Int {
      iconsMap.forEach { (key, value) ->
         if(value.contains(icon)) {
            return key
         }
      }

      return -1
   }
}

private val iconsMap = mapOf(
   R.drawable.ic_sunny to setOf("01d", "01n"),
   R.drawable.ic_sun_cloud to setOf("02d", "02n"),
   R.drawable.ic_cloudy to setOf("03d", "03n", "04d", "04n"),
   R.drawable.ic_rain to setOf("09d", "09n", "10d", "10n"),
   R.drawable.ic_thunder to setOf("11d", "11n"),
   R.drawable.ic_snow to setOf("13d", "13n"),
   R.drawable.ic_wind to setOf("50d", "50n")
)

data class ForecastResult(val list: List<ForecastEntry>)

data class ForecastEntry(val dt: Long, val weather: List<Weather>, val main: Temperature, val dt_txt: String)

data class Weather(val main: String, val icon: String)

data class Temperature(val temp: Double)