package so.somqr.payment.service;

import so.somqr.payment.domain.P2MSomQrPayload;

public interface P2MQrDecoder {
    P2MSomQrPayload decode(String qrPayload);
}
