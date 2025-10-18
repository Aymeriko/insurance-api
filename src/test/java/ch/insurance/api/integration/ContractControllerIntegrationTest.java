package ch.insurance.api.integration;

import ch.insurance.api.TestUtils;
import ch.insurance.api.domain.Contract;
import ch.insurance.api.domain.Person;
import ch.insurance.api.dto.ContractCostUpdateRequest;
import ch.insurance.api.dto.ContractRequest;
import ch.insurance.api.repository.ClientRepository;
import ch.insurance.api.repository.ContractRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
class ContractControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ClientRepository clientRepository;

    private Long clientId;

    @BeforeEach
    void setUp() {
        // Create a test client
        Person client = clientRepository.save(TestUtils.createTestPerson());
        clientId = client.getId();
    }

    @Test
    void createContract_ShouldReturnCreated() throws Exception {
        // Arrange
        ContractRequest request = TestUtils.createContractRequest();

        // Act & Assert
        mockMvc.perform(post("/api/clients/{clientId}/contracts", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.clientId").value(clientId))
                .andExpect(jsonPath("$.startDate").exists())
                .andExpect(jsonPath("$.endDate").exists())
                .andExpect(jsonPath("$.costAmount").isNumber());
    }

    @Test
    void updateContractCost_ShouldReturnUpdatedContract() throws Exception {
        // Arrange
        Contract contract = TestUtils.createTestContract(clientId);
        contract = contractRepository.save(contract);
        Long contractId = contract.getId();

        BigDecimal newCost = new BigDecimal("2500.75");
        ContractCostUpdateRequest updateRequest = new ContractCostUpdateRequest();
        updateRequest.setCostAmount(newCost);

        // Act & Assert
        mockMvc.perform(put("/api/contracts/{id}/cost", contractId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(contractId))
                .andExpect(jsonPath("$.costAmount").value(newCost.doubleValue()));
    }

    @Test
    void getActiveContracts_ShouldReturnActiveContracts() throws Exception {
        // Arrange
        // Create active contract
        Contract activeContract = TestUtils.createTestContract(clientId);
        activeContract.setStartDate(LocalDate.now().minusMonths(1));
        activeContract.setEndDate(LocalDate.now().plusMonths(11));
        contractRepository.save(activeContract);

        // Create inactive contract
        Contract inactiveContract = TestUtils.createTestContract(clientId);
        inactiveContract.setStartDate(LocalDate.now().minusYears(2));
        inactiveContract.setEndDate(LocalDate.now().minusYears(1));
        contractRepository.save(inactiveContract);

        // Act & Assert
        mockMvc.perform(get("/api/clients/{clientId}/contracts/active", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(activeContract.getId()));
    }

    @Test
    void getTotalCost_ShouldReturnSumOfActiveContracts() throws Exception {
        // Arrange
        // Create multiple active contracts
        Contract contract1 = TestUtils.createTestContract(clientId);
        contract1.setCostAmount(new BigDecimal("1000.00"));
        contract1.setStartDate(LocalDate.now().minusMonths(1));
        contract1.setEndDate(LocalDate.now().plusMonths(11));
        contractRepository.save(contract1);

        Contract contract2 = TestUtils.createTestContract(clientId);
        contract2.setCostAmount(new BigDecimal("1500.50"));
        contract2.setStartDate(LocalDate.now().minusDays(10));
        contract2.setEndDate(LocalDate.now().plusMonths(6));
        contractRepository.save(contract2);

        BigDecimal expectedTotal = new BigDecimal("2500.50");

        // Act & Assert
        mockMvc.perform(get("/api/clients/{clientId}/contracts/total-cost", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(clientId))
                .andExpect(jsonPath("$.totalCost").value(expectedTotal.doubleValue()))
                .andExpect(jsonPath("$.activeContractsCount").value(2));
    }

    @Test
    void getContract_WhenContractExists_ShouldReturnContract() throws Exception {
        // Arrange
        Contract contract = contractRepository.save(TestUtils.createTestContract(clientId));
        Long contractId = contract.getId();

        // Act & Assert
        mockMvc.perform(get("/api/contracts/{id}", contractId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(contractId))
                .andExpect(jsonPath("$.clientId").value(clientId));
    }
}
