package ch.insurance.api.service;

import ch.insurance.api.TestUtils;
import ch.insurance.api.domain.Contract;
import ch.insurance.api.dto.ContractCostUpdateRequest;
import ch.insurance.api.dto.ContractRequest;
import ch.insurance.api.dto.ContractResponse;
import ch.insurance.api.dto.TotalCostResponse;
import ch.insurance.api.exception.ResourceNotFoundException;
import ch.insurance.api.repository.ClientRepository;
import ch.insurance.api.repository.ContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ContractServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ContractService contractService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createContract_ShouldReturnCreatedContract() {
        // Arrange
        Long clientId = 1L;
        ContractRequest request = TestUtils.createContractRequest();
        Contract contract = TestUtils.createTestContract(clientId);
        contract.setId(1L);

        when(clientRepository.existsById(clientId)).thenReturn(true);
        when(contractRepository.save(any(Contract.class))).thenReturn(contract);

        // Act
        ContractResponse response = contractService.createContract(clientId, request);

        // Assert
        assertNotNull(response);
        assertEquals(contract.getId(), response.getId());
        assertEquals(clientId, response.getClientId());
        assertEquals(request.getStartDate(), response.getStartDate());
        assertEquals(request.getEndDate(), response.getEndDate());
        assertEquals(0, request.getCostAmount().compareTo(response.getCostAmount()));
    }

    @Test
    void updateContractCost_ShouldUpdateCostAndReturnUpdatedContract() {
        // Arrange
        Long contractId = 1L;
        ContractCostUpdateRequest updateRequest = TestUtils.createCostUpdateRequest();
        Contract existingContract = TestUtils.createTestContract(1L);
        existingContract.setId(contractId);

        when(contractRepository.findById(contractId)).thenReturn(java.util.Optional.of(existingContract));
        when(contractRepository.save(any(Contract.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ContractResponse response = contractService.updateContractCost(contractId, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(0, updateRequest.getCostAmount().compareTo(response.getCostAmount()));
        verify(contractRepository, times(1)).save(any(Contract.class));
    }

    @Test
    void getActiveContracts_ShouldReturnFilteredContracts() {
        // Arrange
        Long clientId = 1L;
        LocalDateTime modifiedAfter = LocalDateTime.now().minusDays(1);
        Contract activeContract = TestUtils.createTestContract(clientId);
        activeContract.setId(1L);
        activeContract.setLastModifiedDate(LocalDateTime.now());

        when(clientRepository.existsById(clientId)).thenReturn(true);
        when(contractRepository.findActiveContractsByClientIdAndModifiedAfter(
                eq(clientId), any(LocalDate.class), eq(modifiedAfter)))
                .thenReturn(Arrays.asList(activeContract));

        // Act
        List<ContractResponse> responses = contractService.getActiveContracts(clientId, modifiedAfter);

        // Assert
        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals(activeContract.getId(), responses.get(0).getId());
    }

    @Test
    void getTotalCost_ShouldReturnSumOfActiveContracts() {
        // Arrange
        Long clientId = 1L;
        BigDecimal expectedTotal = new BigDecimal("3000.00");
        Long expectedCount = 2L;

        when(clientRepository.existsById(clientId)).thenReturn(true);
        when(contractRepository.sumActiveContractsCostByClientId(eq(clientId), any(LocalDate.class)))
                .thenReturn(expectedTotal);
        when(contractRepository.countActiveContractsByClientId(eq(clientId), any(LocalDate.class)))
                .thenReturn(expectedCount);

        // Act
        TotalCostResponse response = contractService.getTotalCost(clientId);

        // Assert
        assertNotNull(response);
        assertEquals(clientId, response.getClientId());
        assertEquals(0, expectedTotal.compareTo(response.getTotalCost()));
        assertEquals(expectedCount, response.getActiveContractsCount());
    }

    @Test
    void createContract_WithInvalidClient_ShouldThrowException() {
        // Arrange
        Long clientId = 999L;
        ContractRequest request = TestUtils.createContractRequest();

        when(clientRepository.existsById(clientId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
            () -> contractService.createContract(clientId, request));
    }
}
