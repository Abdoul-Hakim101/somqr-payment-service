# SOMQR API Documentation

This folder documents the SOMQR API implemented by the payment service.

## Documents

- [P2P generation](p2p-generation.md)
- [P2P decoding](p2p-decoding.md)
- [P2M FI/Bank generation](p2m-fi-generation.md)
- [P2M MNO generation](p2m-mno-generation.md)
- [P2M decoding](p2m-decoding.md)

## Common API response

Successful requests use the common response envelope. Generated payloads are returned under `data.payload`; decoded values are returned under `data.payload` as a structured object.

```json
{
  "time": "2026-10-08T12:00:00",
  "traceId": "4475eeb1-05f8-4932-b370-c3dd3fbdae6b",
  "path": "/api/v1/somqr/p2p/generate",
  "code": 200,
  "message": "P2P QR code generated successfully",
  "status": "200 OK",
  "data": {
    "payload": "..."
  }
}
```

Every error response includes a `traceId`. Use it to find the corresponding application log entry.

## Common errors

| HTTP status | Cause |
|---|---|
| `400 Bad Request` | Missing or invalid request fields, malformed JSON, invalid TLV data, or invalid CRC |
| `404 Not Found` | The requested endpoint does not exist |
| `401 Unauthorized` | A required request header is missing |
| `500 Internal Server Error` | Unexpected server-side failure |

Validation errors are returned in the common response envelope with the validation message in `message` and the exception name in `exception`.

