package ch.insurance.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import ch.insurance.api.domain.Client;
import ch.insurance.api.domain.Company;
import ch.insurance.api.domain.Contract;
import ch.insurance.api.domain.Person;
import ch.insurance.api.dto.*;

public class TestUtils {
  static final LocalDateTime fixedDate = LocalDateTime.of(1970, 1, 1, 1, 2, 3);

  public static Person createTestSavedPerson() {
    return Person.builder()
        .clientType(Client.ClientType.PERSON)
        .firstName("John")
        .lastName("Doe")
        .email("john.doe@example.com")
        .phone("+41231234567")
        .birthDate(LocalDate.of(1990, 1, 1))
        .createdAt(fixedDate)
        .updatedAt(fixedDate)
        .build();
  }

  public static Company createTestSavedCompany() {
    return Company.builder()
        .email("contact@test.com")
        .phone("+41239876543")
        .companyIdentifier("TST-123")
        .clientType(Client.ClientType.COMPANY)
        .createdAt(fixedDate)
        .updatedAt(fixedDate)
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
        .email("john.doe@example.com")
        .phone("+41231234567")
        .clientType(Client.ClientType.PERSON.name())
        .firstName("John")
        .lastName("Doe")
        .birthDate(LocalDate.of(1990, 1, 1))
        .build();
  }

  public static CompanyRequest createCompanyRequest() {
    return CompanyRequest.builder()
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
    return ContractCostUpdateRequest.builder().costAmount(new BigDecimal("2000.00")).build();
  }
}
