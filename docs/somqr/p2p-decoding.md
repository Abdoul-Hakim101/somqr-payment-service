# P2P QR Decoding

Parses and validates a Person-to-Person SOMQR payload.

## Endpoint

```text
POST /api/v1/somqr/p2p/decode
Content-Type: application/json
```

## Request

```json
{
  "payload": "000202010211..."
}
```

The payload must be between 8 and 512 characters and must contain a valid P2P TLV structure and CRC.

## Response

```json
{
  "time": "2026-10-08T12:00:00",
  "traceId": "4475eeb1-05f8-4932-b370-c3dd3fbdae6b",
  "path": "/api/v1/somqr/p2p/decode",
  "code": 200,
  "message": "P2P QR code decoded successfully",
  "status": "200 OK",
  "data": {
    "payload": {
      "payloadFormatIndicator": "02",
      "pointOfInitiationMethod": "11",
      "schemeIdentifier": "01",
      "fiName": "Galaxy International Bank",
      "accountNumber": "SO1234567890",
      "accountHolderName": "Amina Hassan",
      "amount": null,
      "particulars": null,
      "crc": "8295"
    }
  }
}
```

## Validation performed

- Payload format indicator must be `02`.
- Point of initiation method must be `11` or `12`.
- Required TLV fields must be present and correctly sized.
- The CRC must use the P2P CRC field and match the calculated ISO/IEC 3309-compatible CRC-16 value.
- Invalid or incomplete TLV data is rejected.

## Possible errors

- `400`: payload is blank, too short, or too long
- `400`: invalid P2P CRC tag, length, or checksum
- `400`: malformed or incomplete TLV field
- `400`: invalid P2P field value
- `500`: unexpected decoding failure

