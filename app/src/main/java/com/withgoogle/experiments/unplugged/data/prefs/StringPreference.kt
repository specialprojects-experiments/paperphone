package com.withgoogle.experiments.unplugged.data.prefs

import android.content.SharedPreferences

class StringPreference(private val preferences: SharedPreferences,
    private val key: String, private val defaultValue: String = "") {

    fun get(): String {
        return preferences.getString(key, defaultValue)
    }

    val isSet: Boolean
        get() = preferences.contains(key)

    fun set(value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun delete() {
        preferences.edit().remove(key).apply()
    }
}