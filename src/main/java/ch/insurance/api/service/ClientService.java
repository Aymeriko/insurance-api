package ch.insurance.api.service;

import ch.insurance.api.domain.Client;
import ch.insurance.api.domain.Company;
import ch.insurance.api.domain.Contract;
import ch.insurance.api.domain.Person;
import ch.insurance.api.dto.ClientResponse;
import ch.insurance.api.dto.ClientUpdateRequest;
import ch.insurance.api.dto.CompanyRequest;
import ch.insurance.api.dto.PersonRequest;
import ch.insurance.api.exception.ResourceNotFoundException;
import ch.insurance.api.repository.ClientRepository;
import ch.insurance.api.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ContractRepository contractRepository;

    @Transactional
    public ClientResponse createPerson(PersonRequest request) {
        Person person = Person.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .birthdate(request.getBirthdate())
                .build();

        Person savedPerson = clientRepository.save(person);
        return mapToResponse(savedPerson);
    }

    @Transactional
    public ClientResponse createCompany(CompanyRequest request) {
        Company company = Company.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .companyIdentifier(request.getCompanyIdentifier())
                .build();

        Company savedCompany = clientRepository.save(company);
        return mapToResponse(savedCompany);
    }

    @Transactional(readOnly = true)
    public ClientResponse getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        return mapToResponse(client);
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> getAllClients() {
        return clientRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClientResponse updateClient(Long id, ClientUpdateRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));

        client.setName(request.getName());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());

        Client updatedClient = clientRepository.save(client);
        return mapToResponse(updatedClient);
    }

    @Transactional
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));

        List<Contract> contracts = contractRepository.findByClientId(id);
        LocalDate currentDate = LocalDate.now();

        contracts.forEach(contract -> {
            if (contract.getEndDate() == null || contract.getEndDate().isAfter(currentDate)) {
                contract.setEndDate(currentDate);
                contractRepository.save(contract);
            }
        });

        clientRepository.delete(client);
    }

    private ClientResponse mapToResponse(Client client) {
        ClientResponse.ClientResponseBuilder builder = ClientResponse.builder()
                .id(client.getId())
                .clientType(client.getClientType())
                .name(client.getName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt());

        if (client instanceof Person person) {
            builder.birthdate(person.getBirthdate());
        } else if (client instanceof Company company) {
            builder.companyIdentifier(company.getCompanyIdentifier());
        }

        return builder.build();
    }
}
