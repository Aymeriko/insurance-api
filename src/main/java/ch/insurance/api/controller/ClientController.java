package ch.insurance.api.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.insurance.api.dto.ClientResponse;
import ch.insurance.api.dto.ClientUpdateRequest;
import ch.insurance.api.dto.CompanyRequest;
import ch.insurance.api.dto.PersonRequest;
import ch.insurance.api.service.ClientService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

  private final ClientService clientService;

  @PostMapping("/persons")
  public ResponseEntity<ClientResponse> createPerson(@Valid @RequestBody PersonRequest request) {
    ClientResponse response = clientService.createPerson(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/companies")
  public ResponseEntity<ClientResponse> createCompany(@Valid @RequestBody CompanyRequest request) {
    ClientResponse response = clientService.createCompany(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ClientResponse> getClient(@PathVariable Long id) {
    ClientResponse response = clientService.getClientById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<List<ClientResponse>> getAllClients() {
    List<ClientResponse> responses = clientService.getAllClients();
    return ResponseEntity.ok(responses);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ClientResponse> updateClient(
      @PathVariable Long id, @Valid @RequestBody ClientUpdateRequest request) {
    ClientResponse response = clientService.updateClient(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
    clientService.deleteClient(id);
    return ResponseEntity.noContent().build();
  }
}
