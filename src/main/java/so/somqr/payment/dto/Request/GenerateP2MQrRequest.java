package so.somqr.payment.dto.Request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record GenerateP2MQrRequest(
        @NotBlank(message = "Point of initialization method is required")
        @Pattern(
                regexp = "11|12",
                message = "Point of initialization method must be 11 for static QR or 12 for dynamic QR"
        )
        String pointOfInitializationMethod,

        @NotNull(message = "Transaction currency is required")
        @Min(value = 1, message = "Transaction currency must be greater than 0")
        @Max(value = 999, message = "Transaction currency must not exceed 3 digits")
        Integer transactionCurrency,

        @DecimalMin(value = "0.00", message = "Transaction amount must be zero or greater")
        @Digits(integer = 13, fraction = 2, message = "Transaction amount must have up to 13 integer digits and 2 decimal places")
        BigDecimal transactionAmount,

        @NotBlank(message = "Merchant ID is required")
        @Size(max = 14, message = "Merchant ID cannot exceed 14 characters")
        @Pattern(regexp = "[A-Za-z0-9]+", message = "Merchant ID must contain alphanumeric characters only")
        String merchantId,

        @NotNull(message = "Merchant category code is required")
        @Min(value = 0, message = "Merchant category code cannot be negative")
        @Max(value = 9999, message = "Merchant category code must not exceed 4 digits")
        Integer merchantCategoryCode,

        @NotBlank(message = "Merchant name is required")
        @Size(max = 40, message = "Merchant name cannot exceed 40 characters")
        String merchantName,

        @NotBlank(message = "Merchant city is required")
        @Size(max = 30, message = "Merchant city cannot exceed 30 characters")
        String merchantCity,

        @Size(max = 10, message = "Postal code cannot exceed 10 characters")
        String postalCode,

        @Size(max = 26, message = "Store label cannot exceed 26 characters")
        String storeLabel,

        @Size(max = 26, message = "Terminal label cannot exceed 26 characters")
        String terminalLabel,

        @Size(max = 26, message = "Purpose of transaction cannot exceed 26 characters")
        String purposeOfTransaction,

        @Size(max = 26, message = "Reference label cannot exceed 26 characters")
        String referenceLabel
) {
}
