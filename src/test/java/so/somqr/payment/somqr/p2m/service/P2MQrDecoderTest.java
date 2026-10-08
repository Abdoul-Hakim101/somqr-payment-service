package so.somqr.payment.somqr.p2m.service;

import org.junit.jupiter.api.Test;
import so.somqr.payment.domain.P2MSomQrPayload;
import so.somqr.payment.service.impl.P2MQrDecoderImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class P2MQrDecoderTest {

    private static final String STATIC_P2M_PAYLOAD =
            "00020101021126430013so.somqr.sips010601001444123050050078885204274153038405802SO5905Gafal6009Mogadishu63049FE6";

    private final P2MQrDecoderImpl decoder = new P2MQrDecoderImpl();

    @Test
    void shouldDecodeStaticP2mPayload() {
        P2MSomQrPayload result = decoder.decode(STATIC_P2M_PAYLOAD);

        assertEquals("01", result.payloadFormatIndicator());
        assertEquals("11", result.pointOfInitiationMethod());
        assertEquals(2741, result.merchantCategoryCode());
        assertEquals(840, result.transactionCurrency());
        assertEquals("SO", result.countryCode());
        assertEquals("Gafal", result.merchantName());
        assertEquals("Mogadishu", result.merchantCity());
        assertEquals("9FE6", result.crc());
        assertNotNull(result.additionalData());
        assertEquals(1, result.merchantAccountInformation().size());
        assertEquals("so.somqr.sips",
                result.merchantAccountInformation().get("26").globalUniqueIdentifier());
        assertEquals("305005007888",
                result.merchantAccountInformation().get("26").paymentNetworkSpecific().get("44"));
    }
}
