package ch.insurance.api.integration;

import ch.insurance.api.TestUtils;
import ch.insurance.api.domain.Person;
import ch.insurance.api.dto.PersonRequest;
import ch.insurance.api.repository.ClientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
class ClientControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void createPerson_ShouldReturnCreated() throws Exception {
        // Arrange
        PersonRequest request = TestUtils.createPersonRequest();

        // Act & Assert
        mockMvc.perform(post("/api/clients/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.clientType").value("PERSON"))
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.email").value(request.getEmail()));
    }

    @Test
    void getClient_WhenClientExists_ShouldReturnClient() throws Exception {
        // Arrange
        Person person = TestUtils.createTestPerson();
        person = clientRepository.save(person);
        Long clientId = person.getId();

        // Act & Assert
        mockMvc.perform(get("/api/clients/{id}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientId))
                .andExpect(jsonPath("$.clientType").value("PERSON"))
                .andExpect(jsonPath("$.name").value(person.getName()));
    }

    @Test
    void getClient_WhenClientNotExists_ShouldReturnNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/clients/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void updateClient_ShouldReturnUpdatedClient() throws Exception {
        // Arrange
        Person person = clientRepository.save(TestUtils.createTestPerson());
        Long clientId = person.getId();
        
        String updatedName = "Updated Name";
        String updatedEmail = "updated@example.com";
        
        String requestBody = String.format(
            "{\"name\":\"%s\",\"email\":\"%s\"}", 
            updatedName, 
            updatedEmail
        );

        // Act & Assert
        mockMvc.perform(put("/api/clients/{id}", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientId))
                .andExpect(jsonPath("$.name").value(updatedName))
                .andExpect(jsonPath("$.email").value(updatedEmail));
    }

    @Test
    void deleteClient_ShouldReturnNoContent() throws Exception {
        // Arrange
        Person person = clientRepository.save(TestUtils.createTestPerson());
        Long clientId = person.getId();

        // Act & Assert
        mockMvc.perform(delete("/api/clients/{id}", clientId))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/clients/{id}", clientId))
                .andExpect(status().isNotFound());
    }
}
