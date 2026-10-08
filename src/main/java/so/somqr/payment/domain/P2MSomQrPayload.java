package so.somqr.payment.domain;

import so.somqr.payment.exception.ApiException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * Domain representation of a person-to-merchant SomQR payload.
 *
 * <p>The merchant-account and additional-data fields are kept as TLV lists
 * because P2M payloads may contain nested data and the structure differs
 * between FI and MNO merchant profiles.</p>
 */
public record P2MSomQrPayload(
        String payloadFormatIndicator,
        String pointOfInitiationMethod,
        Map<String, P2MMerchantAccount> merchantAccountInformation,
        Integer merchantCategoryCode,
        Integer transactionCurrency,
        BigDecimal transactionAmount,
        Integer tipOrConvenienceIndicator,
        String valueOfConvenienceFeeFixed,
        String valueOfConvenienceFeePercentage,
        String countryCode,
        String merchantName,
        String merchantCity,
        String postalCode,
        P2MAdditionalData additionalData,
        String crc
) {

    public P2MSomQrPayload {
        requireValue("payloadFormatIndicator", payloadFormatIndicator);
        if (!"01".equals(payloadFormatIndicator)) {
            throw new ApiException("P2M payload format indicator must be 01");
        }

        requireValue("pointOfInitiationMethod", pointOfInitiationMethod);
        if (!"11".equals(pointOfInitiationMethod) && !"12".equals(pointOfInitiationMethod)) {
            throw new ApiException("P2M point of initiation method must be 11 or 12");
        }

        merchantAccountInformation = Map.copyOf(
                Objects.requireNonNull(merchantAccountInformation, "Merchant account information cannot be null")
        );
        if (merchantAccountInformation.isEmpty()) {
            throw new ApiException("P2M merchant account information is required");
        }

        requireValue("merchantCategoryCode", merchantCategoryCode);
        requireValue("transactionCurrency", transactionCurrency);
        requireLength("countryCode", countryCode, 2);
        requireLength("merchantName", merchantName, 25);
        requireLength("merchantCity", merchantCity, 15);
        requireLength("postalCode", postalCode, 10);
        requireLength("crc", crc, 4);

        if (transactionAmount != null && transactionAmount.signum() < 0) {
            throw new ApiException("P2M transaction amount must be zero or greater");
        }

        if ("12".equals(pointOfInitiationMethod)
                && (transactionAmount == null || transactionAmount.signum() <= 0)) {
            throw new ApiException(
                    "For P2M point of initiation method 12, transaction amount must be greater than 0.00"
            );
        }
    }

    private static void requireValue(String fieldName, Object value) {
        if (value == null || (value instanceof String string && string.isBlank())) {
            throw new ApiException(fieldName + " is required");
        }
    }

    private static void requireLength(String fieldName, String value, int maxLength) {
        if (value != null && value.length() > maxLength) {
            throw new ApiException(fieldName + " cannot exceed " + maxLength + " characters");
        }
    }
}
