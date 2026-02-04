package com.example.louver.data.auth;

import androidx.annotation.Nullable;

public final class AuthResult {
    public final boolean success;
    @Nullable public final String errorMessage;

    private AuthResult(boolean success, @Nullable String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static AuthResult ok() {
        return new AuthResult(true, null);
    }

    public static AuthResult error(String message) {
        return new AuthResult(false, message);
    }
}
