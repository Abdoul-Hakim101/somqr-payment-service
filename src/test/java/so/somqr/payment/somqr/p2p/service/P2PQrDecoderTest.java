package so.somqr.payment.somqr.p2p.service;

import org.junit.jupiter.api.Test;
import so.somqr.payment.domain.P2PSomQrPayload;
import so.somqr.payment.service.P2PQrDecoder;
import so.somqr.payment.service.impl.P2PQrDecoderImpl;
import so.somqr.payment.util.CrcCalculator;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class P2PQrDecoderTest {

    private final P2PQrDecoder decoder = new P2PQrDecoderImpl();

    @Test
    void shouldDecodeP2pPayload() {
        String data = "0002020102112702010304BANK04031230505AMINA060525.000703INV";
        String payload = data + "1004" + CrcCalculator.calculate(data + "1004");

        P2PSomQrPayload result = decoder.decode(payload);

        assertEquals("02", result.payloadFormatIndicator());
        assertEquals("11", result.pointOfInitiationMethod());
        assertEquals("01", result.schemeIdentifier());
        assertEquals("BANK", result.fiName());
        assertEquals("123", result.accountNumber());
        assertEquals("AMINA", result.accountHolderName());
        assertEquals(new BigDecimal("25.00"), result.amount());
        assertEquals("INV", result.particulars());
        assertEquals(4, result.crc().length());
    }
}
