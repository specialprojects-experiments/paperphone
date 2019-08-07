package com.withgoogle.experiments.unplugged.ui

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.withgoogle.experiments.unplugged.model.Account
import com.withgoogle.experiments.unplugged.model.Location
import com.withgoogle.experiments.unplugged.ui.maps.MapsModule
import java.time.LocalDate

object AppState {
    val account: MutableLiveData<Account> = MutableLiveData()
    val currentDate: MutableLiveData<LocalDate> = MutableLiveData()
    val photoUri: MutableLiveData<Uri> = MutableLiveData()
    val origin: MutableLiveData<Location> = MutableLiveData()
    val destination: MutableLiveData<Location> = MutableLiveData()
    val firstName: MutableLiveData<String> = MutableLiveData()
    val paperAppRes: MutableLiveData<Int> = MutableLiveData()
    val modules: MutableLiveData<Triple<List<PdfModule>, MapsModule?, PdfModule?>> = MutableLiveData()

    init {
        currentDate.postValue(LocalDate.now())
    }
}