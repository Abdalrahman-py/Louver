package com.example.louver.data.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.louver.data.converter.NotificationType;
import com.example.louver.data.entity.NotificationEntity;

/**
 * NotificationScheduler: Manages scheduling and cancellation of booking notifications.
 * Uses AlarmManager for exact-time notifications.
 */
public class NotificationScheduler {

    private static final String NOTIFICATION_ACTION = "com.example.louver.BOOKING_NOTIFICATION";
    private static final String EXTRA_BOOKING_ID = "bookingId";
    private static final String EXTRA_NOTIFICATION_TYPE = "notificationType";

    private static final long ONE_HOUR_MILLIS = 60 * 60 * 1000;
    private static final long LATE_CHECK_DELAY_MILLIS = 30 * 60 * 1000; // 30 minutes after end

    /**
     * Schedule all notifications for a booking.
     * Must check notificationsEnabled before calling.
     *
     * @param context            Application context
     * @param db                 AppDatabase instance for storing notification records
     * @param bookingId          ID of booking
     * @param returnEpochMillis  Return time in milliseconds since epoch
     */
    public static void scheduleBookingNotifications(
            @NonNull Context context,
            @NonNull com.example.louver.data.db.AppDatabase db,
            long bookingId,
            long returnEpochMillis
    ) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        long now = System.currentTimeMillis();

        // Schedule 1 hour before end
        long oneHourBefore = returnEpochMillis - ONE_HOUR_MILLIS;
        if (oneHourBefore > now) {
            scheduleAlarm(context, alarmManager, bookingId, oneHourBefore, NotificationType.BEFORE_END);
            db.notificationDao().insert(new NotificationEntity(bookingId, NotificationType.BEFORE_END, oneHourBefore, null, false));
        }

        // Schedule at end time
        if (returnEpochMillis > now) {
            scheduleAlarm(context, alarmManager, bookingId, returnEpochMillis, NotificationType.ENDED);
            db.notificationDao().insert(new NotificationEntity(bookingId, NotificationType.ENDED, returnEpochMillis, null, false));

            // Schedule late return check 30 minutes after end (only if end time is in future)
            long lateCheckTime = returnEpochMillis + LATE_CHECK_DELAY_MILLIS;
            scheduleAlarm(context, alarmManager, bookingId, lateCheckTime, NotificationType.OVERDUE);
            db.notificationDao().insert(new NotificationEntity(bookingId, NotificationType.OVERDUE, lateCheckTime, null, false));
        }
    }

    /**
     * Cancel all scheduled notifications for a booking.
     *
     * @param context   Application context
     * @param bookingId ID of booking
     */
    public static void cancelBookingNotifications(
            @NonNull Context context,
            long bookingId
    ) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        // Cancel all three notification types for this booking
        for (NotificationType type : NotificationType.values()) {
            cancelAlarm(context, alarmManager, bookingId, type);
        }
    }

    private static void scheduleAlarm(
            @NonNull Context context,
            @NonNull AlarmManager alarmManager,
            long bookingId,
            long triggerAtMillis,
            @NonNull NotificationType type
    ) {
        Intent intent = new Intent(context, BookingAlarmReceiver.class);
        intent.setAction(NOTIFICATION_ACTION);
        intent.putExtra(EXTRA_BOOKING_ID, bookingId);
        intent.putExtra(EXTRA_NOTIFICATION_TYPE, type.name());

        int requestCode = getRequestCode(bookingId, type);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } catch (SecurityException e) {
            // Fallback to setAndAllowWhileIdle if setExactAndAllowWhileIdle is not available
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }

    private static void cancelAlarm(
            @NonNull Context context,
            @NonNull AlarmManager alarmManager,
            long bookingId,
            @NonNull NotificationType type
    ) {
        Intent intent = new Intent(context, BookingAlarmReceiver.class);
        int requestCode = getRequestCode(bookingId, type);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private static int getRequestCode(long bookingId, @NonNull NotificationType type) {
        // Combine bookingId and type into a unique request code
        return (int) (bookingId * 1000 + type.ordinal());
    }
}

