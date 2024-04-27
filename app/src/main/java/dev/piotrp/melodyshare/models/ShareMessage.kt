package dev.piotrp.melodyshare.models

@Suppress("unused")
class ShareMessage(private val map: Map<String, String>) {
    val senderUid by map
    val senderName by map
    val senderPhoto by map
    val receiverUid by map
    val receiverName by map
    val receiverPhoto by map
    val melodyId by map
    val melodyTitle by map
}
