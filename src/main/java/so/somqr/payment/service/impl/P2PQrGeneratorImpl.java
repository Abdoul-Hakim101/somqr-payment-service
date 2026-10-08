package so.somqr.payment.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import so.somqr.payment.domain.P2PSomQrPayload;
import so.somqr.payment.domain.Tlv;
import so.somqr.payment.dto.Request.GenerateP2PQrRequest;
import so.somqr.payment.exception.ApiException;
import so.somqr.payment.service.P2PQrGenerator;
import so.somqr.payment.util.CrcCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class P2PQrGeneratorImpl implements P2PQrGenerator {

    @Value("${somqr.p2p.scheme-identifier}")
    private String schemeIdentifier;

    @Value("${somqr.p2p.fi-name}")
    private String fiName;

    @Override
    public String generate(GenerateP2PQrRequest request) {
        validateRequest(request);

        P2PSomQrPayload payload = new P2PSomQrPayload(
                "02",
                request.pointOfInitiationMethod(),
                schemeIdentifier,
                fiName,
                request.accountNumber(),
                request.accountHolderName(),
                request.amount(),
                request.particulars(),
                null
        );

        List<Tlv> fields = new ArrayList<>();
        fields.add(new Tlv("00", payload.payloadFormatIndicator()));
        fields.add(new Tlv("01", payload.pointOfInitiationMethod()));
        fields.add(new Tlv("27", payload.schemeIdentifier()));
        addIfPresent(fields, "03", payload.fiName());
        fields.add(new Tlv("04", payload.accountNumber()));
        fields.add(new Tlv("05", payload.accountHolderName()));

        if ("12".equals(payload.pointOfInitiationMethod())) {
            fields.add(new Tlv("06", payload.amount().toPlainString()));
            fields.add(new Tlv("07", payload.particulars()));
        }

        String body = fields.stream()
                .map(Tlv::encode)
                .collect(Collectors.joining());
        String crcInput = body + "1004";
        return crcInput + CrcCalculator.calculate(crcInput);
    }

    private void validateRequest(GenerateP2PQrRequest request) {
        String accountNumber = request.accountNumber();
        if (accountNumber == null
                || !accountNumber.matches("SO\\d+")) {
            throw new ApiException(
                    "Invalid Somali IBAN: it must begin with SO and contain digits only after the country code"
            );
        }

        if ("11".equals(request.pointOfInitiationMethod())) {
            if (request.amount() != null && request.amount().signum() != 0) {
                throw new ApiException("Static QR amount must be zero or absent");
            }

            if (request.particulars() != null && !request.particulars().isBlank()) {
                throw new ApiException("Static QR particulars must be absent");
            }
        }

        if ("12".equals(request.pointOfInitiationMethod())) {
            if (request.amount() == null || request.amount().signum() <= 0) {
                throw new ApiException("Dynamic QR amount must be greater than 0.00");
            }

            if (request.particulars() == null || request.particulars().isBlank()) {
                throw new ApiException("Dynamic QR particulars are required");
            }
        }
    }

    private void addIfPresent(List<Tlv> fields, String tag, String value) {
        if (value != null && !value.isBlank()) {
            fields.add(new Tlv(tag, value));
        }
    }
}
