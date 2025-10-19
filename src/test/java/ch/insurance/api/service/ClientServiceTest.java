package ch.insurance.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ch.insurance.api.TestUtils;
import ch.insurance.api.domain.Client;
import ch.insurance.api.domain.Company;
import ch.insurance.api.domain.Person;
import ch.insurance.api.dto.ClientResponse;
import ch.insurance.api.dto.ClientUpdateRequest;
import ch.insurance.api.dto.CompanyRequest;
import ch.insurance.api.dto.PersonRequest;
import ch.insurance.api.exception.ResourceNotFoundException;
import ch.insurance.api.repository.ClientRepository;
import ch.insurance.api.repository.ContractRepository;

class ClientServiceTest {

  @Autowired private TestEntityManager entityManager;

  @Mock private ClientRepository clientRepository;

  @Mock private ContractRepository contractRepository;

  @InjectMocks private ClientService clientService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createPerson_ShouldReturnClientResponse() {
    // Given
    PersonRequest request = TestUtils.createPersonRequest();
    Person savedPerson = TestUtils.createTestSavedPerson();
    savedPerson.setId(1L);

    when(clientRepository.save(any(Person.class))).thenReturn(savedPerson);

    // When
    ClientResponse response = clientService.createPerson(request);

    // Then
    assertNotNull(response, "Response should not be null");
    assertEquals(savedPerson.getId(), response.getId(), "ID should match the saved person's ID");
    assertEquals(
        Client.ClientType.PERSON.name(), response.getClientType(), "Client type should be PERSON");
    assertEquals(request.getEmail(), response.getEmail(), "Email should match the request");
    assertEquals(request.getPhone(), response.getPhone(), "Phone should match the request");
    assertEquals(
        request.getFirstName(), response.getFirstName(), "First name should match the request");
    assertEquals(
        request.getLastName(), response.getLastName(), "Last name should match the request");
    assertEquals(
        request.getBirthDate(), response.getBirthDate(), "Birthdate should match the request");
    assertNotNull(response.getCreatedAt(), "Created date should be set");
    assertNotNull(response.getUpdatedAt(), "Updated date should be set");
  }

  @Test
  void getClientById_WhenClientExists_ShouldReturnClientResponse() {
    // Given
    Long clientId = 1L;
    Person person = TestUtils.createTestSavedPerson();
    person.setId(clientId);

    when(clientRepository.findById(clientId)).thenReturn(Optional.of(person));

    // When
    ClientResponse response = clientService.getClientById(clientId);

    // Then
    assertNotNull(response, "Response should not be null");
    assertEquals(clientId, response.getId(), "Response ID should match the requested ID");
    assertEquals("PERSON", response.getClientType(), "Client type should be PERSON");
  }

  @Test
  void getClientById_WhenClientNotExists_ShouldThrowException() {
    // Given
    Long nonExistentClientId = 999L;
    when(clientRepository.findById(nonExistentClientId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(
        ResourceNotFoundException.class,
        () -> clientService.getClientById(nonExistentClientId),
        "Should throw ResourceNotFoundException when client doesn't exist");
  }

  @Test
  void getAllClients_ShouldReturnListOfClients() {
    // Given
    Person person1 = TestUtils.createTestSavedPerson();
    person1.setId(1L);
    Person person2 = TestUtils.createTestSavedPerson();
    person2.setId(2L);
    person2.setEmail("another.person@example.com");

    when(clientRepository.findAll()).thenReturn(List.of(person1, person2));

    // When
    List<ClientResponse> result = clientService.getAllClients();

    // Then
    assertNotNull(result, "Result should not be null");
    assertEquals(2, result.size(), "Should return 2 clients");

    // Verify first client
    assertEquals(1L, result.get(0).getId(), "First client ID should match");
    assertEquals(
        "john.doe@example.com", result.get(0).getEmail(), "First client email should match");

    // Verify second client
    assertEquals(2L, result.get(1).getId(), "Second client ID should match");
    assertEquals(
        "another.person@example.com", result.get(1).getEmail(), "Second client email should match");
  }

  @Test
  void updateClient_ShouldUpdateAndReturnUpdatedClient() {
    // Given
    Long clientId = 1L;
    ClientUpdateRequest updateRequest = new ClientUpdateRequest();
    updateRequest.setLastName("Updated Name");
    updateRequest.setEmail("updated@example.com");
    updateRequest.setPhone("+41123456789");

    Person existingPerson = TestUtils.createTestSavedPerson();
    existingPerson.setId(clientId);

    when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingPerson));
    when(clientRepository.save(any(Person.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // When
    ClientResponse response = clientService.updateClient(clientId, updateRequest);

    // Then
    assertNotNull(response, "Response should not be null");
    assertEquals(
        updateRequest.getLastName(), response.getLastName(), "Last name should be updated");
    assertEquals(updateRequest.getEmail(), response.getEmail(), "Email should be updated");
    assertEquals(updateRequest.getPhone(), response.getPhone(), "Phone should be updated");
  }

  @Test
  void deleteClient_ShouldEndActiveContracts() {
    // Given
    Long clientId = 1L;
    Person person = TestUtils.createTestSavedPerson();
    person.setId(clientId);

    when(clientRepository.findById(clientId)).thenReturn(Optional.of(person));
    doNothing().when(clientRepository).delete(any(Person.class));

    // When
    assertDoesNotThrow(
        () -> clientService.deleteClient(clientId),
        "Deleting a client should not throw an exception");

    // Then
    verify(contractRepository, times(1)).findByClientId(clientId);
    verify(clientRepository, times(1)).delete(person);
  }

  @Test
  void createCompany_ShouldReturnClientResponse() {
    // Given
    CompanyRequest request = TestUtils.createCompanyRequest();
    Company savedCompany = TestUtils.createTestSavedCompany();
    savedCompany.setId(1L);

    when(clientRepository.save(any(Company.class))).thenReturn(savedCompany);

    // When
    ClientResponse response = clientService.createCompany(request);

    // Then
    assertNotNull(response, "Response should not be null");
    assertEquals(savedCompany.getId(), response.getId(), "ID should match the saved company's ID");
    assertEquals(
        Client.ClientType.COMPANY.name(),
        response.getClientType(),
        "Client type should be COMPANY");
    assertEquals(request.getEmail(), response.getEmail(), "Email should match the request");
    assertEquals(request.getPhone(), response.getPhone(), "Phone should match the request");
    assertEquals(
        request.getCompanyIdentifier(),
        response.getCompanyIdentifier(),
        "Company identifier should match the request");
    assertNotNull(response.getCreatedAt(), "Created date should be set");
    assertNotNull(response.getUpdatedAt(), "Updated date should be set");
  }
}
