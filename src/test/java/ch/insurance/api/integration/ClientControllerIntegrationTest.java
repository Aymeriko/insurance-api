package ch.insurance.api.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.insurance.api.TestUtils;
import ch.insurance.api.domain.Client;
import ch.insurance.api.domain.Person;
import ch.insurance.api.dto.ClientUpdateRequest;
import ch.insurance.api.dto.PersonRequest;
import ch.insurance.api.repository.ClientRepository;

@Transactional
class ClientControllerIntegrationTest extends IntegrationTestBase {

  @Autowired private ClientRepository clientRepository;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void createPerson_ShouldReturnCreated() throws Exception {
    PersonRequest request = TestUtils.createPersonRequest();

    mockMvc
        .perform(
            post("/api/clients/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.email").value(request.getEmail()))
        .andExpect(jsonPath("$.phone").value(request.getPhone()))
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.updatedAt").isNotEmpty())
        .andExpect(jsonPath("$.clientType").value(Client.ClientType.PERSON.name()));
  }

  @Test
  void getClient_WhenClientExists_ShouldReturnClient() throws Exception {
    Person person = TestUtils.createTestSavedPerson();
    person = clientRepository.save(person);
    Long clientId = person.getId();

    mockMvc
        .perform(get("/api/clients/{id}", clientId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(clientId))
        .andExpect(jsonPath("$.clientType").value("PERSON"))
        .andExpect(jsonPath("$.email").value("john.doe@example.com"))
        .andExpect(jsonPath("$.phone").value("+41231234567"))
        .andExpect(jsonPath("$.firstName").value("John"))
        .andExpect(jsonPath("$.lastName").value("Doe"))
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.updatedAt").isNotEmpty())
        .andExpect(jsonPath("$.birthDate").value("1990-01-01"));
  }

  @Test
  void getClient_WhenClientNotExists_ShouldReturnNotFound() throws Exception {
    mockMvc
        .perform(get("/api/clients/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void updateClient_ShouldReturnUpdatedClient() throws Exception {
    // Given
    Person person = clientRepository.save(TestUtils.createTestSavedPerson());
    Long clientId = person.getId();

    String updatedFirstName = "Updated firstName";
    String updatedLastName = "Updated lastName";
    String updatedEmail = "updated@example.com";
    String updatedPhone = "+41123456789";
    LocalDate updatedBirthDate = LocalDate.of(1958, 5, 1);

    ClientUpdateRequest updateRequest =
        ClientUpdateRequest.builder()
            .firstName(updatedFirstName)
            .lastName(updatedLastName)
            .email(updatedEmail)
            .phone(updatedPhone)
            .build();

    String requestBody = objectMapper.writeValueAsString(updateRequest);

    // Then
    mockMvc
        .perform(
            put("/api/clients/{id}", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk())
        // Updated values
        .andExpect(jsonPath("$.id").value(clientId))
        .andExpect(jsonPath("$.firstName").value(updatedFirstName))
        .andExpect(jsonPath("$.lastName").value(updatedLastName))
        .andExpect(jsonPath("$.email").value(updatedEmail))
        .andExpect(jsonPath("$.phone").value(updatedPhone))
        .andExpect(jsonPath("$.birthDate").value("1990-01-01"))
        // Default values
        .andExpect(jsonPath("$.clientType").value("PERSON"))
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.updatedAt").isNotEmpty());
  }

  @Test
  void deleteClient_ShouldReturnNoContent() throws Exception {
    Person person = clientRepository.save(TestUtils.createTestSavedPerson());
    Long clientId = person.getId();

    mockMvc.perform(delete("/api/clients/{id}", clientId)).andExpect(status().isNoContent());

    // Verify deletion
    mockMvc.perform(get("/api/clients/{id}", clientId)).andExpect(status().isNotFound());
  }
}
