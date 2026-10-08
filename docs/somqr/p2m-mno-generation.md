# P2M MNO QR Generation

Generates a Person-to-Merchant SOMQR payload for a mobile network operator merchant account.

## Endpoint

```text
POST /api/v1/somqr/p2m/mno/generate
Content-Type: application/json
```

## Request

The request has the same structure as FI/Bank generation:

```json
{
  "pointOfInitializationMethod": "11",
  "transactionCurrency": 840,
  "transactionAmount": null,
  "merchantId": "305005007888",
  "merchantCategoryCode": 2741,
  "merchantName": "Gafal",
  "merchantCity": "Mogadishu",
  "postalCode": null,
  "storeLabel": "Main Store",
  "terminalLabel": null,
  "purposeOfTransaction": null,
  "referenceLabel": null
}
```

The MNO network ID is read from configuration, not from the request. It is encoded in the root MNO network field.

## Validation

- Static QR omits the amount when it is `null` or `0.00`.
- Static QR rejects a positive amount.
- Dynamic QR requires an amount greater than `0.00`.
- The configured MNO network ID must be `01` for Hormuud or `02` for Somtel.
- Optional additional data is included only when values are provided.

## Response

```json
{
  "code": 200,
  "message": "MNO P2M QR code generated successfully",
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
- `400`: configured MNO network ID is not `01` or `02`
- `500`: unexpected generation failure

