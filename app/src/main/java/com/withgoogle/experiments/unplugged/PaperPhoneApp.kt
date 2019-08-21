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

    override fun onCreate() {
        super.onCreate()

        Places.initialize(this, BuildConfig.PLACES_API_KEY)

        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)

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