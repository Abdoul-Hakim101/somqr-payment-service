package so.somqr.payment.util;


import so.somqr.payment.domain.Tlv;
import so.somqr.payment.exception.ApiException;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses a P2M SomQR payload into top-level and nested TLV fields.
 */
public final class P2MSomQrDecoder {

    private static final int HEADER_LENGTH = 4;
    private static final int CRC_FIELD_LENGTH = 8;
    private static final int MERCHANT_ACCOUNT_INFO_MIN_TAG = 26;
    private static final int MERCHANT_ACCOUNT_INFO_MAX_TAG = 51;
    private static final String ADDITIONAL_DATA_TAG = "62";

    private P2MSomQrDecoder() {
        // Utility class; do not instantiate.
    }

    public static List<Tlv> decodeP2m(String payload) {
        CrcCalculator.validateP2m(payload);
        return parseFields(removeCrc(payload), true);
    }

    private static String removeCrc(String payload) {
        return payload.substring(0, payload.length() - CRC_FIELD_LENGTH);
    }

    private static List<Tlv> parseFields(String payload, boolean allowTemplates) {

        List<Tlv> fields = new ArrayList<>();
        int position = 0;

        while (position < payload.length()) {
            if (payload.length() - position < HEADER_LENGTH) {
                throw new ApiException("P2M SomQR payload contains an incomplete TLV header");
            }

            String tag = payload.substring(position, position + 2);
            String lengthText = payload.substring(position + 2, position + 4);

            if (!tag.matches("\\d{2}")) {
                throw new ApiException("P2M SomQR tag must contain exactly two digits");
            }

            if (!lengthText.matches("\\d{2}")) {
                throw new ApiException("P2M SomQR length must contain exactly two digits");
            }

            int length = Integer.parseInt(lengthText);
            int valueStart = position + HEADER_LENGTH;
            int valueEnd = valueStart + length;

            if (valueEnd > payload.length()) {
                throw new ApiException("P2M SomQR TLV value is shorter than its declared length");
            }

            String value = payload.substring(valueStart, valueEnd);
            List<Tlv> children = allowTemplates && isTemplate(tag)
                    ? parseFields(value, false)
                    : List.of();
            fields.add(new Tlv(tag, value, children));
            position = valueEnd;
        }

        return List.copyOf(fields);
    }

    private static boolean isTemplate(String tag) {
        int numericTag = Integer.parseInt(tag);
        return (numericTag >= MERCHANT_ACCOUNT_INFO_MIN_TAG
                && numericTag <= MERCHANT_ACCOUNT_INFO_MAX_TAG)
                || ADDITIONAL_DATA_TAG.equals(tag);
    }
}
