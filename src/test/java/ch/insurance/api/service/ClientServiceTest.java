package ch.insurance.api.service;

import ch.insurance.api.TestUtils;
import ch.insurance.api.domain.Person;
import ch.insurance.api.dto.ClientResponse;
import ch.insurance.api.dto.ClientUpdateRequest;
import ch.insurance.api.dto.CompanyRequest;
import ch.insurance.api.dto.PersonRequest;
import ch.insurance.api.exception.ResourceNotFoundException;
import ch.insurance.api.repository.ClientRepository;
import ch.insurance.api.repository.ContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPerson_ShouldReturnClientResponse() {
        // Arrange
        PersonRequest request = TestUtils.createPersonRequest();
        Person person = TestUtils.createTestPerson();
        person.setId(1L);

        when(clientRepository.save(any(Person.class))).thenReturn(person);

        // Act
        ClientResponse response = clientService.createPerson(request);

        // Assert
        assertNotNull(response);
        assertEquals(person.getId(), response.getId());
        assertEquals("PERSON", response.getClientType());
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getPhone(), response.getPhone());
        assertEquals(request.getBirthdate(), response.getBirthdate());
    }

    @Test
    void getClientById_WhenClientExists_ShouldReturnClientResponse() {
        // Arrange
        Long clientId = 1L;
        Person person = TestUtils.createTestPerson();
        person.setId(clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(person));

        // Act
        ClientResponse response = clientService.getClientById(clientId);

        // Assert
        assertNotNull(response);
        assertEquals(clientId, response.getId());
        assertEquals("PERSON", response.getClientType());
    }

    @Test
    void getClientById_WhenClientNotExists_ShouldThrowException() {
        // Arrange
        Long clientId = 999L;
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> clientService.getClientById(clientId));
    }

    @Test
    void updateClient_ShouldUpdateAndReturnUpdatedClient() {
        // Arrange
        Long clientId = 1L;
        ClientUpdateRequest updateRequest = new ClientUpdateRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setPhone("+41123456789");

        Person existingPerson = TestUtils.createTestPerson();
        existingPerson.setId(clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingPerson));
        when(clientRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ClientResponse response = clientService.updateClient(clientId, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(updateRequest.getName(), response.getName());
        assertEquals(updateRequest.getEmail(), response.getEmail());
        assertEquals(updateRequest.getPhone(), response.getPhone());
    }

    @Test
    void deleteClient_ShouldEndActiveContracts() {
        // Arrange
        Long clientId = 1L;
        Person person = TestUtils.createTestPerson();
        person.setId(clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(person));
        doNothing().when(clientRepository).delete(any(Person.class));

        // Act & Assert
        assertDoesNotThrow(() -> clientService.deleteClient(clientId));
        verify(contractRepository, times(1)).findByClientId(clientId);
        verify(clientRepository, times(1)).delete(person);
    }
}
