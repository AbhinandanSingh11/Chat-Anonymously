package com.nimus.chatanonymously.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nimus.chatanonymously.Activities.MainActivity;
import com.nimus.chatanonymously.R;

public class FirebaseMessaging  extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("msg", "onMessageReceived: " + remoteMessage.getData().get("message"));
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = "Default";
        NotificationCompat.Builder builder = new  NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.pet)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody()).setAutoCancel(true).setContentIntent(pendingIntent);;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, builder.build());
    }
}