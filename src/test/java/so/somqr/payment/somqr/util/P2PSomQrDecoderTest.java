package so.somqr.payment.somqr.util;

import org.junit.jupiter.api.Test;
import so.somqr.payment.domain.Tlv;
import so.somqr.payment.exception.ApiException;
import so.somqr.payment.util.CrcCalculator;
import so.somqr.payment.util.P2PSomQrDecoder;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class P2PSomQrDecoderTest {

    @Test
    void shouldDecodeMultipleTopLevelFields() {
        List<Tlv> fields = P2PSomQrDecoder.decodeP2p(withP2pCrc("0002020102110402SO"));

        assertEquals(3, fields.size());
        assertEquals(new Tlv("00", "02"), fields.get(0));
        assertEquals(new Tlv("01", "11"), fields.get(1));
        assertEquals(new Tlv("04", "SO"), fields.get(2));
    }

    @Test
    void shouldRejectEmptyPayload() {
        assertThrows(ApiException.class, () -> P2PSomQrDecoder.decodeP2p(withP2pCrc("")));
    }

    @Test
    void shouldRejectIncompleteHeader() {
        assertThrows(ApiException.class, () -> P2PSomQrDecoder.decodeP2p(withP2pCrc("000")));
    }

    @Test
    void shouldRejectNonNumericTag() {
        assertThrows(ApiException.class, () -> P2PSomQrDecoder.decodeP2p(withP2pCrc("AA02SO")));
    }

    @Test
    void shouldRejectNonNumericLength() {
        assertThrows(ApiException.class, () -> P2PSomQrDecoder.decodeP2p(withP2pCrc("00A2SO")));
    }

    @Test
    void shouldRejectValueShorterThanDeclaredLength() {
        assertThrows(ApiException.class, () -> P2PSomQrDecoder.decodeP2p(withP2pCrc("0003SO")));
    }

    private String withP2pCrc(String body) {
        String crcInput = body + "1004";
        return crcInput + CrcCalculator.calculate(crcInput);
    }
}
