package com.example.louver.data.auth;

import android.util.Base64;

import androidx.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Locale;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordHasher {

    private static final SecureRandom RNG = new SecureRandom();

    // Strong but still reasonable for mobile; tune if needed.
    private static final int DEFAULT_ITERATIONS = 120_000;
    private static final int SALT_BYTES = 16;
    private static final int KEY_LENGTH_BITS = 256;

    private PasswordHasher() {}

    /**
     * Format:
     * pbkdf2_sha256$iterations$saltBase64$hashBase64
     */
    @NonNull
    public static String hashPassword(@NonNull char[] password) {
        byte[] salt = new byte[SALT_BYTES];
        RNG.nextBytes(salt);

        int iterations = DEFAULT_ITERATIONS;
        byte[] derived = null;

        try {
            derived = pbkdf2(password, salt, iterations, KEY_LENGTH_BITS);
            String saltB64 = Base64.encodeToString(salt, Base64.NO_WRAP);
            String hashB64 = Base64.encodeToString(derived, Base64.NO_WRAP);
            return "pbkdf2_sha256" + "$" + iterations + "$" + saltB64 + "$" + hashB64;
        } finally {
            wipe(password);
            wipe(salt);
            if (derived != null) wipe(derived);
        }
    }

    public static boolean verifyPassword(@NonNull char[] password, @NonNull String stored) {
        byte[] salt = null;
        byte[] expected = null;
        byte[] actual = null;

        try {
            String[] parts = stored.split("\\$");
            if (parts.length != 4) return false;

            String algo = parts[0].toLowerCase(Locale.US);
            if (!algo.startsWith("pbkdf2_")) return false;

            int iterations;
            try {
                iterations = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                return false;
            }

            salt = Base64.decode(parts[2], Base64.NO_WRAP);
            expected = Base64.decode(parts[3], Base64.NO_WRAP);

            // Prefer SHA-256 if available; fallback handled inside pbkdf2()
            actual = pbkdf2(password, salt, iterations, expected.length * 8);

            return MessageDigest.isEqual(expected, actual);
        } finally {
            wipe(password);
            if (salt != null) wipe(salt);
            if (expected != null) wipe(expected);
            if (actual != null) wipe(actual);
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLengthBits) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLengthBits);
        try {
            SecretKeyFactory skf = getFactoryPreferSha256();
            return skf.generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 not available", e);
        } finally {
            spec.clearPassword();
        }
    }

    private static SecretKeyFactory getFactoryPreferSha256() throws GeneralSecurityException {
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch (GeneralSecurityException ignored) {
            // Older devices/providers may not have SHA-256 PBKDF2; fallback.
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        }
    }

    private static void wipe(@NonNull char[] arr) {
        Arrays.fill(arr, '\0');
    }

    private static void wipe(@NonNull byte[] arr) {
        Arrays.fill(arr, (byte) 0);
    }
}
