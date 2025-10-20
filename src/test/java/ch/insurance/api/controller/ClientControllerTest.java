package ch.insurance.api.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.insurance.api.TestUtils;
import ch.insurance.api.domain.Client;
import ch.insurance.api.dto.ClientResponse;
import ch.insurance.api.dto.CompanyRequest;
import ch.insurance.api.service.ClientService;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

  private MockMvc mockMvc;

  @Mock private ClientService clientService;

  @InjectMocks private ClientController clientController;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(clientController).build();
  }

  @Test
  void getAllClients_ShouldReturnListOfClients() throws Exception {
    // Given
    Client client1 = TestUtils.createTestSavedPerson();
    client1.setId(1L);
    Client client2 = TestUtils.createTestSavedCompany();
    client2.setId(2L);

    List<ClientResponse> clients =
        Arrays.asList(
            ClientResponse.builder()
                .id(client1.getId())
                .email(client1.getEmail())
                .phone(client1.getPhone())
                .build(),
            ClientResponse.builder()
                .id(client2.getId())
                .email(client2.getEmail())
                .phone(client2.getPhone())
                .build());

    when(clientService.getAllClients()).thenReturn(clients);

    // When & Then
    mockMvc
        .perform(get("/api/clients"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(client1.getId()))
        .andExpect(jsonPath("$[0].email").value(client1.getEmail()))
        .andExpect(jsonPath("$[1].id").value(client2.getId()))
        .andExpect(jsonPath("$[1].email").value(client2.getEmail()));

    verify(clientService).getAllClients();
  }

  @Test
  void getAllClients_WhenNoClientsExist_ShouldReturnEmptyList() throws Exception {
    // Given
    when(clientService.getAllClients()).thenReturn(List.of());

    // When & Then
    mockMvc
        .perform(get("/api/clients"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));

    verify(clientService).getAllClients();
  }

  @Test
  void createCompany_WithValidRequest_ShouldReturnCreated() throws Exception {
    // Given
    CompanyRequest request =
        CompanyRequest.builder()
            .email("company@example.com")
            .phone("+1234567890")
            .companyIdentifier("ABC-123")
            .build();

    ClientResponse expectedResponse =
        ClientResponse.builder()
            .id(1L)
            .clientType("COMPANY")
            .email("company@example.com")
            .phone("+1234567890")
            .companyIdentifier("ABC-123")
            .build();

    when(clientService.createCompany(any(CompanyRequest.class))).thenReturn(expectedResponse);

    // When & Then
    mockMvc
        .perform(
            post("/api/clients/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.clientType").value("COMPANY"))
        .andExpect(jsonPath("$.email").value("company@example.com"))
        .andExpect(jsonPath("$.phone").value("+1234567890"))
        .andExpect(jsonPath("$.companyIdentifier").value("ABC-123"));

    verify(clientService).createCompany(any(CompanyRequest.class));
  }
}
