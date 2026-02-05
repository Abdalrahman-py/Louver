package com.example.louver.data.util;

public class RepoResult<T> {
    public final boolean success;
    public final T data;
    public final String error;

    private RepoResult(boolean success, T data, String error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public static <T> RepoResult<T> success(T data) {
        return new RepoResult<>(true, data, null);
    }

    public static <T> RepoResult<T> error(String error) {
        return new RepoResult<>(false, null, error);
    }
}
