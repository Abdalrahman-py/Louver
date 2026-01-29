package com.example.louver.data.repository;

import android.content.Context;

import com.example.louver.data.db.AppDatabase;

/**
 * Simple service-locator for repositories.
 * Keeps repository creation in one place.
 */
public final class RepositoryProvider {

    private static volatile UserRepository userRepository;
    private static volatile CategoryRepository categoryRepository;
    private static volatile CarRepository carRepository;
    private static volatile BookingRepository bookingRepository;
    private static volatile FavoriteRepository favoriteRepository;
    private static volatile ReviewRepository reviewRepository;
    private static volatile SettingsRepository settingsRepository;
    private static volatile NotificationRepository notificationRepository;

    private RepositoryProvider() {}

    public static UserRepository user(Context context) {
        if (userRepository == null) {
            synchronized (RepositoryProvider.class) {
                if (userRepository == null) {
                    userRepository = new UserRepository(AppDatabase.getInstance(context));
                }
            }
        }
        return userRepository;
    }

    public static CategoryRepository categories(Context context) {
        if (categoryRepository == null) {
            synchronized (RepositoryProvider.class) {
                if (categoryRepository == null) {
                    categoryRepository = new CategoryRepository(AppDatabase.getInstance(context));
                }
            }
        }
        return categoryRepository;
    }

    public static CarRepository cars(Context context) {
        if (carRepository == null) {
            synchronized (RepositoryProvider.class) {
                if (carRepository == null) {
                    carRepository = new CarRepository(AppDatabase.getInstance(context));
                }
            }
        }
        return carRepository;
    }

    public static BookingRepository bookings(Context context) {
        if (bookingRepository == null) {
            synchronized (RepositoryProvider.class) {
                if (bookingRepository == null) {
                    bookingRepository = new BookingRepository(AppDatabase.getInstance(context));
                }
            }
        }
        return bookingRepository;
    }

    public static FavoriteRepository favorites(Context context) {
        if (favoriteRepository == null) {
            synchronized (RepositoryProvider.class) {
                if (favoriteRepository == null) {
                    favoriteRepository = new FavoriteRepository(AppDatabase.getInstance(context));
                }
            }
        }
        return favoriteRepository;
    }

    public static ReviewRepository reviews(Context context) {
        if (reviewRepository == null) {
            synchronized (RepositoryProvider.class) {
                if (reviewRepository == null) {
                    reviewRepository = new ReviewRepository(AppDatabase.getInstance(context));
                }
            }
        }
        return reviewRepository;
    }

    public static SettingsRepository settings(Context context) {
        if (settingsRepository == null) {
            synchronized (RepositoryProvider.class) {
                if (settingsRepository == null) {
                    settingsRepository = new SettingsRepository(AppDatabase.getInstance(context));
                }
            }
        }
        return settingsRepository;
    }

    public static NotificationRepository notifications(Context context) {
        if (notificationRepository == null) {
            synchronized (RepositoryProvider.class) {
                if (notificationRepository == null) {
                    notificationRepository = new NotificationRepository(AppDatabase.getInstance(context));
                }
            }
        }
        return notificationRepository;
    }
}
