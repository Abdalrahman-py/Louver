package com.example.louver.data.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.louver.R;
import com.example.louver.data.converter.NotificationType;
import com.example.louver.data.db.AppDatabase;

/**
 * NotificationBroadcastReceiver: Handles alarm broadcasts and shows notifications.
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "louver_bookings";
    private static final int NOTIFICATION_ID_BASE = 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !intent.getAction().equals("com.example.louver.BOOKING_NOTIFICATION")) {
            return;
        }

        long bookingId = intent.getLongExtra("bookingId", -1);
        String typeString = intent.getStringExtra("notificationType");

        if (bookingId < 0 || typeString == null) {
            return;
        }

        NotificationType type = NotificationType.valueOf(typeString);
        String title = "Booking Notification";
        String message = "";

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
                message = "Your booking return is overdue. Please return the car immediately.";
                break;
        }

        showNotification(context, bookingId, type, title, message);

        // Mark notification as fired in database
        AppDatabase db = AppDatabase.getInstance(context);
        db.notificationDao().getAllForBooking(bookingId).observeForever(notifications -> {
            for (com.example.louver.data.entity.NotificationEntity n : notifications) {
                if (n.type == type && !n.isFired) {
                    db.notificationDao().markFired(n.id, System.currentTimeMillis());
                }
            }
        });
    }

    private void showNotification(
            Context context,
            long bookingId,
            NotificationType type,
            String title,
            String message
    ) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        int notificationId = (int) (NOTIFICATION_ID_BASE + bookingId * 10 + type.ordinal());
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }
}

