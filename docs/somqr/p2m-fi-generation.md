# P2M FI/Bank QR Generation

Generates a Person-to-Merchant SOMQR payload for a financial institution or bank merchant account.

## Endpoint

```text
POST /api/v1/somqr/p2m/fi/generate
Content-Type: application/json
```

## Request

```json
{
  "pointOfInitializationMethod": "12",
  "transactionCurrency": 840,
  "transactionAmount": 8.33,
  "merchantId": "305005007888",
  "merchantCategoryCode": 2741,
  "merchantName": "Gafal",
  "merchantCity": "Mogadishu",
  "postalCode": null,
  "storeLabel": null,
  "terminalLabel": null,
  "purposeOfTransaction": "Payment",
  "referenceLabel": "Reference 1"
}
```

| Field | Required | Rules |
|---|---:|---|
| `pointOfInitializationMethod` | Yes | `11` static or `12` dynamic |
| `transactionCurrency` | Yes | Numeric value from 1 to 999 |
| `transactionAmount` | Conditional | Static QR may omit it or use zero; dynamic QR requires a positive amount |
| `merchantId` | Yes | Alphanumeric, maximum 14 characters |
| `merchantCategoryCode` | Yes | Numeric value from 0 to 9999 |
| `merchantName` | Yes | Maximum 40 characters |
| `merchantCity` | Yes | Maximum 30 characters |
| `postalCode` | No | Maximum 10 characters |
| `storeLabel` | No | Maximum 26 characters |
| `terminalLabel` | No | Maximum 26 characters |
| `purposeOfTransaction` | No | Maximum 26 characters |
| `referenceLabel` | No | Maximum 26 characters |

The FI acquirer ID is read from configuration. The request supplies the merchant ID; it does not supply the acquirer ID.

## Validation

- Static QR omits the amount when it is `null` or `0.00`.
- Static QR rejects a positive amount.
- Dynamic QR requires an amount greater than `0.00`.
- The configured FI acquirer ID must contain exactly four digits. The generator prefixes `01` when encoding the SOMQR field.
- Optional additional data is included only when values are provided.

## Response

The response contains the generated payload under `data.payload` in the common response envelope.

```json
{
  "code": 200,
  "message": "FI/Bank P2M QR code generated successfully",
  "status": "200 OK",
  "data": {
    "payload": "000201..."
  }
}
```

## Possible errors

- `400`: invalid request field
- `400`: static QR contains a positive amount
- `400`: dynamic QR has no positive amount
- `400`: configured FI acquirer ID is not exactly four digits
- `500`: unexpected generation failure

