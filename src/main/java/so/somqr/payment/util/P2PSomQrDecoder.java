package so.somqr.payment.util;

import so.somqr.payment.domain.Tlv;
import so.somqr.payment.exception.ApiException;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses a SomQR payload into its top-level TLV fields.
 */
public final class P2PSomQrDecoder {

    private static final int HEADER_LENGTH = 4;

    private P2PSomQrDecoder() {
        // Utility class; do not instantiate.
    }

    public static List<Tlv> decodeP2p(String payload) {
        CrcCalculator.validateP2p(payload);
        return parseFields(removeCrc(payload));
    }

    private static String removeCrc(String payload) {
        return payload.substring(0, payload.length() - 8);
    }

    private static List<Tlv> parseFields(String payload) {
        if (payload == null || payload.isBlank()) {
            throw new ApiException("P2P SomQR payload cannot be empty");
        }

        List<Tlv> fields = new ArrayList<>();
        int position = 0;

        while (position < payload.length()) {
            if (payload.length() - position < HEADER_LENGTH) {
                throw new ApiException("P2P SomQR payload contains an incomplete TLV header");
            }

            String tag = payload.substring(position, position + 2);
            String lengthText = payload.substring(position + 2, position + 4);

            if (!tag.matches("\\d{2}")) {
                throw new ApiException("P2P SomQR tag must contain exactly two digits");
            }

            if (!lengthText.matches("\\d{2}")) {
                throw new ApiException("P2P SomQR length must contain exactly two digits");
            }

            int length = Integer.parseInt(lengthText);
            int valueStart = position + HEADER_LENGTH;
            int valueEnd = valueStart + length;

            if (valueEnd > payload.length()) {
                throw new ApiException("P2P SomQR TLV value is shorter than its declared length");
            }

            String value = payload.substring(valueStart, valueEnd);
            fields.add(new Tlv(tag, value));
            position = valueEnd;
        }

        return List.copyOf(fields);
    }
}
