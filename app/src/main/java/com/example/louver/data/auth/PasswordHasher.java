package com.example.louver.data.auth;

import android.util.Base64;
import android.util.Log;

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

    private static final int DEFAULT_ITERATIONS = 120_000;
    private static final int SALT_BYTES = 16;
    private static final int KEY_LENGTH_BITS = 256;

    // Fixed algorithm — used for both hashing and verification of new hashes
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String HASH_PREFIX = "pbkdf2_sha256";

    private PasswordHasher() {}

    /**
     * Convenience overload for seeding / tests.
     * Converts String to char[], then delegates to hashPassword(char[]).
     */
    @NonNull
    public static String hashPassword(@NonNull String password) {
        return hashPassword(password.toCharArray());
    }

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
            derived = pbkdf2(password, salt, iterations, KEY_LENGTH_BITS, ALGORITHM);
            String saltB64 = Base64.encodeToString(salt, Base64.NO_WRAP);
            String hashB64 = Base64.encodeToString(derived, Base64.NO_WRAP);
            return HASH_PREFIX + "$" + iterations + "$" + saltB64 + "$" + hashB64;
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

        // Log exact password being verified
        Log.d("VERIFY_DEBUG", "Password length: " + password.length);
        Log.d("VERIFY_DEBUG", "Password chars: [" + new String(password) + "]");

        try {
            String[] parts = stored.split("\\$");
            Log.d("VERIFY_DEBUG", "Parts count: " + parts.length);
            if (parts.length != 4) return false;

            String prefix = parts[0].toLowerCase(Locale.US);
            Log.d("VERIFY_DEBUG", "Algo prefix: " + prefix);
            if (!prefix.startsWith("pbkdf2_")) return false;

            // Derive exact JCE algorithm name from stored prefix
            // pbkdf2_sha256 → PBKDF2WithHmacSHA256
            // pbkdf2_sha1   → PBKDF2WithHmacSHA1
            String jceAlgo;
            if (prefix.equals("pbkdf2_sha256")) {
                jceAlgo = "PBKDF2WithHmacSHA256";
            } else if (prefix.equals("pbkdf2_sha1")) {
                jceAlgo = "PBKDF2WithHmacSHA1";
            } else {
                Log.d("VERIFY_DEBUG", "Unknown prefix: " + prefix);
                return false;
            }
            Log.d("VERIFY_DEBUG", "JCE algo: " + jceAlgo);

            int iterations;
            try {
                iterations = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                Log.d("VERIFY_DEBUG", "Bad iterations: " + parts[1]);
                return false;
            }
            Log.d("VERIFY_DEBUG", "Iterations: " + iterations);

            salt     = Base64.decode(parts[2], Base64.NO_WRAP);
            expected = Base64.decode(parts[3], Base64.NO_WRAP);
            Log.d("VERIFY_DEBUG", "Salt len: " + salt.length + " Expected len: " + expected.length);

            actual = pbkdf2(password, salt, iterations, expected.length * 8, jceAlgo);
            Log.d("VERIFY_DEBUG", "Actual len: " + (actual != null ? actual.length : -1));

            boolean match = MessageDigest.isEqual(expected, actual);
            Log.d("VERIFY_DEBUG", "isEqual: " + match);
            return match;
        } finally {
            wipe(password);
            if (salt != null) wipe(salt);
            if (expected != null) wipe(expected);
            if (actual != null) wipe(actual);
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations,
                                  int keyLengthBits, String jceAlgo) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLengthBits);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(jceAlgo);
            return skf.generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 not available: " + jceAlgo, e);
        } finally {
            spec.clearPassword();
        }
    }


    private static void wipe(@NonNull char[] arr) {
        Arrays.fill(arr, '\0');
    }

    private static void wipe(@NonNull byte[] arr) {
        Arrays.fill(arr, (byte) 0);
    }
}
