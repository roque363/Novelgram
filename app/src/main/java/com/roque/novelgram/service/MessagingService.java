package com.roque.novelgram.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.internal.ImageRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.roque.novelgram.R;
import com.roque.novelgram.login.view.LoginActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Response;

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMsgService";
    Bitmap bitmap;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // super.onMessageReceived(remoteMessage);
        String from = remoteMessage.getFrom();
        Log.d(TAG, "Mensaje recibido de: " + from);

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            mostrarNotificacion(title, body);
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            //The message will have keys names [title, message, img_url] and corresponding values.
            String title, message, img_url;
            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("message");
            img_url = remoteMessage.getData().get("img_url");

            //To get a Bitmap image from the URL received
            bitmap = getBitmapFromUrl(img_url);

            mostrarNotificacionImagen(title, message, bitmap);
        }

    }

    private void mostrarNotificacionImagen(String title, String message, Bitmap img_url) {

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String chanelId = getString(R.string.default_notification_channel_id);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.mipmap.icono_launcher_round));
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setAutoCancel(true);
        builder.setStyle(new NotificationCompat
                .BigPictureStyle()
                .bigPicture(img_url));/*Notification with Image*/
        builder.setContentIntent(pendingIntent);
        builder.setSound(soundUri);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(chanelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, builder.build());

    }

    private void mostrarNotificacion(String title, String body) {

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String chanelId = getString(R.string.default_notification_channel_id);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.mipmap.icono_launcher_round))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(chanelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, notificationBuilder.build());

    }

    /*
     * To get a Bitmap image from the URL received
     * */
    public Bitmap getBitmapFromUrl (String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onNewToken(String token) {
        // super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
    }

}
