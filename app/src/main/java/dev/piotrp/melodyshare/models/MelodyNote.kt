package dev.piotrp.melodyshare.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MelodyNote(var channel: Int, var pitch: Int, var velocity: Int, var tick: Long, var duration: Long) :
    Parcelable
