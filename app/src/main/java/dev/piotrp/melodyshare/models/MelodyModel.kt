package dev.piotrp.melodyshare.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MelodyModel(var id: Long = 0, var title: String = "", var description: String = "", var tempo: Float = 120f, var notes: ArrayList<MelodyNote> = ArrayList()) : Parcelable
