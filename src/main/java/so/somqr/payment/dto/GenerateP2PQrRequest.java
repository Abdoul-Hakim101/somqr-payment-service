package so.somqr.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record GenerateP2PQrRequest(
        @NotBlank(message = "Point of initiation method is required")
        @Pattern(regexp = "11|12", message = "Point of initiation method must be 11 for static QR or 12 for dynamic QR")
        String pointOfInitiationMethod,

        @NotBlank(message = "Account number is required")
        @Size(max = 35, message = "Account number cannot exceed 35 characters")
        String accountNumber,

        @NotBlank(message = "Account holder name is required")
        @Size(max = 45, message = "Account holder name cannot exceed 45 characters")
        String accountHolderName,

        @DecimalMin(value = "0.00", message = "Amount must be zero or greater")
        BigDecimal amount,

        @Size(max = 30, message = "Particulars cannot exceed 30 characters")
        String particulars
) {
}
