package com.withgoogle.experiments.unplugged.data.prefs

import android.content.SharedPreferences

class BooleanPreference(private val preferences: SharedPreferences,
    private val key: String, private val defaultValue: Boolean = false) {

    fun get(): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }

    val isSet: Boolean
        get() = preferences.contains(key)

    fun set(value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun delete() {
        preferences.edit().remove(key).apply()
    }
}