# Corporate Background Verification API

A Spring Boot REST API that performs background verification on corporate clients before onboarding. It concurrently checks an organisation against a **watchlist** (sanctions, enforcement) and a **corporate registry** (registration validity), and returns a consolidated risk verdict.

---

## Table of Contents

- [Overview](#overview)
- [Risk Status](#risk-status)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [API Reference](#api-reference)
- [Project Structure](#project-structure)

---

## Overview

When onboarding a corporate client, the API runs two checks in parallel:

| Check | Service | What it verifies |
|---|---|---|
| Watchlist | `WatchlistService` | Sanctions, regulatory enforcement, financial crime lists |
| Corporate Registry | `RegistryService` | Company registration number validity and active status |

Results from both checks are combined into a single `VerificationResponse` with an overall `riskStatus`.

---

## Risk Status

| Status | Meaning |
|---|---|
| `GREEN` | Watchlist is `CLEAN` and registry status is `ACTIVE` |
| `YELLOW` | One or more checks timed out or returned an unknown result |
| `RED` | One or more checks returned a flagged or non-compliant status |

---

## Tech Stack

- **Java 21**
- **Spring Boot 4.0.6**
- **Spring Web MVC**
- **Apache HttpClient 5** — for RestTemplate HTTP connectivity
- **Lombok** — reduces boilerplate
- **Maven** — build and dependency management

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+

### Clone and Run

```bash
git clone https://github.com/sairam-nagaraj/corporate-background-verification.git
cd corporate-background-verification
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080` by default.

### Build JAR

```bash
./mvnw clean package
java -jar target/corporate-background-verification-0.0.1-SNAPSHOT.jar
```

---

## API Reference

### POST `/api/verify`

Runs a background verification check on a corporate entity.

#### Request Body

```json
{
  "reference": "REF-001",
  "companyName": "Acme Solutions",
  "registrationNumber": "REG-100234",
  "countryCode": "US"
}
```

| Field | Type | Description |
|---|---|---|
| `reference` | String | Unique reference ID for this verification request |
| `companyName` | String | Registered name of the company |
| `registrationNumber` | String | Company registration number (format: `REG-XXXXXX`) |
| `countryCode` | String | ISO 3166-1 alpha-2 country code (e.g. `US`, `GB`) |

#### Response Body

```json
{
  "reference": "REF-001",
  "riskStatus": "GREEN",
  "processedAt": "2026-05-17T10:30:00",
  "verificationDetails": [
    {
      "type": "Watchlist",
      "status": "CLEAN"
    },
    {
      "type": "CorporateRegistry",
      "status": "ACTIVE"
    }
  ]
}
```

#### Response when flagged

```json
{
    "reference": "680b9cb8-74af-4b08-ad1c-d7cd1aa0a0da",
    "riskStatus": "RED",
    "processedAt": "2026-05-29T18:08:04.3276082",
    "verificationDetails": [
        {
            "type": "Watchlist",
            "status": "FLAGGED",
            "error": "Multiple entities found for the given name. Active entities found 3"
        },
        {
            "type": "CorporateRegistry",
            "status": "ACTIVE",
            "error": ""
        }
    ]
}
```

#### Response when a service times out

```json
{
    "reference": "680b9cb8-74af-4b08-ad1c-d7cd1aa0a0da",
    "riskStatus": "YELLOW",
    "processedAt": "2026-05-29T16:27:11.1842638",
    "verificationDetails": [
        {
            "type": "Watchlist",
            "status": "UNKNOWN",
            "error": "java.util.concurrent.ExecutionException: org.springframework.web.client.ResourceAccessException: I/O error on GET request for \"http://localhost:8081/api/v1/watchlist-check\": Read timed out"
        },
        {
            "type": "CorporateRegistry",
            "status": "ACTIVE",
            "error": ""
        }
    ]
}
```

## Project Structure

```
src/
└── main/
    └── java/
        └── io/github/four88labs/corporatebackgroundverification/
            ├── controller/       # REST endpoints (BgvController)
            ├── service/          # Core logic (BgvService, WatchlistService, RegistryService)
            ├── model/            # Request & response models (VerificationRequest, VerificationResponse, VerificationDetails)
            └── config/           # RestTemplate / HTTP client configuration
```

## Mock Data

The mock API can be run from the mock folder which should be excluded from the build process. To run the project 
```bash
./mvnw clean package
java -jar freelance-csv-mock-apis-0.0.1-SNAPSHOT.jar
```

To fetch mock data, in case of Registry Cases
```
GET:/ http://localhost:8081/api/v1/fetch-data?t=registryOrgMock
```

To fetch mock data, in case of watchlist Cases
```
GET:/ http://localhost:8081/api/v1/fetch-data?t=watchlistOrgMock
```

---

## License

This project is open source. See [LICENSE](LICENSE) for details.