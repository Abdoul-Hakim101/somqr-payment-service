# SOMQR Payment Service

An open-source implementation of the Somalia National QR (SOMQR) standard.

This project provides reusable APIs for Somali banks, financial institutions, mobile network operators, technology providers, and regulators to integrate SOMQR payment functionality. By providing the core QR generation, decoding, validation, and CRC processing in one project, organizations can reduce development time and integration costs.

The implementation is based on the official SOMQR specifications included in the project and is ready to be integrated into payment systems. Organizations remain responsible for their own deployment configuration, security controls, testing, certification, and regulatory approval requirements.

The service currently supports:

- Person-to-Person (P2P) QR generation and decoding
- Person-to-Merchant (P2M) QR decoding
- P2M Financial Institution/Bank QR generation
- P2M Mobile Network Operator (MNO) QR generation
- ISO/IEC 3309 CRC-16 validation
- Trace IDs in API responses and logs

## Project Status

The current implementation provides P2P and P2M QR generation, decoding, validation, and CRC processing. Production deployments should apply the organization-specific security, monitoring, certification, and regulatory controls required by the deploying institution.

## Technology Stack

- Java 26
- Spring Boot 4.1.1
- Maven
- Spring Web MVC
- Jakarta Bean Validation
- Springdoc OpenAPI / Swagger UI

## Project Structure

```text
src/main/java/so/somqr/payment
├── controller       REST API endpoints
├── domain          SOMQR domain models and TLV structures
├── dto             Request and response objects
├── exception       API exception handling
├── service         QR generation and decoding services
└── util            CRC, TLV, and QR parsing utilities
```

## Documentation

Detailed API and implementation guides are available in [`docs/somqr`](docs/somqr/README.md):

- [P2P QR generation](docs/somqr/p2p-generation.md)
- [P2P QR decoding](docs/somqr/p2p-decoding.md)
- [P2M FI/Bank QR generation](docs/somqr/p2m-fi-generation.md)
- [P2M MNO QR generation](docs/somqr/p2m-mno-generation.md)
- [P2M QR decoding](docs/somqr/p2m-decoding.md)

The official SOMQR specification is available here:

- [SOMQR Code Standards](docs/reference/SOMQR-Code-Standards.pdf)

## Configuration

Copy the environment template before running locally:

```bash
copy .env.example .env
```

Important variables include:

| Variable | Description |
|---|---|
| `SERVER_PORT` | Port used by the application |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile, for example `dev` |
| `SPRING_DOC_API_DOCS_ENABLED` | Enables the OpenAPI endpoint |
| `SPRING_DOC_SWAGGER_UI_ENABLED` | Enables Swagger UI |
| `CORS_ALLOWED_ORIGINS` | Allowed frontend origins |
| `SOMQR_P2P_SCHEME_IDENTIFIER` | P2P scheme identifier |
| `SOMQR_P2P_FI_NAME` | Financial institution name for P2P QR codes |
| `SOMQR_P2M_FI_ACQUIRER_ID` | Four-digit FI acquirer ID; `01` is added by the generator |
| `SOMQR_P2M_MNO_NETWORK_ID` | MNO network ID: `01` Hormuud or `02` Somtel |

The local `.env` file must not be committed. Use `.env.example` as the shared template.

## Prerequisites

- Java 26
- Docker Desktop with Docker Compose, if running with containers

## Quick Start

1. Copy `.env.example` to `.env` and adjust the values for your environment.
2. Choose either the Maven or Docker Compose startup method below.
3. Open Swagger UI to explore the available endpoints.

## Run Locally

Start the application with Maven:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
./mvnw.cmd spring-boot:run
```

## Run with Docker Compose

```bash
docker compose up -d --build
```

View the container logs with:

```bash
docker compose logs -f
```

Stop the containers with:

```bash
docker compose down
```

## API Endpoints

### P2P

```text
POST /api/v1/somqr/p2p/decode
POST /api/v1/somqr/p2p/generate
```

### P2M

```text
POST /api/v1/somqr/p2m/decode
POST /api/v1/somqr/p2m/fi/generate
POST /api/v1/somqr/p2m/mno/generate
```

All endpoints accept and return JSON. Generated QR endpoints return the encoded payload under the `payload` field.

Request and response examples, field definitions, and validation rules are documented in the [SOMQR API guides](docs/somqr/README.md).

## API Documentation

When enabled, Swagger UI is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

The OpenAPI specification is available at:

```text
http://localhost:8080/v3/api-docs
```

## Error Responses

Errors use a common JSON response containing the request path, status, message, and trace ID:

```json
{
  "time": "2026-10-08T12:00:00",
  "traceId": "4475eeb1-05f8-4932-b370-c3dd3fbdae6b",
  "path": "/api/v1/somqr/p2p/decode",
  "code": 400,
  "message": "Invalid SOMQR payload",
  "status": "400 BAD_REQUEST"
}
```

Use the `traceId` when searching application logs or reporting an error.

## Testing

Run all tests:

```bash
./mvnw test
```

On Windows PowerShell:

```powershell
./mvnw.cmd test
```

Build without running tests:

```bash
./mvnw clean package -DskipTests
```

The project also runs these checks automatically for pushes and pull requests through [Maven CI](.github/workflows/maven-ci.yml).

## SOMQR Standards

The implementation follows the SOMQR P2P and P2M specifications supplied in the project documentation. The CRC is calculated over all characters before the CRC value using ISO/IEC 3309-compatible CRC-16 processing.

## Contributing

Contributions are welcome. Please keep changes focused, add or update tests for behavior changes, and update the relevant documentation under `docs/somqr`.

## License

This project is licensed under the [Apache License 2.0](LICENSE).
