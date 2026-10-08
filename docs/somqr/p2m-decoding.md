# P2M QR Decoding

Parses and validates a Person-to-Merchant SOMQR payload. The same endpoint handles FI/Bank and MNO payloads.

## Endpoint

```text
POST /api/v1/somqr/p2m/decode
Content-Type: application/json
```

## Request

```json
{
  "payload": "000201010211..."
}
```

The payload must be between 8 and 512 characters and must contain valid P2M TLV data and CRC.

## Response

```json
{
  "time": "2026-10-08T12:00:00",
  "traceId": "4475eeb1-05f8-4932-b370-c3dd3fbdae6b",
  "path": "/api/v1/somqr/p2m/decode",
  "code": 200,
  "message": "P2M QR code decoded successfully",
  "status": "200 OK",
  "data": {
    "payload": {
      "payloadFormatIndicator": "01",
      "pointOfInitiationMethod": "11",
      "merchantAccountInformation": {
        "26": {
          "globalUniqueIdentifier": "so.somqr.sips",
          "paymentNetworkSpecific": {
            "01": "010014",
            "44": "305005007888"
          }
        }
      },
      "merchantCategoryCode": 2741,
      "transactionCurrency": 840,
      "transactionAmount": null,
      "tipOrConvenienceIndicator": null,
      "valueOfConvenienceFeeFixed": null,
      "valueOfConvenienceFeePercentage": null,
      "countryCode": "SO",
      "merchantName": "Gafal",
      "merchantCity": "Mogadishu",
      "postalCode": null,
      "additionalData": {
        "billNumber": null,
        "mobileNumber": null,
        "storeLabel": null,
        "loyaltyNumber": null,
        "referenceLabel": null,
        "customerLabel": null,
        "terminalLabel": null,
        "purposeOfTransaction": null,
        "additionalConsumerDataRequest": null
      },
      "crc": "9FE6"
    }
  }
}
```

For MNO payloads, the root MNO network field identifies the network. The decoder supports the shared merchant account template and the optional additional-data template.

## Validation performed

- Payload format indicator and required P2M fields are checked.
- TLV tags and declared lengths are validated, including nested templates.
- FI/Bank and MNO merchant account information is parsed.
- Additional data fields are returned as a structured object; absent fields are `null`.
- The CRC must match the calculated ISO/IEC 3309-compatible CRC-16 value.
- MNO network data is treated as a root network field, not as nested merchant-account TLV data.

## Possible errors

- `400`: payload is blank, too short, or too long
- `400`: invalid P2M payload format indicator
- `400`: malformed or incomplete TLV field
- `400`: declared TLV length does not match the available value
- `400`: invalid CRC tag, length, or checksum
- `400`: invalid merchant account or P2M field value
- `500`: unexpected decoding failure

