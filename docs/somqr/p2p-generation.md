# P2P QR Generation

Generates a Person-to-Person SOMQR payload for a beneficiary account.

## Endpoint

```text
POST /api/v1/somqr/p2p/generate
Content-Type: application/json
```

## Request

```json
{
  "pointOfInitiationMethod": "11",
  "accountNumber": "SO1234567890",
  "accountHolderName": "Amina Hassan",
  "amount": null,
  "particulars": null
}
```

| Field | Required | Rules |
|---|---:|---|
| `pointOfInitiationMethod` | Yes | `11` for static or `12` for dynamic |
| `accountNumber` | Yes | Maximum 35 characters; the generator accepts a Somali IBAN format beginning with `SO` followed by digits |
| `accountHolderName` | Yes | Maximum 45 characters |
| `amount` | Conditional | Static QR may omit it or use zero; dynamic QR requires a positive amount |
| `particulars` | Conditional | Dynamic QR requires non-blank particulars; static QR must omit it |

The scheme identifier and FI name are supplied by application configuration rather than by the request.

## Validation

- Only point-of-initiation methods `11` and `12` are allowed.
- Static QR must not contain a positive amount.
- Static QR must not contain particulars.
- Dynamic QR requires an amount greater than `0.00`.
- Dynamic QR requires non-blank particulars.
- The generated payload ends with a CRC calculated using ISO/IEC 3309-compatible CRC-16 processing.

## Response

```json
{
  "time": "2026-10-08T12:00:00",
  "traceId": "4475eeb1-05f8-4932-b370-c3dd3fbdae6b",
  "path": "/api/v1/somqr/p2p/generate",
  "code": 200,
  "message": "P2P QR code generated successfully",
  "status": "200 OK",
  "data": {
    "payload": "000202010211..."
  }
}
```

## Possible errors

- `400`: required field is missing or invalid
- `400`: static QR contains a positive amount or particulars
- `400`: dynamic QR has no positive amount or has blank particulars
- `500`: unexpected generation failure

