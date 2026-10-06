package so.somqr.payment.somqr.domain;

import org.junit.jupiter.api.Test;
import so.somqr.payment.domain.P2PSomQrPayload;
import so.somqr.payment.exception.ApiException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class P2PSomQrPayloadTest {

    @Test
    void shouldCreateValidP2pPayload() {
        P2PSomQrPayload payload = new P2PSomQrPayload(
                "02",
                "11",
                "01",
                "SIPS Bank",
                "123456789",
                "Amina Hassan",
                new BigDecimal("25.00"),
                "Invoice 1",
                null
        );

        assertEquals("02", payload.payloadFormatIndicator());
        assertEquals(new BigDecimal("25.00"), payload.amount());
    }

    @Test
    void shouldRejectNonP2pFormatIndicator() {
        assertThrows(ApiException.class, () -> new P2PSomQrPayload(
                "01", "11", "01", "Bank", "123", "Amina",
                BigDecimal.TEN, null, null
        ));
    }

    @Test
    void shouldRejectMissingSchemeIdentifier() {
        assertThrows(ApiException.class, () -> new P2PSomQrPayload(
                "02", "11", null, "Bank", "123", "Amina",
                BigDecimal.TEN, null, null
        ));
    }

    @Test
    void shouldAllowMaximumConfiguredFieldLengths() {
        P2PSomQrPayload payload = new P2PSomQrPayload(
                "02",
                "11",
                "01",
                "A".repeat(35),
                "1".repeat(35),
                "B".repeat(45),
                null,
                "C".repeat(30),
                "ABCD"
        );

        assertEquals(35, payload.fiName().length());
        assertEquals(35, payload.accountNumber().length());
        assertEquals(45, payload.accountHolderName().length());
        assertEquals(30, payload.particulars().length());
    }

    @Test
    void shouldRejectValuesOverOfficialMaximumLengths() {
        assertThrows(ApiException.class, () -> new P2PSomQrPayload(
                "02", "11", "01", "A".repeat(36), "123", "Amina",
                null, null, null
        ));

        assertThrows(ApiException.class, () -> new P2PSomQrPayload(
                "02", "11", "01", "Bank", "1".repeat(36), "Amina",
                null, null, null
        ));
    }

    @Test
    void shouldRejectInvalidPointOfInitiationMethod() {
        assertThrows(ApiException.class, () -> new P2PSomQrPayload(
                "02", "99", "01", "Bank", "123", "Amina",
                null, null, null
        ));
    }

    @Test
    void shouldRejectNegativeAmount() {
        assertThrows(ApiException.class, () -> new P2PSomQrPayload(
                "02", "11", "01", "Bank", "123", "Amina",
                new BigDecimal("-1.00"), null, null
        ));
    }
}
