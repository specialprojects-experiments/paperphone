package com.withgoogle.experiments.unplugged.model

import java.time.Instant

data class ThreeHourForecast(
    val timestamp: Instant,
    val temperature: Int,
    val weather: String,
    val icon: String)