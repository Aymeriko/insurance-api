package ch.insurance.api;

import ch.insurance.api.domain.Client;
import ch.insurance.api.domain.Company;
import ch.insurance.api.domain.Contract;
import ch.insurance.api.domain.Person;
import ch.insurance.api.dto.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class TestUtils {

    public static Person createTestPerson() {
        return Person.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("+41231234567")
                .birthdate(LocalDate.of(1990, 1, 1))
                .build();
    }

    public static Company createTestCompany() {
        return Company.builder()
                .name("Test Company")
                .email("contact@test.com")
                .phone("+41239876543")
                .companyIdentifier("TST-123")
                .build();
    }

    public static Contract createTestContract(Client client) {
        LocalDateTime now = LocalDateTime.now();
        return Contract.builder()
                .client(client)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .costAmount(new BigDecimal("1500.50"))
                .lastModifiedDate(now)
                .createdAt(now)
                .build();
    }

    public static PersonRequest createPersonRequest() {
        return PersonRequest.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("+41231234567")
                .birthdate(LocalDate.of(1990, 1, 1))
                .build();
    }

    public static CompanyRequest createCompanyRequest() {
        return CompanyRequest.builder()
                .name("Test Company")
                .email("contact@test.com")
                .phone("+41239876543")
                .companyIdentifier("TST-123")
                .build();
    }

    public static ContractRequest createContractRequest() {
        return ContractRequest.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .costAmount(new BigDecimal("1500.50"))
                .build();
    }

    public static ContractCostUpdateRequest createCostUpdateRequest() {
        return ContractCostUpdateRequest.builder()
                .costAmount(new BigDecimal("2000.00"))
                .build();
    }
}
