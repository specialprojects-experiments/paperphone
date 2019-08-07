package com.withgoogle.experiments.unplugged.util

import android.text.format.DateUtils
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val scheduleDate = DateTimeFormatter.ofPattern("dd MMMM yyyy")

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.of("UTC"))

private val weatherFormatter = DateTimeFormatter.ofPattern("EEEE dd, MMMM")

fun LocalDate.toExtent() = scheduleDate.format(this)

fun LocalDate.weatherFormat() = weatherFormatter.format(this)

fun Instant.toTime() = timeFormatter.format(this)

fun Instant.isToday() = DateUtils.isToday(toEpochMilli())