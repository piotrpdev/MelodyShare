package dev.piotrp.melodyshare.models

import timber.log.Timber.i

var lastId = 0L
internal fun getId() = lastId++

class PlacemarkMemStore : PlacemarkStore  {
    private val placemarks = ArrayList<PlacemarkModel>()

    override fun findAll(): List<PlacemarkModel> {
        return placemarks
    }

    override fun create(placemark: PlacemarkModel) {
        placemark.id = getId()
        placemarks.add(placemark)
        logAll()
    }

    override fun update(placemark: PlacemarkModel) {
        val foundPlacemark: PlacemarkModel? = placemarks.find { p -> p.id == placemark.id }
        if (foundPlacemark != null) {
            foundPlacemark.title = placemark.title
            foundPlacemark.description = placemark.description
            logAll()
        }
    }

    private fun logAll() {
        placemarks.forEach{ i("$it") }
    }
}