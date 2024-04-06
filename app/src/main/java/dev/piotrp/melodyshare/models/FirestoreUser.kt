package dev.piotrp.melodyshare.models

import kotlinx.serialization.Serializable

// For some reason trying better types fails during de/serialization

// ! Only use this class for documents read from Firestore
@Serializable
data class FirestoreUser(
    val uid: String = "",
    val photoURL: String = "",
    val metadata: UserMetadata = UserMetadata(),
    val providerData: List<ProviderData> = arrayListOf(),
    val displayName: String = "",
    val email: String = "",
    // ? List of uuid's
    val friends: List<String> = arrayListOf()
)

@Serializable
data class UserMetadata(
    val lastSignedInAt: String = "",
    val createdAt: String = ""
)

@Serializable
data class ProviderData(
    val photoURL: String = "",
    val uid: String = "",
    val providerId: String = "",
    val displayName: String = "",
    val email: String = ""
)

