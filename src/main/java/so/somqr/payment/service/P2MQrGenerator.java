package so.somqr.payment.service;

import so.somqr.payment.dto.Request.GenerateP2MQrRequest;

public interface P2MQrGenerator {

    String generateFi(GenerateP2MQrRequest request);

    String generateMno(GenerateP2MQrRequest request);
}
