package so.somqr.payment.dto;

import so.somqr.payment.domain.P2PSomQrPayload;

import java.math.BigDecimal;

public record P2PQrResponse(
        String payloadFormatIndicator,
        String pointOfInitiationMethod,
        String schemeIdentifier,
        String fiName,
        String accountNumber,
        String accountHolderName,
        BigDecimal amount,
        String particulars,
        String crc
) {

    public static P2PQrResponse from(P2PSomQrPayload payload) {
        return new P2PQrResponse(
                payload.payloadFormatIndicator(),
                payload.pointOfInitiationMethod(),
                payload.schemeIdentifier(),
                payload.fiName(),
                payload.accountNumber(),
                payload.accountHolderName(),
                payload.amount(),
                payload.particulars(),
                payload.crc()
        );
    }
}
