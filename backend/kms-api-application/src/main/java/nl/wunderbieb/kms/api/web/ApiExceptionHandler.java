package nl.wunderbieb.kms.api.web;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import nl.wunderbieb.kms.api.security.AccessDeniedException;
import nl.wunderbieb.kms.api.security.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(UnauthorizedException.class)
  ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException exception, HttpServletRequest request) {
    return error(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", exception.getMessage(), Map.of(), request);
  }

  @ExceptionHandler(AccessDeniedException.class)
  ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException exception, HttpServletRequest request) {
    return error(
        HttpStatus.FORBIDDEN,
        "ACCESS_DENIED_CAPABILITY",
        exception.getMessage(),
        Map.of("requiredCapability", exception.requiredCapability()),
        request
    );
  }

  @ExceptionHandler(NoSuchElementException.class)
  ResponseEntity<ApiError> handleNotFound(NoSuchElementException exception, HttpServletRequest request) {
    return error(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", exception.getMessage(), Map.of(), request);
  }

  @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
  ResponseEntity<ApiError> handleBadRequest(Exception exception, HttpServletRequest request) {
    return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", exception.getMessage(), Map.of(), request);
  }

  private ResponseEntity<ApiError> error(
      HttpStatus status,
      String code,
      String message,
      Map<String, Object> details,
      HttpServletRequest request
  ) {
    String traceId = (String) request.getAttribute(TraceIdFilter.TRACE_ID_ATTRIBUTE);
    return ResponseEntity.status(status).body(new ApiError(code, message, details, traceId, Instant.now()));
  }
}
