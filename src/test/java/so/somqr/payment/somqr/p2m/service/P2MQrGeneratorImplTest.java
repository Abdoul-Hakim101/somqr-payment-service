package so.somqr.payment.somqr.p2m.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import so.somqr.payment.domain.P2MSomQrPayload;
import so.somqr.payment.dto.Request.GenerateP2MQrRequest;
import so.somqr.payment.exception.ApiException;
import so.somqr.payment.service.impl.P2MQrDecoderImpl;
import so.somqr.payment.service.impl.P2MQrGeneratorImpl;
import so.somqr.payment.util.CrcCalculator;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class P2MQrGeneratorImplTest {

    private P2MQrGeneratorImpl generator;
    private P2MQrDecoderImpl decoder;

    @BeforeEach
    void setUp() {
        generator = new P2MQrGeneratorImpl();
        decoder = new P2MQrDecoderImpl();
        ReflectionTestUtils.setField(generator, "fiAcquirerId", "0014");
        ReflectionTestUtils.setField(generator, "mnoNetworkId", "01");
    }

    @Test
    void shouldGenerateAndDecodeStaticFiQrWithoutAmount() {
        String payload = generator.generateFi(request("11", null));

        assertEquals(CrcCalculator.validateP2m(payload), payload.substring(payload.length() - 4));
        P2MSomQrPayload decoded = decoder.decode(payload);

        assertEquals("01", decoded.payloadFormatIndicator());
        assertEquals("11", decoded.pointOfInitiationMethod());
        assertEquals(840, decoded.transactionCurrency());
        assertEquals("Gafal", decoded.merchantName());
        assertEquals("Mogadishu", decoded.merchantCity());
        assertEquals("010014", decoded.merchantAccountInformation()
                .get("26").paymentNetworkSpecific().get("01"));
        assertEquals("305005007888", decoded.merchantAccountInformation()
                .get("26").paymentNetworkSpecific().get("44"));
        assertNull(decoded.transactionAmount());
    }

    @Test
    void shouldGenerateAndDecodeDynamicFiQrWithAmount() {
        GenerateP2MQrRequest request = request("12", new BigDecimal("8.33"));

        P2MSomQrPayload decoded = decoder.decode(generator.generateFi(request));

        assertEquals(new BigDecimal("8.33"), decoded.transactionAmount());
    }

    @Test
    void shouldGenerateStaticMnoQrWithNetworkId() {
        String payload = generator.generateMno(request("11", BigDecimal.ZERO));

        assertEquals(CrcCalculator.validateP2m(payload), payload.substring(payload.length() - 4));
        assertTrue(payload.contains("310201"));
        assertFalse(payload.contains("540"));
    }

    @Test
    void shouldGenerateDynamicMnoQrWithAdditionalData() {
        GenerateP2MQrRequest request = new GenerateP2MQrRequest(
                "12", 840, new BigDecimal("8.33"), "305005007888", 2741,
                "Gafal", "Mogadishu", null, "Store 1", "Terminal 1",
                "Payment", "Reference 1"
        );

        P2MSomQrPayload decoded = decoder.decode(generator.generateMno(request));

        assertEquals(new BigDecimal("8.33"), decoded.transactionAmount());
        assertEquals("Store 1", decoded.additionalData().storeLabel());
        assertEquals("Reference 1", decoded.additionalData().referenceLabel());
        assertEquals("Terminal 1", decoded.additionalData().terminalLabel());
        assertEquals("Payment", decoded.additionalData().purposeOfTransaction());
    }

    @Test
    void shouldRejectPositiveAmountForStaticQr() {
        assertThrows(ApiException.class, () -> generator.generateFi(
                request("11", new BigDecimal("1.00"))
        ));
    }

    @Test
    void shouldRejectMissingOrNonPositiveAmountForDynamicQr() {
        assertThrows(ApiException.class, () -> generator.generateFi(request("12", null)));
        assertThrows(ApiException.class, () -> generator.generateFi(request("12", BigDecimal.ZERO)));
        assertThrows(ApiException.class, () -> generator.generateFi(
                request("12", new BigDecimal("-1.00"))
        ));
    }

    @Test
    void shouldRejectInvalidFiAcquirerId() {
        ReflectionTestUtils.setField(generator, "fiAcquirerId", "10014");

        assertThrows(ApiException.class, () -> generator.generateFi(request("11", null)));
    }

    @Test
    void shouldRejectInvalidMnoNetworkId() {
        ReflectionTestUtils.setField(generator, "mnoNetworkId", "03");

        assertThrows(ApiException.class, () -> generator.generateMno(request("11", null)));
    }

    private GenerateP2MQrRequest request(String pointOfInitializationMethod, BigDecimal amount) {
        return new GenerateP2MQrRequest(
                pointOfInitializationMethod,
                840,
                amount,
                "305005007888",
                2741,
                "Gafal",
                "Mogadishu",
                null,
                null,
                null,
                null,
                null
        );
    }
}
