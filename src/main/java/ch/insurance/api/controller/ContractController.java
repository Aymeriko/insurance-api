package ch.insurance.api.controller;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.insurance.api.dto.ContractCostUpdateRequest;
import ch.insurance.api.dto.ContractRequest;
import ch.insurance.api.dto.ContractResponse;
import ch.insurance.api.dto.TotalCostResponse;
import ch.insurance.api.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Contracts", description = "API for managing insurance contracts linked to clients")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ContractController {

  private final ContractService contractService;

  @Operation(
      summary = "Get contract by ID",
      description = "Retrieves contract details by its unique identifier")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Contract found",
            content = @Content(schema = @Schema(implementation = ContractResponse.class))),
        @ApiResponse(responseCode = "404", description = "Contract not found")
      })
  @GetMapping(value = "/contracts/{contractId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ContractResponse> getContract(
      @Parameter(description = "ID of the contract to be retrieved", required = true) @PathVariable
          Long contractId) {
    ContractResponse response = contractService.getContract(contractId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/clients/{clientId}/contracts")
  @Operation(
      summary = "Update contract cost",
      description = "Updates the cost of an existing contract")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Contract cost updated successfully",
            content = @Content(schema = @Schema(implementation = ContractResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Contract not found")
      })
  @PutMapping(
      value = "/contracts/{contractId}/cost",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ContractResponse> updateContractCost(
      @Parameter(description = "ID of the contract to update", required = true) @PathVariable
          Long contractId,
      @Valid @RequestBody ContractCostUpdateRequest request) {
    ContractResponse response = contractService.updateContractCost(contractId, request);
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Create a new contract",
      description = "Creates a new insurance contract for a client")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Contract created successfully",
            content = @Content(schema = @Schema(implementation = ContractResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Client not found")
      })
  @PostMapping(
      value = "/clients/{clientId}/contracts",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ContractResponse> createContract(
      @Parameter(description = "ID of the client for whom to create the contract", required = true)
          @PathVariable
          Long clientId,
      @Valid @RequestBody ContractRequest request) {
    ContractResponse response = contractService.createContract(clientId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(
      summary = "Get active contracts",
      description =
          "Retrieves all active contracts for a client, optionally filtered by modification date")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of active contracts",
            content = @Content(schema = @Schema(implementation = ContractResponse.class))),
        @ApiResponse(responseCode = "404", description = "Client not found")
      })
  @GetMapping(value = "/clients/{clientId}/contracts", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ContractResponse>> getActiveContracts(
      @Parameter(description = "ID of the client", required = true) @PathVariable Long clientId,
      @Parameter(
              description = "Optional filter to get contracts modified after this date/time",
              required = false,
              example = "2023-01-01T00:00:00")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime modifiedAfter) {
    List<ContractResponse> responses = contractService.getActiveContracts(clientId, modifiedAfter);
    return ResponseEntity.ok(responses);
  }

  @Operation(
      summary = "Get total cost",
      description = "Calculates the total cost of all active contracts for a client")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Total cost calculated successfully",
            content = @Content(schema = @Schema(implementation = TotalCostResponse.class))),
        @ApiResponse(responseCode = "404", description = "Client not found")
      })
  @GetMapping(
      value = "/clients/{clientId}/contracts/total-cost",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<TotalCostResponse> getTotalCost(
      @Parameter(description = "ID of the client", required = true) @PathVariable Long clientId) {
    TotalCostResponse response = contractService.getTotalCost(clientId);
    return ResponseEntity.ok(response);
  }

}
