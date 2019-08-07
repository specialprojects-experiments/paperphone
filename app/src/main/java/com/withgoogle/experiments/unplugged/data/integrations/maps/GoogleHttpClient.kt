package com.withgoogle.experiments.unplugged.data.integrations.maps

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.util.concurrent.TimeUnit

object GoogleHttpClient {
    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message -> Timber.d(message) }).apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                })
            .build()
    }
}