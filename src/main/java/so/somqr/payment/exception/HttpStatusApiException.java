package so.somqr.payment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class HttpStatusApiException extends ApiException {

    private final HttpStatus status;

    public HttpStatusApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
