package so.somqr.payment.service.impl;

import org.springframework.stereotype.Service;
import so.somqr.payment.domain.P2MAdditionalData;
import so.somqr.payment.domain.P2MMerchantAccount;
import so.somqr.payment.domain.P2MSomQrPayload;
import so.somqr.payment.domain.Tlv;
import so.somqr.payment.exception.ApiException;
import so.somqr.payment.service.P2MQrDecoder;
import so.somqr.payment.util.CrcCalculator;
import so.somqr.payment.util.P2MSomQrDecoder;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class P2MQrDecoderImpl implements P2MQrDecoder {

    @Override
    public P2MSomQrPayload decode(String qrPayload) {
        String crc = CrcCalculator.validateP2m(qrPayload);
        List<Tlv> fields = P2MSomQrDecoder.decodeP2m(qrPayload);

        return new P2MSomQrPayload(
                valueOf(fields, "00"),
                valueOf(fields, "01"),
                merchantAccountInformation(fields),
                integerValueOf(fields, "52", "merchant category code"),
                integerValueOf(fields, "53", "transaction currency"),
                transactionAmount(fields),
                integerValueOfOrNull(fields, "55", "tip or convenience indicator"),
                valueOf(fields, "56"),
                valueOf(fields, "57"),
                valueOf(fields, "58"),
                valueOf(fields, "59"),
                valueOf(fields, "60"),
                valueOf(fields, "61"),
                additionalData(fields),
                crc
        );
    }

    private Map<String, P2MMerchantAccount> merchantAccountInformation(List<Tlv> fields) {
        Map<String, P2MMerchantAccount> merchantAccounts = new LinkedHashMap<>();

        fields.stream()
                .filter(field -> isMerchantAccountInformation(field.tag()))
                .forEach(field -> {
                    String globalUniqueIdentifier = field.children().stream()
                            .filter(child -> child.tag().equals("00"))
                            .map(Tlv::value)
                            .findFirst()
                            .orElse(field.children().isEmpty() ? field.value() : null);

                    Map<String, String> paymentNetworkSpecific = new LinkedHashMap<>();
                    field.children().stream()
                            .filter(child -> !child.tag().equals("00"))
                            .forEach(child -> paymentNetworkSpecific.put(child.tag(), child.value()));

                    merchantAccounts.put(
                            field.tag(),
                            new P2MMerchantAccount(globalUniqueIdentifier, paymentNetworkSpecific)
                    );
                });

        return merchantAccounts;
    }

    private P2MAdditionalData additionalData(List<Tlv> fields) {
        List<Tlv> children = fields.stream()
                .filter(field -> field.tag().equals("62"))
                .flatMap(field -> field.children().stream())
                .toList();

        return new P2MAdditionalData(
                valueOf(children, "01"),
                valueOf(children, "02"),
                valueOf(children, "03"),
                valueOf(children, "04"),
                valueOf(children, "05"),
                valueOf(children, "06"),
                valueOf(children, "07"),
                valueOf(children, "08"),
                valueOf(children, "09")
        );
    }

    private boolean isMerchantAccountInformation(String tag) {
        int numericTag = Integer.parseInt(tag);
        return numericTag >= 26 && numericTag <= 51;
    }

    private String valueOf(List<Tlv> fields, String tag) {
        return fields.stream()
                .filter(field -> field.tag().equals(tag))
                .map(Tlv::value)
                .findFirst()
                .orElse(null);
    }

    private BigDecimal transactionAmount(List<Tlv> fields) {
        String value = valueOf(fields, "54");
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return new BigDecimal(value);
        } catch (NumberFormatException exception) {
            throw new ApiException("P2M transaction amount is not a valid number", exception);
        }
    }

    private Integer integerValueOf(List<Tlv> fields, String tag, String fieldName) {
        String value = valueOf(fields, tag);
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException exception) {
            throw new ApiException("P2M " + fieldName + " is not a valid integer", exception);
        }
    }

    private Integer integerValueOfOrNull(List<Tlv> fields, String tag, String fieldName) {
        return integerValueOf(fields, tag, fieldName);
    }
}
