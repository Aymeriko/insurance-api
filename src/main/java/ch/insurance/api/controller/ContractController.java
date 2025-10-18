package ch.insurance.api.controller;

import ch.insurance.api.dto.ContractCostUpdateRequest;
import ch.insurance.api.dto.ContractRequest;
import ch.insurance.api.dto.ContractResponse;
import ch.insurance.api.dto.TotalCostResponse;
import ch.insurance.api.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @PostMapping("/clients/{clientId}/contracts")
    public ResponseEntity<ContractResponse> createContract(
            @PathVariable Long clientId,
            @Valid @RequestBody ContractRequest request) {
        ContractResponse response = contractService.createContract(clientId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/contracts/{contractId}/cost")
    public ResponseEntity<ContractResponse> updateContractCost(
            @PathVariable Long contractId,
            @Valid @RequestBody ContractCostUpdateRequest request) {
        ContractResponse response = contractService.updateContractCost(contractId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/clients/{clientId}/contracts")
    public ResponseEntity<List<ContractResponse>> getActiveContracts(
            @PathVariable Long clientId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime modifiedAfter) {
        List<ContractResponse> responses = contractService.getActiveContracts(clientId, modifiedAfter);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/clients/{clientId}/contracts/total-cost")
    public ResponseEntity<TotalCostResponse> getTotalCost(@PathVariable Long clientId) {
        TotalCostResponse response = contractService.getTotalCost(clientId);
        return ResponseEntity.ok(response);
    }
}
