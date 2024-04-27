package dev.piotrp.melodyshare.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Parcelize
data class ShareDocument(
    var id: String = UUID.randomUUID().toString(),
    var senderUid: String = "",
    var receiverUid: String = "",
    var melodyId: String = ""
) : Parcelable
