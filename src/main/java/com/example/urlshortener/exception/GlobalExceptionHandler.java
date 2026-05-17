// previously we were using a Map to return error responses, which is not a standardized way of handling errors in REST APIs. Now we have created a new class called ErrorResponse which has two fields: message and status. This class will be used to return error responses in a standardized format.

package com.example.urlshortener.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFound(
            UrlNotFoundException ex) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex) {

        ErrorResponse error = new ErrorResponse(
                "Something went wrong",
                HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}




// Old Exception Which was Using Map which is Unprofressional and Not Standardized, Now we are using a Standardized Response with Error and Message Keys

// package com.example.urlshortener.exception;

// import java.util.HashMap;
// import java.util.Map;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.bind.annotation.RestControllerAdvice;

// @RestControllerAdvice
// public class GlobalExceptionHandler {

//     @ExceptionHandler(UrlNotFoundException.class)
//     public ResponseEntity<Map<String, String>> handleUrlNotFound(UrlNotFoundException ex) {
//         Map<String, String> body = new HashMap<>();
//         body.put("error", "Not Found");
//         body.put("message", ex.getMessage());   // Or body.put("message", "Something went wrong");
//         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
//     }

//     @ExceptionHandler(IllegalArgumentException.class)
//     public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
//         Map<String, String> body = new HashMap<>();
//         body.put("error", "Bad Request");
//         body.put("message", ex.getMessage());
//         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
//     }

//     @ExceptionHandler(Exception.class)
//     public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
//         Map<String, String> body = new HashMap<>();
//         body.put("error", "Internal Server Error");
//         body.put("message", ex.getMessage());
//         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
//     }
// }
