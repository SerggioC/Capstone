package com.sergiocruz.capstone;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sergiocruz.capstone.view.MainActivity;

import java.util.Map;

import timber.log.Timber;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final int NOTIFICATION_ID = 111;
    private static final String CHANNEL_ID = "message_channel_id";

    private static PendingIntent getPendingIntent(Context context) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);

        return PendingIntent.getActivity(
                context,
                NOTIFICATION_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Timber.i("onMessageReceived remoteMessage= %s", remoteMessage);

        if (remoteMessage == null) return;

        Map<String, String> data = remoteMessage.getData();
        if (data == null) return;

        // Check if notification preference is enabled
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean shouldNotify = preferences.getBoolean(
                getString(R.string.notification_pref_key),
                getResources().getBoolean(R.bool.default_notifications));
        if (!shouldNotify) return;

        Context context = getApplicationContext();

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android Oreo devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        Uri alertSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(data.get("author"))
                        .setContentText(data.get("message"))
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.travel_image)))
                        .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                        .setColorized(true)
                        .setContentIntent(getPendingIntent(context))
                        .setSound(alertSound)
                        .setAutoCancel(true);

        Notification notification = notificationBuilder.build();
        notification.sound = alertSound;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        // Trigger the notification by calling notify on the NotificationManager.
        // Pass in a unique ID of your choosing for the notification and the notification
        notificationManager.notify(NOTIFICATION_ID, notification);

    }

}

// example server data:
//{"to":"","data":{"author":"TestAccount","message":"Venmo knausgaard actually distillery 8-bit ethical","date":1521736350167,"authorKey":"key_test"}}

//{"to":"eAbZ8L3lnz8:APA91bF5Xt03b09QMU4oBwAdpI-74tTzm2uF_1fo2q0CRr_-VmKB_nh28zB90TyrYfdHW7HXppN6S8KZJDWqL-DvKCxINm7nPy8Dr_qZ8wtMyo2zhwU0VLpXGeS6uqd_jRvKgK3P3JWX","data":{"author":"TestAccount","message":"Fap tilde butcher keffiyeh, helvetica master cleanse readymade, keffiyeh leggings semiotics la croix kale chips biodiesel quinoa affogato, he


