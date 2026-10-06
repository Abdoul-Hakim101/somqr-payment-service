package so.somqr.payment.service;

import so.somqr.payment.domain.P2PSomQrPayload;

public interface P2PQrDecoder {

    P2PSomQrPayload decode(String qrPayload) ;
}
