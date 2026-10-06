package so.somqr.payment.service;

import so.somqr.payment.dto.GenerateP2PQrRequest;

public interface P2PQrGenerator {

    String generate(GenerateP2PQrRequest request);
}
