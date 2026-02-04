package com.example.louver.data.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class AuthState {

    public enum Status { IDLE, LOADING, SUCCESS, ERROR }

    @NonNull public final Status status;
    @Nullable public final String message;

    private AuthState(@NonNull Status status, @Nullable String message) {
        this.status = status;
        this.message = message;
    }

    public static AuthState idle() {
        return new AuthState(Status.IDLE, null);
    }

    public static AuthState loading() {
        return new AuthState(Status.LOADING, null);
    }

    public static AuthState success() {
        return new AuthState(Status.SUCCESS, null);
    }

    public static AuthState error(@NonNull String message) {
        return new AuthState(Status.ERROR, message);
    }
}
