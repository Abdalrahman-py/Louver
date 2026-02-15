package com.example.louver.data.notification;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.louver.R;
import com.example.louver.data.converter.NotificationType;

/**
 * BookingAlarmReceiver: Handles alarm broadcasts and shows notifications.
 */
public class BookingAlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "louver_bookings";
    private static final int NOTIFICATION_ID_BASE = 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getAction();
        if (action == null || !action.equals("com.example.louver.BOOKING_NOTIFICATION")) {
            return;
        }

        long bookingId = intent.getLongExtra("bookingId", -1);
        String typeString = intent.getStringExtra("notificationType");

        if (bookingId < 0 || typeString == null) {
            return;
        }

        NotificationType type;
        try {
            type = NotificationType.valueOf(typeString);
        } catch (IllegalArgumentException e) {
            return;
        }

        String title;
        String message;

        switch (type) {
            case BEFORE_END:
                title = "Booking Reminder";
                message = "Your booking ends in 1 hour";
                break;
            case ENDED:
                title = "Booking Ended";
                message = "Your booking has ended. Please return the car.";
                break;
            case OVERDUE:
                title = "Late Return";
                message = "Your booking is overdue. Please return the car immediately.";
                break;
            default:
                title = "Booking Notification";
                message = "";
                break;
        }

        showNotification(context, bookingId, title, message);
    }

    private void showNotification(Context context, long bookingId, String title, String message) {
        // Check POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, fail gracefully without crashing
                return;
            }
        }

        ensureNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        int notificationId = (int) (NOTIFICATION_ID_BASE + bookingId);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }

    private void ensureNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Booking Notifications";
            String description = "Notifications for car rental bookings";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}

