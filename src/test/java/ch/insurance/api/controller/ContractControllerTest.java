package ch.insurance.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ch.insurance.api.TestUtils;
import ch.insurance.api.domain.Client;
import ch.insurance.api.dto.ContractResponse;
import ch.insurance.api.exception.ResourceNotFoundException;
import ch.insurance.api.service.ContractService;

@ExtendWith(MockitoExtension.class)
class ContractControllerTest {

  private MockMvc mockMvc;

  @Mock private ContractService contractService;

  @InjectMocks private ContractController contractController;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(contractController).build();
  }

  @Test
  void deleteContract_WhenValidRequest_ReturnsNoContent() throws Exception {
    // Given
    Long existingId = 1L;
    doNothing().when(contractService).deleteContract(anyLong());

    // When & Then
    mockMvc
        .perform(delete("/api/contracts/{contractId}", existingId))
        .andExpect(status().isNoContent());

    verify(contractService, times(1)).deleteContract(existingId);
  }

  @Test
  void deleteContract_WhenContractNotFound_ReturnsNotFound() throws Exception {
    // Given
    Long nonExistentContractId = 999L;
    doThrow(new ResourceNotFoundException("Contract not found with id: " + nonExistentContractId))
        .when(contractService)
        .deleteContract(nonExistentContractId);

    // When & Then
    mockMvc
        .perform(delete("/api/contracts/{contractId}", nonExistentContractId))
        .andExpect(status().isNotFound());

    verify(contractService, times(1)).deleteContract(anyLong());
  }

  @Test
  void getActiveContracts_WhenClientExists_ShouldReturnContracts() throws Exception {
    // Given
    Long clientId = 1L;
    LocalDateTime modifiedAfter = LocalDateTime.now().minusDays(1);

    Client client = TestUtils.createTestSavedPerson();
    client.setId(clientId);

    ContractResponse contract1 =
        ContractResponse.builder()
            .id(1L)
            .clientId(clientId)
            .startDate(LocalDateTime.now().toLocalDate())
            .endDate(LocalDateTime.now().plusYears(1).toLocalDate())
            .costAmount(new BigDecimal("1000.00"))
            .build();

    ContractResponse contract2 =
        ContractResponse.builder()
            .id(2L)
            .clientId(clientId)
            .startDate(LocalDateTime.now().toLocalDate())
            .endDate(LocalDateTime.now().plusYears(2).toLocalDate())
            .costAmount(new BigDecimal("2000.00"))
            .build();

    List<ContractResponse> contracts = Arrays.asList(contract1, contract2);

    when(contractService.getActiveContracts(eq(clientId), any(LocalDateTime.class)))
        .thenReturn(contracts);

    // When & Then
    mockMvc
        .perform(
            get("/api/clients/{clientId}/contracts", clientId)
                .param("modifiedAfter", modifiedAfter.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(contract1.getId()))
        .andExpect(jsonPath("$[0].clientId").value(clientId))
        .andExpect(jsonPath("$[1].id").value(contract2.getId()))
        .andExpect(jsonPath("$[1].clientId").value(clientId));

    verify(contractService).getActiveContracts(eq(clientId), any(LocalDateTime.class));
  }

  @Test
  void getActiveContracts_WhenClientDoesNotExist_ShouldReturnEmptyList() throws Exception {
    // Given
    Long nonExistentClientId = 999L;
    LocalDateTime modifiedAfter = LocalDateTime.now().minusDays(1);

    when(contractService.getActiveContracts(eq(nonExistentClientId), any(LocalDateTime.class)))
        .thenReturn(List.of());

    // When & Then
    mockMvc
        .perform(
            get("/api/clients/{clientId}/contracts", nonExistentClientId)
                .param("modifiedAfter", modifiedAfter.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));

    verify(contractService).getActiveContracts(eq(nonExistentClientId), any(LocalDateTime.class));
  }

  @Test
  void getActiveContracts_WhenModifiedAfterNotProvided_ShouldUseDefaultValue() throws Exception {
    // Given
    Long clientId = 1L;

    when(contractService.getActiveContracts(eq(clientId), isNull())).thenReturn(List.of());

    // When & Then
    mockMvc
        .perform(get("/api/clients/{clientId}/contracts", clientId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));

    verify(contractService).getActiveContracts(eq(clientId), isNull());
  }
}
