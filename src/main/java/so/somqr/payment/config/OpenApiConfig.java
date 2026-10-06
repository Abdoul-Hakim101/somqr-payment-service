package so.somqr.payment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SOMQR Payment Service API")
                        .version("1.0.0")
                        .description("""
                                API documentation for the **SOMQR Payment Service**, the national interoperable QR payment platform for the Somali Instant Payment System (SIPS).

                                SOMQR is the **Somali National Quick Response (QR) Code Standard** defined by the Central Bank of Somalia (CBS) in collaboration with banks, Mobile Network Operators (MNOs), and Fintechs under the Somali Bankers Association (SBA). It is fully compliant with **EMVCo QR specifications** and serves as a low-cost, scalable, secure, and interoperable solution toward a cashless society.

                                ---

                                ### Architecture and Domain Rules:
                                1. **National Interoperability**: Enables seamless QR payments across banks, MNOs, Microfinance Institutions (MFIs), and Payment Service Providers (PSPs) under a unified SOMQR standard.
                                2. **EMVCo Compliance**: All QR payloads follow EMVCo Merchant-Presented Mode (MPM) and Person-to-Person (P2P) specifications with ISO/IEC 3309 CRC-16 checksums.
                                3. **Instant Settlement**: Integrates with the Somali Payments Switch (SPS) and Somali Instant Payment System (SIPS) for real-time inter-network settlement with Net Debit/Credit (NDC) limits.
                                4. **Risk & Compliance**: Implements velocity checks, geo-location fraud detection, KYC/KYM protocols, and CBS regulatory compliance for AML/CFT.
                                5. **Auditable Processing**: All merchant onboarding, QR generation, transactions, disputes, and settlement reports are persisted for traceability and reconciliation.

                                ---

                                ### Core API Capabilities:
                                • **Merchant QR (MPM)**: Generation and parsing of static/dynamic SOMQR payloads per EMVCo MPM specification (Tags 00–63), including Merchant Account Information (Tag 26), MCC (Tag 52), and CRC (Tag 63).
                                • **P2P QR**: Generation and parsing of Person-to-Person SOMQR payloads (Tags 00–10) for bank and non-bank fund transfers using IBAN or mobile wallet identifiers.
                                • **Merchant Management**: Merchant onboarding, KYC/KYM verification, MCC assignment, and alias merchant ID handling for feature phones (+252 aliases).
                                • **Transactions**: QR-initiated payment processing, status inquiry, refunds, and instant credit notifications.
                                • **Settlement**: Daily net settlement calculation, NDC limit enforcement, cutover-time processing, and reconciliation reports.
                                • **Disputes**: Customer complaint registration, merchant dispute handling, and CBS-compliant resolution workflows.
                                • **Security**: Encrypted and signed API instructions, PII protection, and annual security audit compliance.
                                • **Operations**: Health checks, participant status, metrics, fraud reporting, and CBS compliance endpoints.

                                ---

                                ### Regulatory Context:
                                • **Regulator**: Central Bank of Somalia (CBS)
                                • **Framework**: SOMQR Standard & Operational Guidelines (May 2023, Version 1)
                                • **Switch**: Somali Payments Switch (SPS) / Somali Instant Payment System (SIPS)
                                • **Currency**: Somali Shilling (SOS, ISO 4217 code: 706)
                                • **Country Code**: SO (ISO 3166)
                                • **Domain**: so.somqr.sips
                                """)
                        .contact(new Contact()
                                .name("SOMQR Engineering Team")
                                .email("info@centralbank.gov.so")
                                .url("https://www.centralbank.gov.so"))
                        .license(new License()
                                .name("Central Bank of Somalia – SOMQR Standard (May 2023)")
                                .url("https://www.centralbank.gov.so")));
    }
}