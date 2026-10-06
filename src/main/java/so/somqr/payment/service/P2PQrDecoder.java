package so.somqr.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import so.somqr.payment.domain.P2PSomQrPayload;
import so.somqr.payment.domain.Tlv;
import so.somqr.payment.exception.ApiException;
import so.somqr.payment.util.CrcCalculator;
import so.somqr.payment.util.P2PSomQrDecoder;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class P2PQrDecoder {

    public P2PSomQrPayload decode(String qrPayload) {
        String crc = CrcCalculator.validateP2p(qrPayload);
        List<Tlv> fields = P2PSomQrDecoder.decodeP2p(qrPayload);

        return new P2PSomQrPayload(
                valueOf(fields, "00"),
                valueOf(fields, "01"),
                valueOf(fields, "27"),
                valueOf(fields, "03"),
                valueOf(fields, "04"),
                valueOf(fields, "05"),
                decimalValueOf(fields),
                valueOf(fields, "07"),
                crc
        );
    }

    private String valueOf(List<Tlv> fields, String tag) {
        return fields.stream()
                .filter(field -> field.tag().equals(tag))
                .map(Tlv::value)
                .findFirst()
                .orElse(null);
    }

    private BigDecimal decimalValueOf(List<Tlv> fields) {
        String value = valueOf(fields, "06");
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return new BigDecimal(value);
        } catch (NumberFormatException exception) {
            throw new ApiException("P2P amount is not a valid number", exception);
        }
    }
}
