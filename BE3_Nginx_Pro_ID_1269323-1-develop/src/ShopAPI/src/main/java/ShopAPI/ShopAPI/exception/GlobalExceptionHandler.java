package ShopAPI.ShopAPI.exception;


import ShopAPI.ShopAPI.dto.response.errorResponse.ErrorResponse;
import ShopAPI.ShopAPI.dto.response.errorResponse.ValidationErrorResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
        log.error("Validation error : {}", exception.getMessage());
        Map<String, List<String>> errors = new HashMap<>();

        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
        });

        ValidationErrorResponse response = new ValidationErrorResponse(
                "Ошибка валидации данных"
                , HttpStatus.BAD_REQUEST.value()
                , LocalDateTime.now()
                , errors);

        return ResponseEntity.badRequest().body(response);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage()); // "Product not found"
        errorResponse.put("status", "404");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(OperationNotExcepted.class)
    public ResponseEntity<Map<String, String>> handleOperationNotExcepted(OperationNotExcepted excepted) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", excepted.getMessage());
        errors.put("status", String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()));
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errors);
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handelImageNotFound(ImageNotFoundException exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidation(ValidationException exception) {
        Map<String, String> response = new HashMap<>();
        response.put("message", exception.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.toString());
        if (exception.getErrors() != null) {
            response.put("errors", exception.getErrors().toString());
        }
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handlerInsufficientStock(InsufficientStockException exception) {
        ErrorResponse error = new ErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handlerBadRequest(BadRequestException exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(InvalidImageFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidImageFormat(InvalidImageFormatException exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(DataIntegrityException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DuplicateResourceException exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintValidation(ConstraintViolationException exception) {
        log.error("Constraint validation: {} ", exception.getMessage());
        Map<String, List<String>> errors = new HashMap<>();
        exception.getConstraintViolations().forEach(violations -> {
            String filedName = violations.getPropertyPath().toString();
            String message = violations.getMessage();
            errors.computeIfAbsent(filedName, k -> new ArrayList<>()).add(message);
        });
        ValidationErrorResponse response = new ValidationErrorResponse("Validation error", HttpStatus.BAD_REQUEST.value(), LocalDateTime.now(), errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException exception) {
        log.error("Missing parameter: ", exception.getMessage());
        String message = String.format("Отсутствует обязательный параметр '%s' типа '%s'", exception.getParameterName(), exception.getParameterType());
        ErrorResponse errorResponse = new ErrorResponse(message, HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
        return ResponseEntity.badRequest().body(errorResponse);
    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
//        Map<String, List<String>> errors = new HashMap<>();
//        exception.getBindingResult().getFieldErrors().forEach(error -> {
//            String filed = error.getField();
//            String message = error.getDefaultMessage();
//            errors.computeIfAbsent(filed, k -> new ArrayList<>()).add(message);
//        });
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "Error validation");
//        response.put("status", HttpStatus.BAD_REQUEST.value());
//        response.put("errors", errors);
//        return ResponseEntity.badRequest().body(response);
//    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded() {
        ErrorResponse error = new ErrorResponse("Error max size file", HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        ErrorResponse error = new ErrorResponse("Internal server error: " + exception.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ...
}
