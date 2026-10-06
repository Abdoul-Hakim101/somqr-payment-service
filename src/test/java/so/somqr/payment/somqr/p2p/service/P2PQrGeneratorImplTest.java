package so.somqr.payment.somqr.p2p.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import so.somqr.payment.dto.GenerateP2PQrRequest;
import so.somqr.payment.exception.ApiException;
import so.somqr.payment.service.impl.P2PQrGeneratorImpl;
import so.somqr.payment.util.CrcCalculator;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class P2PQrGeneratorImplTest {

    private P2PQrGeneratorImpl generator;

    @BeforeEach
    void setUp() {
        generator = new P2PQrGeneratorImpl();
        ReflectionTestUtils.setField(generator, "schemeIdentifier", "01");
        ReflectionTestUtils.setField(generator, "fiName", "Galaxy International Bank");
    }

    @Test
    void shouldGenerateStaticQrWithoutAmountOrParticulars() {
        String payload = generator.generate(request("11", "SO1234", null, null));

        assertEquals(CrcCalculator.validateP2p(payload), payload.substring(payload.length() - 4));
    }

    @Test
    void shouldAllowZeroAmountForStaticQr() {
        assertDoesNotThrow(() -> generator.generate(
                request("11", "SO1234", BigDecimal.ZERO, null)
        ));
    }

    @Test
    void shouldRejectPositiveAmountForStaticQr() {
        assertThrows(ApiException.class, () -> generator.generate(
                request("11", "SO1234", BigDecimal.ONE, null)
        ));
    }

    @Test
    void shouldRejectParticularsForStaticQr() {
        assertThrows(ApiException.class, () -> generator.generate(
                request("11", "SO1234", null, "Invoice 1")
        ));
    }

    @Test
    void shouldGenerateDynamicQrWithPositiveAmountAndParticulars() {
        assertDoesNotThrow(() -> generator.generate(
                request("12", "SO1234", new BigDecimal("25.00"), "Invoice 1")
        ));
    }

    @Test
    void shouldRejectMissingOrNonPositiveDynamicAmount() {
        assertThrows(ApiException.class, () -> generator.generate(
                request("12", "SO1234", null, "Invoice 1")
        ));
        assertThrows(ApiException.class, () -> generator.generate(
                request("12", "SO1234", BigDecimal.ZERO, "Invoice 1")
        ));
        assertThrows(ApiException.class, () -> generator.generate(
                request("12", "SO1234", new BigDecimal("-1.00"), "Invoice 1")
        ));
    }

    @Test
    void shouldRejectMissingOrEmptyDynamicParticulars() {
        assertThrows(ApiException.class, () -> generator.generate(
                request("12", "SO1234", BigDecimal.ONE, null)
        ));
        assertThrows(ApiException.class, () -> generator.generate(
                request("12", "SO1234", BigDecimal.ONE, "   ")
        ));
    }

    @Test
    void shouldRejectAccountNumberThatIsNotSomaliIbanFormat() {
        assertThrows(ApiException.class, () -> generator.generate(
                request("11", "XX1234", null, null)
        ));
        assertThrows(ApiException.class, () -> generator.generate(
                request("11", "SO12AB", null, null)
        ));
    }

    private GenerateP2PQrRequest request(
            String pointOfInitiationMethod,
            String accountNumber,
            BigDecimal amount,
            String particulars
    ) {
        return new GenerateP2PQrRequest(
                pointOfInitiationMethod,
                accountNumber,
                "Amina Hassan",
                amount,
                particulars
        );
    }
}
