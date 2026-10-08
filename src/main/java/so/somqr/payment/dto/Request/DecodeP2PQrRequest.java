package so.somqr.payment.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DecodeP2PQrRequest(
        @NotBlank(message = "QR payload is required")
        @Size(min = 8, max = 512, message = "QR payload must be between 8 and 512 characters")
        String payload
) {
}
