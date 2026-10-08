package so.somqr.payment.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import so.somqr.payment.domain.Tlv;
import so.somqr.payment.dto.Request.GenerateP2MQrRequest;
import so.somqr.payment.exception.ApiException;
import so.somqr.payment.service.P2MQrGenerator;
import so.somqr.payment.util.CrcCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class P2MQrGeneratorImpl implements P2MQrGenerator {

    private static final String PAYLOAD_FORMAT_INDICATOR = "01";
    private static final String COUNTRY_CODE = "SO";
    private static final String GLOBAL_UNIQUE_IDENTIFIER = "so.somqr.sips";
    private static final String FI_ACQUIRER_CATEGORY = "01";

    @Value("${somqr.p2m.fi.acquirer-id}")
    private String fiAcquirerId;

    @Value("${somqr.p2m.mno.network-id}")
    private String mnoNetworkId;

    @Override
    public String generateFi(GenerateP2MQrRequest request) {
        validateRequest(request);
        validateFiConfiguration();

        List<Tlv> fields = commonFields(request);
        fields.add(2, merchantAccountInformation(
                request,
                new Tlv("01", FI_ACQUIRER_CATEGORY + fiAcquirerId)
        ));
        return encode(fields);
    }

    @Override
    public String generateMno(GenerateP2MQrRequest request) {
        validateRequest(request);
        validateMnoConfiguration();

        List<Tlv> fields = commonFields(request);
        fields.add(2, merchantAccountInformation(request, null));
        fields.add(3, new Tlv("31", mnoNetworkId));
        return encode(fields);
    }

    private List<Tlv> commonFields(GenerateP2MQrRequest request) {
        List<Tlv> fields = new ArrayList<>();
        fields.add(new Tlv("00", PAYLOAD_FORMAT_INDICATOR));
        fields.add(new Tlv("01", request.pointOfInitializationMethod()));
        fields.add(new Tlv("52", String.format("%04d", request.merchantCategoryCode())));
        fields.add(new Tlv("53", String.format("%03d", request.transactionCurrency())));
        if (request.transactionAmount() != null && request.transactionAmount().signum() > 0) {
            fields.add(new Tlv("54", request.transactionAmount().toPlainString()));
        }
        fields.add(new Tlv("58", COUNTRY_CODE));
        fields.add(new Tlv("59", request.merchantName()));
        fields.add(new Tlv("60", request.merchantCity()));
        addIfPresent(fields, "61", request.postalCode());
        addAdditionalData(fields, request);
        return fields;
    }

    private Tlv merchantAccountInformation(GenerateP2MQrRequest request, Tlv providerField) {
        String value = merchantAccountValue(request, providerField);
        return new Tlv("26", value);
    }

    private String merchantAccountValue(GenerateP2MQrRequest request, Tlv providerField) {
        List<Tlv> children = new ArrayList<>();
        children.add(new Tlv("00", GLOBAL_UNIQUE_IDENTIFIER));
        if (providerField != null) {
            children.add(providerField);
        }
        children.add(new Tlv("44", request.merchantId()));
        return children.stream()
                .map(Tlv::encode)
                .collect(Collectors.joining());
    }

    private void addAdditionalData(List<Tlv> fields, GenerateP2MQrRequest request) {
        List<Tlv> additionalFields = new ArrayList<>();
        addIfPresent(additionalFields, "03", request.storeLabel());
        addIfPresent(additionalFields, "05", request.referenceLabel());
        addIfPresent(additionalFields, "07", request.terminalLabel());
        addIfPresent(additionalFields, "08", request.purposeOfTransaction());

        if (!additionalFields.isEmpty()) {
            String value = additionalFields.stream()
                    .map(Tlv::encode)
                    .collect(Collectors.joining());
            fields.add(new Tlv("62", value));
        }
    }

    private String encode(List<Tlv> fields) {
        String body = fields.stream()
                .map(Tlv::encode)
                .collect(Collectors.joining());
        String crcInput = body + "6304";
        return crcInput + CrcCalculator.calculate(crcInput);
    }

    private void validateRequest(GenerateP2MQrRequest request) {
        if (request.transactionAmount() != null && request.transactionAmount().signum() < 0) {
            throw new ApiException("P2M transaction amount must be zero or greater");
        }

        if ("11".equals(request.pointOfInitializationMethod())
                && request.transactionAmount() != null
                && request.transactionAmount().signum() > 0) {
            throw new ApiException("Static P2M QR code must not contain a positive transaction amount");
        }

        if ("12".equals(request.pointOfInitializationMethod())
                && (request.transactionAmount() == null
                || request.transactionAmount().signum() <= 0)) {
            throw new ApiException("Dynamic P2M QR code requires a transaction amount greater than 0.00");
        }
    }

    private void validateFiConfiguration() {
        if (fiAcquirerId == null || !fiAcquirerId.matches("\\d{4}")) {
            throw new ApiException("FI acquirer ID must contain exactly 4 digits");
        }
    }

    private void validateMnoConfiguration() {
        if (mnoNetworkId == null || !mnoNetworkId.matches("01|02")) {
            throw new ApiException("MNO network ID must be 01 for Hormuud or 02 for Somtel");
        }
    }

    private void addIfPresent(List<Tlv> fields, String tag, String value) {
        if (value != null && !value.isBlank()) {
            fields.add(new Tlv(tag, value));
        }
    }
}
