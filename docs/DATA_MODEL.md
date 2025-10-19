classDiagram
    class Client {
        <<Abstract>>
        +Long id
        +String email
        +String phone
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
        +ClientType clientType
    }

    class Person {
        +String firstName
        +String lastName
        +LocalDate birthDate
        +String getName()
    }

    class Company {
        +String companyName
        +String companyIdentifier
        +String industry
    }

    class Contract {
        +Long id
        +LocalDate startDate
        +LocalDate endDate
        +BigDecimal costAmount
        +LocalDateTime lastModifiedDate
        +Client client
    }

    class ClientType {
        <<Enumeration>>
        PERSON
        COMPANY
    }

    Client <|-- Person
    Client <|-- Company
    Client "1" -- "0..*" Contract
    Client o-- ClientType