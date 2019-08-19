package com.withgoogle.experiments.unplugged

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import com.google.android.libraries.places.api.Places
import com.withgoogle.experiments.unplugged.data.prefs.BooleanPreference
import com.withgoogle.experiments.unplugged.data.prefs.StringPreference
import com.withgoogle.experiments.unplugged.model.Account
import com.withgoogle.experiments.unplugged.ui.AppState
import timber.log.Timber

class PaperPhoneApp: Application() {
    lateinit var accountPreference: StringPreference
    lateinit var onboardingPreference: BooleanPreference
    lateinit var namePreference: StringPreference

    val preferenceMap = mutableMapOf<Int, BooleanPreference>()

    val modulesInfoMap = mapOf(
        R.id.maps to R.string.maps_info,
        R.id.contacts to R.string.contacts_info,
        R.id.calendar to R.string.calendar_info,
        R.id.weather to R.string.weather_info,
        R.id.tasks to R.string.tasks_info,
        R.id.notes to R.string.notes_info,
        R.id.photos to R.string.photos_info,
        R.id.contactless to R.string.contactless_info,
        R.id.paper_apps to R.string.paper_apps_info
    )

    override fun onCreate() {
        super.onCreate()

        Places.initialize(this, BuildConfig.PLACES_API_KEY)

        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)

        modulesInfoMap.forEach {
            preferenceMap[it.key] = BooleanPreference(preferenceManager, "$it-interacted")
        }

        accountPreference = StringPreference(preferenceManager, "gaccount")
        onboardingPreference = BooleanPreference(preferenceManager, "completedOnboarding")
        namePreference = StringPreference(preferenceManager, "name")

        AppState.firstName.value = namePreference.get()

        if (accountPreference.isSet) {
            val splitted = accountPreference.get().split("|")
            AppState.account.value = Account(splitted[0], splitted[1])
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {
        @JvmStatic
        fun obtain(context: Context): PaperPhoneApp {
            return context.applicationContext as PaperPhoneApp
        }
    }

    var taskToken: String? = null
}