package so.somqr.payment.somqr.p2m.util;

import org.junit.jupiter.api.Test;
import so.somqr.payment.domain.Tlv;
import so.somqr.payment.exception.ApiException;
import so.somqr.payment.util.CrcCalculator;
import so.somqr.payment.util.P2MSomQrDecoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class P2MSomQrDecoderTest {

    private static final String STATIC_P2M_PAYLOAD =
            "00020101021126430013so.somqr.sips010601001444123050050078885204274153038405802SO5905Gafal6009Mogadishu63049FE6";

    @Test
    void shouldDecodeTopLevelAndNestedMerchantAccountFields() {
        List<Tlv> fields = P2MSomQrDecoder.decodeP2m(STATIC_P2M_PAYLOAD);

        assertEquals(8, fields.size());
        Tlv merchantAccount = fields.stream()
                .filter(field -> field.tag().equals("26"))
                .findFirst()
                .orElseThrow();

        assertEquals(3, merchantAccount.children().size());
        assertEquals("so.somqr.sips", merchantAccount.children().get(0).value());
        assertEquals("010014", merchantAccount.children().get(1).value());
        assertEquals("305005007888", merchantAccount.children().get(2).value());
    }

    @Test
    void shouldDecodeAdditionalDataWhenTag62IsPresent() {
        String body = "00020101021126430013so.somqr.sips010601001444123050050078885204274153038405802SO5905Gafal6009Mogadishu62070303ABC";
        String payload = withP2mCrc(body);

        List<Tlv> fields = P2MSomQrDecoder.decodeP2m(payload);

        Tlv additionalData = fields.stream()
                .filter(field -> field.tag().equals("62"))
                .findFirst()
                .orElseThrow();

        assertEquals("03", additionalData.children().get(0).tag());
        assertEquals("ABC", additionalData.children().get(0).value());
    }

    @Test
    void shouldRejectInvalidCrc() {
        String invalidPayload = STATIC_P2M_PAYLOAD.substring(0, STATIC_P2M_PAYLOAD.length() - 1) + "0";

        assertThrows(ApiException.class, () -> P2MSomQrDecoder.decodeP2m(invalidPayload));
    }

    @Test
    void shouldRejectValueShorterThanDeclaredLength() {
        String body = "000301";

        assertThrows(ApiException.class, () -> P2MSomQrDecoder.decodeP2m(withP2mCrc(body)));
    }

    private String withP2mCrc(String body) {
        String crcInput = body + "6304";
        return crcInput + CrcCalculator.calculate(crcInput);
    }
}
