package ch.insurance.api.service;

import ch.insurance.api.domain.Contract;
import ch.insurance.api.dto.ContractCostUpdateRequest;
import ch.insurance.api.dto.ContractRequest;
import ch.insurance.api.dto.ContractResponse;
import ch.insurance.api.dto.TotalCostResponse;
import ch.insurance.api.exception.ResourceNotFoundException;
import ch.insurance.api.repository.ClientRepository;
import ch.insurance.api.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final ClientRepository clientRepository;

    @Transactional
    public ContractResponse createContract(Long clientId, ContractRequest request) {
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client", clientId);
        }

        Contract contract = Contract.builder()
                .clientId(clientId)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .costAmount(request.getCostAmount())
                .build();

        if (contract.getEndDate() != null && contract.getStartDate() != null) {
            if (contract.getEndDate().isBefore(contract.getStartDate())) {
                throw new IllegalArgumentException("End date must be after or equal to start date");
            }
        }

        Contract savedContract = contractRepository.save(contract);
        return mapToResponse(savedContract);
    }

    @Transactional
    public ContractResponse updateContractCost(Long contractId, ContractCostUpdateRequest request) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract", contractId));

        contract.setCostAmount(request.getCostAmount());
        Contract updatedContract = contractRepository.save(contract);

        return mapToResponse(updatedContract);
    }

    @Transactional(readOnly = true)
    public List<ContractResponse> getActiveContracts(Long clientId, LocalDateTime modifiedAfter) {
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client", clientId);
        }

        List<Contract> contracts;
        LocalDate currentDate = LocalDate.now();

        if (modifiedAfter != null) {
            contracts = contractRepository.findActiveContractsByClientIdAndModifiedAfter(
                    clientId, currentDate, modifiedAfter);
        } else {
            contracts = contractRepository.findActiveContractsByClientId(clientId, currentDate);
        }

        return contracts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TotalCostResponse getTotalCost(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client", clientId);
        }

        LocalDate currentDate = LocalDate.now();
        BigDecimal totalCost = contractRepository.sumActiveContractsCostByClientId(clientId, currentDate);
        Long activeCount = contractRepository.countActiveContractsByClientId(clientId, currentDate);

        return TotalCostResponse.builder()
                .clientId(clientId)
                .totalCost(totalCost)
                .activeContractsCount(activeCount)
                .build();
    }

    private ContractResponse mapToResponse(Contract contract) {
        return ContractResponse.builder()
                .id(contract.getId())
                .clientId(contract.getClientId())
                .startDate(contract.getStartDate())
                .endDate(contract.getEndDate())
                .costAmount(contract.getCostAmount())
                .createdAt(contract.getCreatedAt())
                .build();
    }
}
