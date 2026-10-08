package so.somqr.payment.service;

import so.somqr.payment.dto.Request.GenerateP2PQrRequest;

public interface P2PQrGenerator {

    String generate(GenerateP2PQrRequest request);
}
