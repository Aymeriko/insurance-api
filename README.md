# Vaudoise Insurance API

A RESTful API for managing insurance clients and contracts, built with Spring Boot and Java 21.

## 📋 Table of Contents
- [Architecture Overview](#architecture-overview)
- [Quick Start](#quick-start)
- [API Documentation](#api-documentation)
- [Testing the API](#testing-the-api)
- [Technical Stack](#technical-stack)
- [Project Structure](#project-structure)

---

## 🏗️ Architecture Overview

### Design Philosophy (Max 1000 chars)

This API follows a **layered architecture** with clear separation of concerns:

**1. Domain Layer**: Entities use Single Table Inheritance for Client polymorphism (Person/Company), enabling type-safe operations while maintaining referential integrity. Immutable fields (birthDate, companyIdentifier) are enforced at the database level.

**2. Repository Layer**: Spring Data JPA with optimized queries. The performant total-cost endpoint uses a single aggregation query with proper indexing on endDate and clientId.

**3. Service Layer**: Business logic including soft-delete (contracts end-dated on client deletion) and automatic timestamp management (lastModifiedDate hidden from API).

**4. Controller Layer**: RESTful endpoints with comprehensive validation using Jakarta Bean Validation.

**Key Decisions**:
- H2 file-based database for persistence across restarts
- Soft-delete pattern preserves audit trail
- ISO 8601 date formatting throughout
- Indexed queries for performance
- Transactional consistency for cascading operations

---

## 🚀 Quick Start

### Prerequisites
- Java 21 or higher
- Maven 3.6+

### Running the Application

1. **Clone the repository**
```bash
git clone <repository-url>
cd insurance-api
```

2. **Build the project**
```bash
mvn clean install
```

3. **Run the application**
```bash
mvn spring-boot:run
```

The API will start on `http://localhost:8080`

### Database Console
Access H2 console at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/insurance`
- Username: `sa`
- Password: *(leave empty)*

---

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Client Endpoints

#### Create Person
```http
POST /api/clients/persons
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "+41 21 123 45 67",
  "birthDate": "1990-05-15"
}
```

#### Create Company
```http
POST /api/clients/companies
Content-Type: application/json

{
  "name": "Acme Corporation",
  "email": "contact@acme.com",
  "phone": "+41 21 987 65 43",
  "companyIdentifier": "ACM-123"
}
```

#### Get Client
```http
GET /api/clients/{id}
```

#### Get All Clients
```http
GET /api/clients
```

#### Update Client
```http
PUT /api/clients/{id}
Content-Type: application/json

{
  "name": "Updated Name",
  "email": "new.email@example.com",
  "phone": "+41 21 111 22 33"
}
```
*Note: birthDate and companyIdentifier cannot be updated*

#### Delete Client
```http
DELETE /api/clients/{id}
```
*Note: All active contracts will have their endDate set to current date*

### Contract Endpoints

#### Create Contract
```http
POST /api/clients/{clientId}/contracts
Content-Type: application/json

{
  "startDate": "2024-01-01",
  "endDate": "2025-12-31",
  "costAmount": 1500.50
}
```
*Note: startDate defaults to current date if not provided. endDate can be null for indefinite contracts*

#### Update Contract Cost
```http
PATCH /api/contracts/{contractId}/cost
Content-Type: application/json

{
  "costAmount": 1750.00
}
```
*Note: Automatically updates lastModifiedDate*

#### Get Active Contracts
```http
GET /api/clients/{clientId}/contracts
```

Optional filter by modification date:
```http
GET /api/clients/{clientId}/contracts?modifiedAfter=2024-01-01T00:00:00
```

#### Get Total Cost (Performant)
```http
GET /api/clients/{clientId}/contracts/total-cost
```

Response:
```json
{
  "clientId": 1,
  "totalCost": 5250.00,
  "activeContractsCount": 3
}
```

---

## 🧪 Testing the API

### Using cURL

**Create a Person:**
```bash
curl -X POST http://localhost:8080/api/clients/persons \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Smith",
    "email": "alice@example.com",
    "phone": "+41211234567",
    "birthDate": "1985-03-20"
  }'
```

**Create a Contract:**
```bash
curl -X POST http://localhost:8080/api/clients/1/contracts \
  -H "Content-Type: application/json" \
  -d '{
    "costAmount": 2500.00
  }'
```

**Get Total Cost:**
```bash
curl http://localhost:8080/api/clients/1/contracts/total-cost
```

### Using Postman

Import the provided collection: `docs/Insurance-API.postman_collection.json`

### Validation Examples

The API validates all inputs:

**Invalid Email:**
```json
{
  "name": "Test",
  "email": "invalid-email",
  "phone": "+41211234567",
  "birthDate": "1990-01-01"
}
```
Response: `400 Bad Request` with validation errors

**Invalid Company Identifier:**
```json
{
  "companyIdentifier": "INVALID"
}
```
Response: `400 Bad Request` - Must match format XXX-123

**Invalid Cost Amount:**
```json
{
  "costAmount": -100
}
```
Response: `400 Bad Request` - Must be positive

---

## 🛠️ Technical Stack

- **Java 21** - Programming language
- **Spring Boot 3.2.0** - Application framework
- **Spring Data JPA** - Data persistence
- **Hibernate** - ORM
- **H2 Database** - Embedded database with file persistence
- **Lombok** - Reduces boilerplate code
- **Jakarta Bean Validation** - Input validation
- **Maven** - Build tool

---

## 📁 Project Structure

```
insurance-api/
├── src/main/java/ch/insurance/api/
│   ├── InsuranceApiApplication.java      # Main application class
│   ├── controller/                      # REST controllers
│   │   ├── ClientController.java
│   │   └── ContractController.java
│   ├── service/                         # Business logic
│   │   ├── ClientService.java
│   │   └── ContractService.java
│   ├── repository/                      # Data access
│   │   ├── ClientRepository.java
│   │   └── ContractRepository.java
│   ├── domain/                          # Entity models
│   │   ├── Client.java
│   │   ├── Person.java
│   │   ├── Company.java
│   │   └── Contract.java
│   ├── dto/                             # Data transfer objects
│   │   ├── PersonRequest.java
│   │   ├── CompanyRequest.java
│   │   ├── ClientUpdateRequest.java
│   │   ├── ClientResponse.java
│   │   ├── ContractRequest.java
│   │   ├── ContractResponse.java
│   │   ├── ContractCostUpdateRequest.java
│   │   └── TotalCostResponse.java
│   └── exception/                       # Exception handling
│       ├── ResourceNotFoundException.java
│       ├── GlobalExceptionHandler.java
│       └── ErrorResponse.java
├── src/main/resources/
│   └── application.yml                  # Application configuration
├── docs/                                # Documentation
│   ├── DATA_MODEL.md                    # Detailed data model
│   ├── architecture-diagram.mmd         # Mermaid diagram
│   └── API_EXAMPLES.md                  # API usage examples
├── data/                                # H2 database files (auto-created)
├── pom.xml                              # Maven configuration
└── README.md                            # This file
```

---

## 🔍 Key Features

### ✅ Implemented Requirements

- [x] Create Person and Company clients with validation
- [x] Read client with all fields
- [x] Update client (excluding immutable fields)
- [x] Delete client with contract end-dating
- [x] Create contracts with default start date
- [x] Update contract cost with automatic timestamp
- [x] Get active contracts with optional date filtering
- [x] Performant total cost calculation endpoint
- [x] ISO 8601 date format throughout
- [x] Comprehensive validation (email, phone, dates, numbers)
- [x] RESTful API design with JSON
- [x] File-based persistence (survives restarts)
- [x] Descriptive code with clear naming

### 🚀 Performance Optimizations

1. **Database Indexes**: On clientId, endDate, and lastModifiedDate
2. **Aggregation Query**: Single query for total cost calculation
3. **Transactional Boundaries**: Proper transaction management
4. **Query Optimization**: Filtered queries at database level

### 🛡️ Data Integrity

- Immutable fields enforced at entity level
- Validation at multiple layers (DTO, Entity, Database)
- Soft-delete pattern for audit trail
- Transactional consistency for cascading operations

---

## 📝 Notes

- Database persists in `./data/insurance.mv.db`
- All dates use ISO 8601 format
- Active contracts: `endDate == null OR endDate > currentDate`
- lastModifiedDate is internal and not exposed in API responses
- Phone validation accepts international formats with optional country code
- Company identifier format: 3 uppercase letters, hyphen, 3 digits (e.g., ABC-123)

---

## 👨‍💻 Development

### Building for Production
```bash
mvn clean package
java -jar target/insurance-api-1.0.0.jar
```

### Running Tests
```bash
mvn test
```

---

## 📄 License

This project is part of a technical exercise for La Vaudoise.
