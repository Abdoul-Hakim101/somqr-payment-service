package so.somqr.payment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "An unexpected error occurred.";
    private final HttpStatus status;

    // Default constructor (BAD_REQUEST)
    public ApiException(String message) {
        super(message != null && !message.trim().isEmpty() ? message : DEFAULT_MESSAGE);
        this.status = HttpStatus.BAD_REQUEST;
    }

    // Constructor with HttpStatus and message
    public ApiException(HttpStatus status, String message) {
        super(message != null && !message.trim().isEmpty() ? message : DEFAULT_MESSAGE);
        this.status = status != null ? status : HttpStatus.BAD_REQUEST;
    }

    // Constructor with message and cause (BAD_REQUEST)
    public ApiException(String message, Throwable cause) {
        super(message != null && !message.trim().isEmpty() ? message : DEFAULT_MESSAGE, cause);
        this.status = HttpStatus.BAD_REQUEST;
    }

    // Constructor with HttpStatus, message, and cause
    public ApiException(HttpStatus status, String message, Throwable cause) {
        super(message != null && !message.trim().isEmpty() ? message : DEFAULT_MESSAGE, cause);
        this.status = status != null ? status : HttpStatus.BAD_REQUEST;
    }

    // Constructor with cause (INTERNAL_SERVER_ERROR)
    public ApiException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

}