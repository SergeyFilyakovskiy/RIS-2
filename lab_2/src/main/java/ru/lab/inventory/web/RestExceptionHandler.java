package ru.lab.inventory.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.lab.inventory.exception.DuplicateInventoryNumberException;
import ru.lab.inventory.exception.ItemNotFoundException;

import java.util.List;

/** Advice только для REST-контроллера: ошибки уходят клиенту в JSON с текстом. */
@RestControllerAdvice(assignableTypes = InventoryRestController.class)
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError onValidation(MethodArgumentNotValidException ex) {
        List<ApiError.FieldErrorInfo> fields = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ApiError.FieldErrorInfo(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return ApiError.of(400, "Bad Request", "Validation failed", fields);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError onUnreadable(HttpMessageNotReadableException ex) {
        return ApiError.of(400, "Bad Request",
                "Malformed JSON body or invalid value format (status must be WORKING or FAULTY)");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError onTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ApiError.of(400, "Bad Request", "Invalid parameter value: " + ex.getName());
    }

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError onNotFound(ItemNotFoundException ex) {
        return ApiError.of(404, "Not Found", ex.getMessage());
    }

    @ExceptionHandler(DuplicateInventoryNumberException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError onDuplicate(DuplicateInventoryNumberException ex) {
        return ApiError.of(409, "Conflict", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError onOther(Exception ex) {
        return ApiError.of(500, "Internal Server Error", "Unexpected error: " + ex.getMessage());
    }
}