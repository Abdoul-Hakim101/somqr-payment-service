package so.somqr.payment.somqr.util;

import org.junit.jupiter.api.Test;
import so.somqr.payment.exception.ApiException;
import so.somqr.payment.util.CrcCalculator;

import static org.junit.jupiter.api.Assertions.*;

class CrcCalculatorTest {

    @Test
    void shouldCalculateStandardCrc16CcittFalseVector() {
        assertEquals("29B1", CrcCalculator.calculate("123456789"));
    }

    @Test
    void shouldValidatePayloadCrc() {
        String data = "0002016304";
        String payload = data + CrcCalculator.calculate(data);

        assertEquals(CrcCalculator.calculate(data), CrcCalculator.validateP2m(payload));
    }

    @Test
    void shouldRejectInvalidCrc() {
        ApiException exception = assertThrows(ApiException.class,
                () -> CrcCalculator.validateP2m("00020163040000"));

        assertTrue(exception.getMessage().contains("provided CRC '0000'"));
        assertTrue(exception.getMessage().contains("calculated CRC"));
    }

    @Test
    void shouldValidateP2pPayloadCrc() {
        String data = "0002020102111004";
        String payload = data + CrcCalculator.calculate(data);

        assertEquals(CrcCalculator.calculate(data), CrcCalculator.validateP2p(payload));
    }

    @Test
    void shouldRejectMissingCrcField() {
        assertThrows(ApiException.class, () -> CrcCalculator.validateP2m("000201"));
    }
}
