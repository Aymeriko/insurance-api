package ch.insurance.api.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ch.insurance.api.domain.Client;
import ch.insurance.api.domain.Contract;
import ch.insurance.api.exception.ResourceNotFoundException;
import ch.insurance.api.repository.ContractRepository;
import ch.insurance.api.service.ContractService;

@ExtendWith(MockitoExtension.class)
class ContractControllerTest {

  private MockMvc mockMvc;

  @Mock private ContractService contractService;

  @Mock private ContractRepository contractRepository;

  @InjectMocks private ContractController contractController;

  private Client testClient;
  private Contract testContract;

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
}
