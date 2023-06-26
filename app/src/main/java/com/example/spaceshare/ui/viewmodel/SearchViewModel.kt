package com.example.spaceshare.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spaceshare.interfaces.LocationInterface
import com.google.android.gms.maps.model.LatLng

class SearchViewModel : ViewModel(), LocationInterface {

    var spaceRequired = MutableLiveData<Double>(0.0)

    private val spaceLowerLimit: Double = 0.0
    private val spaceUpperLimit: Double = 100.0

    var location: MutableLiveData<LatLng?>? = null

    var startTime = MutableLiveData<Long>()
    var endTime = MutableLiveData<Long>()

    fun incrementSpaceRequired() {
        if (spaceRequired.value?.plus(0.5)!! < spaceUpperLimit)
            spaceRequired.value = spaceRequired.value?.plus(0.5)
    }

    fun decrementSpaceRequired() {
        if (spaceRequired.value?.minus(0.5)!! >= spaceLowerLimit)
            spaceRequired.value = spaceRequired.value?.minus(0.5)
    }

    fun clearAllData() {
        // TODO: Find some way to reset the date range picker
        location?.value = null
        spaceRequired.value = 0.0
        startTime.value = System.currentTimeMillis()
        endTime.value = System.currentTimeMillis()
    }

    fun submitSearch() {

    }

    override fun setLocation(latLng: LatLng) {
        location?.value = latLng
    }

}