# Conceptual Data Model

## Overview
The system manages two main entities: **Clients** and **Contracts**. Clients can be either Persons or Companies, following a single-table inheritance pattern.

## Entity Relationship Diagram

```
┌─────────────────────────────────────────────────────────┐
│                        Client                           │
│─────────────────────────────────────────────────────────│
│ + id: Long (PK)                                         │
│ + clientType: String (PERSON/COMPANY)                   │
│ + name: String                                          │
│ + email: String                                         │
│ + phone: String                                         │
│ + createdAt: LocalDateTime                              │
│ + updatedAt: LocalDateTime                              │
│─────────────────────────────────────────────────────────│
│ Person specific:                                        │
│ + birthdate: LocalDate (immutable)                      │
│─────────────────────────────────────────────────────────│
│ Company specific:                                       │
│ + companyIdentifier: String (immutable, format: xxx-###)│
└─────────────────────────────────────────────────────────┘
                          │
                          │ 1
                          │
                          │ has
                          │
                          │ 0..*
                          ▼
┌─────────────────────────────────────────────────────────┐
│                       Contract                          │
│─────────────────────────────────────────────────────────│
│ + id: Long (PK)                                         │
│ + clientId: Long (FK)                                   │
│ + startDate: LocalDate                                  │
│ + endDate: LocalDate (nullable)                         │
│ + costAmount: BigDecimal                                │
│ + lastModifiedDate: LocalDateTime (internal)            │
│ + createdAt: LocalDateTime                              │
└─────────────────────────────────────────────────────────┘
```

## Entity Descriptions

### Client (Abstract)
Base entity representing any client in the system.

**Attributes:**
- `id`: Unique identifier (auto-generated)
- `clientType`: Discriminator field (PERSON or COMPANY)
- `name`: Client name (required, updatable)
- `email`: Email address (required, validated, updatable)
- `phone`: Phone number (required, validated, updatable)
- `createdAt`: Timestamp of creation
- `updatedAt`: Timestamp of last update

### Person (extends Client)
Represents an individual client.

**Additional Attributes:**
- `birthdate`: Date of birth (required, **immutable** after creation, validated)

**Validation Rules:**
- Birthdate must be in the past
- Birthdate cannot be updated after creation
- Must be at least 18 years old (business rule)

### Company (extends Client)
Represents a corporate client.

**Additional Attributes:**
- `companyIdentifier`: Unique company identifier (required, **immutable**, format: `aaa-123`)

**Validation Rules:**
- Company identifier format: 3 letters, hyphen, 3 digits (e.g., "ABC-123")
- Company identifier cannot be updated after creation
- Company identifier must be unique

### Contract
Represents an insurance contract for a client.

**Attributes:**
- `id`: Unique identifier (auto-generated)
- `clientId`: Foreign key to Client (required)
- `startDate`: Contract start date (defaults to current date if not provided)
- `endDate`: Contract end date (nullable, null means active indefinitely)
- `costAmount`: Contract cost (required, positive number, updatable)
- `lastModifiedDate`: Internal timestamp (auto-updated, **not exposed in API**)
- `createdAt`: Timestamp of creation

**Business Rules:**
- Active contract: `endDate == null OR endDate > currentDate`
- When client is deleted, all their contracts' `endDate` is set to current date
- Updating `costAmount` automatically updates `lastModifiedDate`
- `startDate` must be before or equal to `endDate` (if endDate is provided)

## Relationships

### Client → Contract (One-to-Many)
- One client can have multiple contracts (0..*)
- Each contract belongs to exactly one client (1)
- Cascade behavior: When client is deleted, contracts are soft-deleted (endDate set to current date)

## Key Design Decisions

### 1. Single Table Inheritance
Using a discriminator column (`clientType`) to store both Person and Company in the same table. This approach:
- ✅ Simplifies queries across all clients
- ✅ Maintains referential integrity with contracts
- ✅ Allows polymorphic relationships
- ⚠️ May have some null columns (birthdate for companies, companyIdentifier for persons)

### 2. Soft Delete for Contracts
When a client is deleted, contracts are not physically deleted but marked as ended:
- ✅ Preserves historical data
- ✅ Maintains audit trail
- ✅ Allows reporting on past contracts

### 3. Immutable Fields
`birthdate` and `companyIdentifier` are immutable after creation:
- ✅ Prevents data integrity issues
- ✅ Reflects real-world constraints (birthdates don't change)
- ✅ Simplifies auditing

### 4. Internal vs External Fields
`lastModifiedDate` is internal and not exposed via API:
- ✅ Separates internal tracking from external interface
- ✅ Prevents client manipulation of metadata
- ✅ Allows filtering without exposing implementation details

## Database Indexes

For optimal performance:

```sql
-- Primary Keys (auto-indexed)
Client.id
Contract.id

-- Foreign Keys
Contract.clientId (indexed for joins)

-- Query Optimization
Contract.endDate (for active contract queries)
Contract.lastModifiedDate (for filtering by update date)
Client.email (for lookup/uniqueness)
Company.companyIdentifier (for lookup/uniqueness)
```

## API Endpoints Mapping

| Operation | Endpoint | Entity |
|-----------|----------|--------|
| Create Person | POST /api/clients/persons | Person |
| Create Company | POST /api/clients/companies | Company |
| Get Client | GET /api/clients/{id} | Client |
| Update Client | PUT/PATCH /api/clients/{id} | Client |
| Delete Client | DELETE /api/clients/{id} | Client |
| Create Contract | POST /api/clients/{clientId}/contracts | Contract |
| Update Contract Cost | PATCH /api/contracts/{id}/cost | Contract |
| Get Client Contracts | GET /api/clients/{clientId}/contracts | Contract |
| Get Total Cost | GET /api/clients/{clientId}/contracts/total-cost | Contract |

## Validation Summary

### Email
- Format: RFC 5322 compliant
- Example: `user@example.com`

### Phone
- Format: International format with optional country code
- Example: `+41 21 123 45 67` or `0211234567`

### Dates
- Format: ISO 8601 (`yyyy-MM-dd` for dates, `yyyy-MM-dd'T'HH:mm:ss` for timestamps)
- Example: `2024-01-15` or `2024-01-15T14:30:00`

### Company Identifier
- Format: `[A-Z]{3}-[0-9]{3}`
- Example: `ABC-123`

### Cost Amount
- Type: Decimal with 2 decimal places
- Constraint: Must be positive (> 0)
- Example: `1250.50`
