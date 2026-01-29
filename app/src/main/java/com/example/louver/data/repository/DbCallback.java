package com.example.louver.data.repository;

/**
 * Simple callback for background DB operations.
 * Used to deliver results back to the caller (typically a ViewModel).
 */
public interface DbCallback<T> {
    void onComplete(T result);
}
