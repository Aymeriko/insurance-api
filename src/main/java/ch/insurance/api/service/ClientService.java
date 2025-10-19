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
import java.time.LocalDateTime;
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
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();

        Person savedPerson = clientRepository.save(person);
        
        return mapToResponse(savedPerson);
    }

    @Transactional
    public ClientResponse createCompany(CompanyRequest request) {
        Company company = Company.builder()
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

        if (client.getClientType().equals(Client.ClientType.PERSON) && client instanceof Person person) {
            // Handle person update
            String newFirstName = request.getFirstName() != null ? request.getFirstName() : person.getFirstName();
            String newLastName = request.getLastName() != null ? request.getLastName() : person.getLastName();
            LocalDate newBirthdate = request.getBirthDate() != null ? request.getBirthDate() : person.getBirthDate();

            person.setFirstName(newFirstName);
            person.setLastName(newLastName);
            person.setBirthDate(newBirthdate);

        } else if (client.getClientType().equals(Client.ClientType.COMPANY) && client instanceof Company company) {
            // Handle company update
            String newCompanyIdentifier = request.getCompanyIdentifier() != null ? request.getCompanyIdentifier() : company.getCompanyIdentifier();
            company.setCompanyIdentifier(newCompanyIdentifier);
        }
        
        if (request.getEmail() != null) {
            client.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            client.setPhone(request.getPhone());
        }
        if (request.getPhone() != null) {
            client.setPhone(request.getPhone());
        }

        if (request.getCreatedAt() != null) {
            client.setCreatedAt(request.getCreatedAt());
        }
        if (request.getUpdatedAt() != null) {
            client.setUpdatedAt(request.getUpdatedAt());
        } else {
            client.setUpdatedAt(LocalDateTime.now());
        }

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

        if (client.getClientType() == null)
            throw new IllegalStateException("Unknown client type");

        ClientResponse.ClientResponseBuilder builder = ClientResponse.builder()
                .id(client.getId())
                .clientType(client.getClientType().name())
                .email(client.getEmail())
                .phone(client.getPhone())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt());

        if (client.getClientType().equals(Client.ClientType.PERSON)) {
            builder.firstName(((Person) client).getFirstName());
            builder.lastName(((Person) client).getLastName());
            builder.birthDate(((Person) client).getBirthDate());
        } else if (client.getClientType().equals(Client.ClientType.COMPANY)) {
            builder.companyIdentifier(((Company) client).getCompanyIdentifier());
        }

        return builder.build();
    }
}
