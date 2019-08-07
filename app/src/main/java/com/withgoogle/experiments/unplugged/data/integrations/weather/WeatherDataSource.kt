package com.withgoogle.experiments.unplugged.data.integrations.weather

import com.withgoogle.experiments.unplugged.model.ThreeHourForecast

object WeatherDataSource {
    val forecasts = mutableListOf<ThreeHourForecast>()

    var location: String = ""
}