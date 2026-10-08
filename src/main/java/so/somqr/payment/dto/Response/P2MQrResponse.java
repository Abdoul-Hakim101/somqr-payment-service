package so.somqr.payment.dto.Response;

import so.somqr.payment.domain.P2MAdditionalData;
import so.somqr.payment.domain.P2MMerchantAccount;
import so.somqr.payment.domain.P2MSomQrPayload;

import java.math.BigDecimal;
import java.util.Map;

public record P2MQrResponse(
        String payloadFormatIndicator,
        String pointOfInitiationMethod,
        Map<String, P2MMerchantAccount> merchantAccountInformation,
        Integer merchantCategoryCode,
        Integer transactionCurrency,
        BigDecimal transactionAmount,
        Integer tipOrConvenienceIndicator,
        String valueOfConvenienceFeeFixed,
        String valueOfConvenienceFeePercentage,
        String countryCode,
        String merchantName,
        String merchantCity,
        String postalCode,
        P2MAdditionalData additionalData,
        String crc
) {

    public static P2MQrResponse from(P2MSomQrPayload payload) {
        return new P2MQrResponse(
                payload.payloadFormatIndicator(),
                payload.pointOfInitiationMethod(),
                payload.merchantAccountInformation(),
                payload.merchantCategoryCode(),
                payload.transactionCurrency(),
                payload.transactionAmount(),
                payload.tipOrConvenienceIndicator(),
                payload.valueOfConvenienceFeeFixed(),
                payload.valueOfConvenienceFeePercentage(),
                payload.countryCode(),
                payload.merchantName(),
                payload.merchantCity(),
                payload.postalCode(),
                payload.additionalData(),
                payload.crc()
        );
    }
}
