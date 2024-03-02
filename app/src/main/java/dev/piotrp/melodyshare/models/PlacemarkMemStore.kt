package dev.piotrp.melodyshare.models

import timber.log.Timber.i

class PlacemarkMemStore : PlacemarkStore  {
    private val placemarks = ArrayList<PlacemarkModel>()

    override fun findAll(): List<PlacemarkModel> {
        return placemarks
    }

    override fun create(placemark: PlacemarkModel) {
        placemarks.add(placemark)
        logAll()
    }

    private fun logAll() {
        placemarks.forEach{ i("$it") }
    }
}