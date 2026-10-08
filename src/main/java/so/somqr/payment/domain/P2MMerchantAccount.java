package so.somqr.payment.domain;

import java.util.Map;

public record P2MMerchantAccount(
        String globalUniqueIdentifier,
        Map<String, String> paymentNetworkSpecific
) {
    public P2MMerchantAccount {
        paymentNetworkSpecific = paymentNetworkSpecific == null
                ? Map.of()
                : Map.copyOf(paymentNetworkSpecific);
    }
}
