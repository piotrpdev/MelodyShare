package dev.piotrp.melodyshare.models

interface MelodyStore {
    fun findAll(): List<MelodyModel>

    fun create(melody: MelodyModel)

    fun update(melody: MelodyModel)

    fun remove(melody: MelodyModel)
}
