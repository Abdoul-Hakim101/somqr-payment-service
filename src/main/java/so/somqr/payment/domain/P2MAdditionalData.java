package so.somqr.payment.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.ALWAYS)
public record P2MAdditionalData(
        String billNumber,
        String mobileNumber,
        String storeLabel,
        String loyaltyNumber,
        String referenceLabel,
        String customerLabel,
        String terminalLabel,
        String purposeOfTransaction,
        String additionalConsumerDataRequest
) {
}
