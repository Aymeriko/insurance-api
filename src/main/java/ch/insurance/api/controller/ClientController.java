package ch.insurance.api.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.insurance.api.dto.ClientResponse;
import ch.insurance.api.dto.ClientUpdateRequest;
import ch.insurance.api.dto.CompanyRequest;
import ch.insurance.api.dto.PersonRequest;
import ch.insurance.api.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Clients", description = "API for managing insurance clients (persons or companies)")
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

  private final ClientService clientService;

  @Operation(
      summary = "Create a new person client",
      description = "Creates a new individual client with personal information")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Person client created successfully",
            content = @Content(schema = @Schema(implementation = ClientResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input")
      })
  @PostMapping(
      value = "/persons",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ClientResponse> createPerson(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Person details to create",
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON_VALUE,
                      schema = @Schema(implementation = PersonRequest.class)))
          @Valid
          @RequestBody
          PersonRequest request) {
    ClientResponse response = clientService.createPerson(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(
      summary = "Create a new company client",
      description = "Creates a new corporate client with company information")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Company client created successfully",
            content = @Content(schema = @Schema(implementation = ClientResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input")
      })
  @PostMapping(
      value = "/companies",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ClientResponse> createCompany(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Company details to create",
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON_VALUE,
                      schema = @Schema(implementation = CompanyRequest.class)))
          @Valid
          @RequestBody
          CompanyRequest request) {
    ClientResponse response = clientService.createCompany(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(
      summary = "Get client by ID",
      description = "Retrieves detailed information about a specific client")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Client found",
            content = @Content(schema = @Schema(implementation = ClientResponse.class))),
        @ApiResponse(responseCode = "404", description = "Client not found")
      })
  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ClientResponse> getClient(
      @Parameter(description = "ID of the client to retrieve", required = true, example = "1")
          @PathVariable
          Long id) {
    ClientResponse response = clientService.getClientById(id);
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Get all clients",
      description = "Retrieves a list of all clients in the system")
  @ApiResponse(
      responseCode = "200",
      description = "List of all clients",
      content = @Content(schema = @Schema(implementation = ClientResponse.class)))
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ClientResponse>> getAllClients() {
    List<ClientResponse> responses = clientService.getAllClients();
    return ResponseEntity.ok(responses);
  }

  @Operation(
      summary = "Update client information",
      description = "Updates the information of an existing client")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Client updated successfully",
            content = @Content(schema = @Schema(implementation = ClientResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Client not found")
      })
  @PutMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ClientResponse> updateClient(
      @Parameter(description = "ID of the client to update", required = true, example = "1")
          @PathVariable
          Long id,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Updated client information",
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON_VALUE,
                      schema = @Schema(implementation = ClientUpdateRequest.class)))
          @Valid
          @RequestBody
          ClientUpdateRequest request) {
    ClientResponse response = clientService.updateClient(id, request);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Delete a client", description = "Deletes a client and all associated data")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Client deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Client not found")
      })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteClient(
      @Parameter(description = "ID of the client to delete", required = true, example = "1")
          @PathVariable
          Long id) {
    clientService.deleteClient(id);
    return ResponseEntity.noContent().build();
  }
}
