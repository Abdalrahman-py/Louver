package com.example.louver.ui.admin;

import com.example.louver.data.auth.SessionManager;

public final class AdminAccessGuard {

    private AdminAccessGuard() {}

    public static boolean isAdmin(SessionManager sessionManager) {
        return sessionManager != null && "ADMIN".equals(sessionManager.getUserRole());
    }
}
