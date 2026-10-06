package so.somqr.payment.util;

import so.somqr.payment.exception.ApiException;

import java.nio.charset.StandardCharsets;

/**
 * Calculates and validates the SomQR CRC-16/CCITT-FALSE checksum.
 * The checksum is calculated over the payload data plus the CRC tag and
 * length, excluding only the four-character CRC value.
 */
public final class CrcCalculator {

    private static final int INITIAL_VALUE = 0xFFFF;
    private static final int POLYNOMIAL = 0x1021;

    private CrcCalculator() {
    }

    public static String calculate(String data) {
        if (data == null) {
            throw new ApiException("Data cannot be null");
        }

        int crc = INITIAL_VALUE;

        for (byte value : data.getBytes(StandardCharsets.UTF_8)) {
            crc ^= (value & 0xFF) << 8;

            for (int bit = 0; bit < 8; bit++) {
                crc = (crc & 0x8000) != 0
                        ? (crc << 1) ^ POLYNOMIAL
                        : crc << 1;
                crc &= 0xFFFF;
            }
        }

        return String.format("%04X", crc);
    }

    public static String validateP2p(String payload) {
        return validate(payload, "10", "P2P");
    }

    public static String validateP2m(String payload) {
        return validate(payload, "63", "P2M");
    }

    private static String validate(String payload, String expectedTag, String qrType) {
        if (payload == null || payload.length() < 8) {
            throw new ApiException(qrType + " SomQR payload is too short to contain a CRC");
        }

        if (expectedTag == null || !expectedTag.matches("\\d{2}")) {
            throw new ApiException("Expected CRC tag must contain exactly two digits");
        }

        int crcStart = payload.length() - 8;
        String crcTag = payload.substring(crcStart, crcStart + 2);
        String crcLength = payload.substring(crcStart + 2, crcStart + 4);
        String providedCrc = payload.substring(crcStart + 4).toUpperCase();

        if (!expectedTag.equals(crcTag) || !"04".equals(crcLength)) {
            throw new ApiException(
                    qrType + " SomQR payload must end with tag " + expectedTag + " and length 04"
            );
        }

        String calculatedCrc = calculate(payload.substring(0, payload.length() - 4));
        if (!calculatedCrc.equals(providedCrc)) {
            throw new ApiException(qrType + " SomQR CRC validation failed: the provided CRC '"
                    + providedCrc + "' does not match the calculated CRC '" + calculatedCrc + "'.");
        }

        return calculatedCrc;
    }
}
