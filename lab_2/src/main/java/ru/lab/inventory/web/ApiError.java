package ru.lab.inventory.web;

import java.time.Instant;
import java.util.List;

/** Единый формат ошибки для REST API. */
public record ApiError(
        String timestamp,
        int status,
        String error,
        String message,
        List<FieldErrorInfo> fieldErrors) {

    public record FieldErrorInfo(String field, String message) {}

    public static ApiError of(int status, String error, String message) {
        return new ApiError(Instant.now().toString(), status, error, message, List.of());
    }

    public static ApiError of(int status, String error, String message, List<FieldErrorInfo> fieldErrors) {
        return new ApiError(Instant.now().toString(), status, error, message, fieldErrors);
    }
}