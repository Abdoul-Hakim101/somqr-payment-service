package so.somqr.payment.domain;

import so.somqr.payment.exception.ApiException;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Domain representation of a person-to-person SomQR payload.
 *
 * <p>The field names and maximum lengths mirror the P2PPayload
 * implementation. The payload format indicator must be {@code 02} for P2P.</p>
 */
public record P2PSomQrPayload(
        String payloadFormatIndicator,
        String pointOfInitiationMethod,
        String schemeIdentifier,
        String fiName,
        String accountNumber,
        String accountHolderName,
        BigDecimal amount,
        String particulars,
        String crc
) {

    public P2PSomQrPayload {
        requireLength("payloadFormatIndicator", payloadFormatIndicator, 2, true);
        if (!"02".equals(payloadFormatIndicator)) {
            throw new ApiException("P2P payload format indicator must be 02");
        }

        requireLength("pointOfInitiationMethod", pointOfInitiationMethod, 2, true);
        if (!"11".equals(pointOfInitiationMethod) && !"12".equals(pointOfInitiationMethod)) {
            throw new ApiException("Point of initiation method must be 11 or 12");
        }

        requireLength("schemeIdentifier", schemeIdentifier, 2, true);
        if (!Set.of("01", "02", "03").contains(schemeIdentifier)) {
            throw new ApiException("Scheme identifier must be 01, 02, or 03");
        }

        requireLength("fiName", fiName, 35, false);
        requireLength("accountNumber", accountNumber, 35, true);
        requireLength("accountHolderName", accountHolderName, 45, true);
        requireLength("particulars", particulars, 30, false);
        requireLength("crc", crc, 4, false);

        if (amount != null && amount.signum() < 0) {
            throw new ApiException("Amount must be zero or greater");
        }
    }

    private static void requireLength(String fieldName, String value, int maxLength, boolean required) {
        if (required && (value == null || value.isBlank())) {
            throw new ApiException(fieldName + " is required");
        }

        if (value != null && value.length() > maxLength) {
            throw new ApiException(fieldName + " cannot exceed " + maxLength + " characters");
        }
    }
}
