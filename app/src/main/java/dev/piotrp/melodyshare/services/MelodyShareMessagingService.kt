package dev.piotrp.melodyshare.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.github.ajalt.timberkt.d
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dev.piotrp.melodyshare.R
import dev.piotrp.melodyshare.activities.MainActivity
import dev.piotrp.melodyshare.models.ShareMessage
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

// Adapter from example code at:
//  https://github.com/firebase/quickstart-android/blob/98e1f9785911b525f7835ebfb3b050e0ef558f13/messaging/app/src/main/java/com/google/firebase/quickstart/fcm/kotlin/MyFirebaseMessagingService.kt

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MelodyShareMessagingService : FirebaseMessagingService() {
    private lateinit var fid: String
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // ! This is only called when an FCM message is sent
    override fun onCreate() {
        super.onCreate()

        d { "Creating MelodyShareMessagingService" }

        runBlocking {
            fid = FirebaseInstallations.getInstance().id.await()
        }

        db = Firebase.firestore
        auth = Firebase.auth
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        d { "From: ${remoteMessage.from}" }

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            d { "Message data payload: ${remoteMessage.data}" }

            if (auth.currentUser?.uid == null) {
                d { "No auth user id in service, skipping message" }
                return
            }

            // TODO: Check all of the data is in there
            val msg = ShareMessage(remoteMessage.data)

            if (auth.currentUser!!.uid.toString() != msg.receiverUid) {
                d { "FCM message receiverUid doesn't match currentUser.uid, skipping message" }
                return
            }

            sendNotification(msg)
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param msg FCM message received.
     */
    private fun sendNotification(msg: ShareMessage) {
        val requestCode = 0
        val intent = Intent(this, MainActivity::class.java)
        // TODO: Check this doesn't cause a NullPointerException in MelodyChangeActivity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        // TODO: Use melodyId instead
        intent.putExtra("shared_melody_title", msg.melodyTitle)
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )

        // TODO: Use async
        val senderBitmap =
            Glide.with(this)
                .asBitmap()
                .load(msg.senderPhoto)
                .submit()
                .get()

        val channelId = getString(R.string.default_notification_channel_id)

        val notificationBuilder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(senderBitmap)
                .setContentTitle(getString(R.string.melody_received_title))
                .setContentText(getString(R.string.melody_received_description, msg.senderName, msg.melodyTitle))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        val channel =
            NotificationChannel(
                channelId,
                getString(R.string.notification_channel),
                NotificationManager.IMPORTANCE_DEFAULT,
            )
        notificationManager.createNotificationChannel(channel)

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
