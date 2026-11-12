# Spring Boot Alumni Search API (PhantomBuster Integration)

A robust, production-grade Spring Boot 3 RESTful API that allows users to search for LinkedIn profiles of alumni from an educational institution using the **PhantomBuster API** and persists the results into a database (**PostgreSQL** / **H2**).

---

## 🚀 Key Highlights & Architecture

- **Clean RESTful Design**: Complies with standard HTTP verbs and status codes (`200 OK`, `400 Bad Request`, `502 Bad Gateway`, `500 Internal Server Error`).
- **Design Patterns**:
  - **Strategy / Adapter Pattern (`PhantomBusterClient`)**: Decouples external API communication from internal business logic. Automatically switches between `PhantomBusterClientImpl` (real API) and `MockPhantomBusterClient` (sandbox/fallback) based on configuration.
  - **DTO & Mapper Pattern**: Separates persistence entity models (`AlumniProfile`) from inbound requests (`AlumniSearchRequest`) and outbound responses (`AlumniResponseDto`).
  - **Repository Pattern**: Spring Data JPA abstraction with custom query methods and duplicate detection.
  - **Centralized Exception Handling**: `@RestControllerAdvice` catching validation and third-party API exceptions, returning unified `ApiResponse<T>` objects.
- **Deduplication Logic**: Updates existing alumni profiles if already present to prevent duplicate records.
- **Zero-Config Evaluation**: Runs out of the box using embedded H2 in `dev` profile. Can switch to PostgreSQL (`prod` profile) with Docker Compose.
- **Test-Driven & Tested**: Comprehensive JUnit 5, Mockito, and MockMvc test suite with 100% test pass rate.

---

## 🛠️ Tech Stack

- **Java**: 21 LTS
- **Framework**: Spring Boot 3.3.5
- **ORM / Persistence**: Spring Data JPA, Hibernate 6
- **Database**: PostgreSQL 16 (production), H2 (development & testing)
- **Validation**: Jakarta Bean Validation (`hibernate-validator`)
- **HTTP Client**: Spring 6 `RestClient`
- **Testing**: JUnit 5, Mockito, Spring Boot Starter Test, MockMvc
- **Build Tool**: Maven 3 / Maven Wrapper (`mvnw`)

---

## 📁 Project Structure

```
alumni-search-api/
├── docker-compose.yml              # PostgreSQL Docker Compose
├── postman_collection.json         # Ready-to-import Postman collection
├── pom.xml                         # Maven dependencies & build config
├── README.md
├── src/
│   ├── main/
│   │   ├── java/com/example/alumnisearch/
│   │   │   ├── AlumniSearchApplication.java
│   │   │   ├── client/             # PhantomBuster Adapter & Strategy
│   │   │   │   ├── PhantomBusterClient.java
│   │   │   │   ├── PhantomBusterClientImpl.java
│   │   │   │   └── MockPhantomBusterClient.java
│   │   │   ├── config/             # Configuration & conditional beans
│   │   │   │   ├── PhantomBusterConfig.java
│   │   │   │   └── PhantomBusterClientConfig.java
│   │   │   ├── controller/         # REST Controllers
│   │   │   │   └── AlumniController.java
│   │   │   ├── dto/                # Request / Response DTOs
│   │   │   │   ├── AlumniSearchRequest.java
│   │   │   │   ├── AlumniResponseDto.java
│   │   │   │   └── ApiResponse.java
│   │   │   ├── entity/             # JPA Entities
│   │   │   │   └── AlumniProfile.java
│   │   │   ├── exception/          # Global Exception Handling
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── PhantomBusterApiException.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── repository/         # Spring Data JPA Repositories
│   │   │   │   └── AlumniProfileRepository.java
│   │   │   └── service/            # Business Logic & Orchestration
│   │   │       ├── AlumniService.java
│   │   │       └── AlumniServiceImpl.java
│   │   └── resources/
│   │       ├── application.yml     # Common application configuration
│   │       ├── application-dev.yml # H2 in-memory profile (default)
│   │       └── application-prod.yml# PostgreSQL configuration
│   └── test/
│       ├── java/com/example/alumnisearch/
│       │   ├── AlumniSearchApplicationTests.java
│       │   ├── controller/AlumniControllerTest.java
│       │   ├── repository/AlumniProfileRepositoryTest.java
│       │   └── service/AlumniServiceTest.java
│       └── resources/
│           └── application-test.yml
```

---

## ⚙️ Configuration & PhantomBuster Modes

Configure in `application.yml` or via Environment Variables:

| Variable | Property | Description | Default |
|---|---|---|---|
| `PHANTOMBUSTER_API_KEY` | `phantombuster.api-key` | Your PhantomBuster API Key | `""` |
| `PHANTOMBUSTER_AGENT_ID` | `phantombuster.agent-id` | LinkedIn Search Phantom Agent ID | `""` |
| `PHANTOMBUSTER_MOCK_MODE` | `phantombuster.mock-mode` | Force mock mode | `false` |
| `SPRING_PROFILES_ACTIVE` | `spring.profiles.active` | Active profile (`dev` or `prod`) | `dev` |

### Smart Mode Switching:
1. **Live Mode**: If `PHANTOMBUSTER_API_KEY` and `PHANTOMBUSTER_AGENT_ID` are configured, the app launches the live PhantomBuster LinkedIn agent, polls for scrape output, maps the profiles, and saves them to the DB.
2. **Mock Mode (Default when API key is empty)**: Automatically provides realistic LinkedIn alumni data without requiring paid PhantomBuster credits, making the API instantly evaluable.

---

## 🏃 Getting Started

### 1. Run with Default In-Memory H2 (Instant Evaluation)

```bash
./mvnw spring-boot:run
```
The server will start on port `8080`.
H2 Web Console is accessible at: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:alumnidb`, User: `sa`, Password: empty).

### 2. Run with PostgreSQL

1. Start PostgreSQL using Docker:
   ```bash
   docker compose up -d
   ```
2. Start the application with the `prod` profile:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
   ```

---

## 🧪 Running Automated Tests

Run the full test suite (Repository, Service, Controller, and Application Context tests):

```bash
./mvnw clean test
```

All 11 tests execute with zero external dependencies using JUnit 5, Mockito, and in-memory H2.

---

## 📡 API Reference & Examples

### 1. Search Alumni Profiles
- **URL**: `POST /api/alumni/search`
- **Headers**: `Content-Type: application/json`

#### Request Body:
```json
{
    "university": "University of XYZ",
    "designation": "Software Engineer",
    "passoutYear": 2020
}
```
*(Note: `passoutYear` is optional)*

#### Success Response (`200 OK`):
```json
{
    "status": "success",
    "data": [
        {
            "name": "John Doe",
            "currentRole": "Software Engineer",
            "university": "University of XYZ",
            "location": "New York, NY",
            "linkedinHeadline": "Passionate Software Engineer | University of XYZ Alum",
            "passoutYear": 2020
        },
        {
            "name": "Jane Smith",
            "currentRole": "Senior Software Engineer",
            "university": "University of XYZ",
            "location": "San Francisco, CA",
            "linkedinHeadline": "Software Engineer | Tech Innovator & University of XYZ Alumna",
            "passoutYear": 2019
        }
    ]
}
```

#### cURL:
```bash
curl -X POST http://localhost:8080/api/alumni/search \
  -H "Content-Type: application/json" \
  -d '{
    "university": "University of XYZ",
    "designation": "Software Engineer",
    "passoutYear": 2020
  }'
```

---

### 2. Get All Saved Alumni Profiles
- **URL**: `GET /api/alumni/all`

#### Success Response (`200 OK`):
```json
{
    "status": "success",
    "data": [
        {
            "name": "John Doe",
            "currentRole": "Software Engineer",
            "university": "University of XYZ",
            "location": "New York, NY",
            "linkedinHeadline": "Passionate Software Engineer | University of XYZ Alum",
            "passoutYear": 2020
        }
    ]
}
```

#### cURL:
```bash
curl -X GET http://localhost:8080/api/alumni/all
```

---

### 3. Validation Error Handling Example
- **URL**: `POST /api/alumni/search`
- **Request Body**: `{"university": "", "designation": ""}`

#### Response (`400 Bad Request`):
```json
{
    "status": "error",
    "message": "Validation failed",
    "errors": [
        "university: University name cannot be blank",
        "designation: Designation cannot be blank"
    ]
}
```

---

## 📮 Postman Collection

Import `postman_collection.json` located in the root directory directly into Postman to test:
1. Search Alumni (Success)
2. Search Alumni (Without Optional Passout Year)
3. Validation Error (Missing required fields)
4. Get Saved Alumni Profiles
5. Actuator Health Check
