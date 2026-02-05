package com.example.louver.data.converter;

import androidx.room.TypeConverter;

public class AppTypeConverters {

    // BookingStatus
    @TypeConverter
    public static String bookingStatusToString(BookingStatus status) {
        return status == null ? null : status.name();
    }

    @TypeConverter
    public static BookingStatus stringToBookingStatus(String value) {
        return value == null ? null : BookingStatus.valueOf(value);
    }

    // TransmissionType
    @TypeConverter
    public static String transmissionTypeToString(TransmissionType type) {
        return type == null ? null : type.name();
    }

    @TypeConverter
    public static TransmissionType stringToTransmissionType(String value) {
        return value == null ? null : TransmissionType.valueOf(value);
    }

    // FuelType
    @TypeConverter
    public static String fuelTypeToString(FuelType type) {
        return type == null ? null : type.name();
    }

    @TypeConverter
    public static FuelType stringToFuelType(String value) {
        return value == null ? null : FuelType.valueOf(value);
    }

    // NotificationType
    @TypeConverter
    public static String notificationTypeToString(NotificationType type) {
        return type == null ? null : type.name();
    }

    @TypeConverter
    public static NotificationType stringToNotificationType(String value) {
        return value == null ? null : NotificationType.valueOf(value);
    }
    @TypeConverter
    public static String fromRole(UserRole role) {
        return role == null ? null : role.name();
    }

    @TypeConverter
    public static UserRole toRole(String role) {
        return role == null ? UserRole.USER : UserRole.valueOf(role);
    }

}
