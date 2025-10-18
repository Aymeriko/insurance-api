package ch.insurance.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponse {

    private Long id;
    private String clientType;
    private String name;
    private String email;
    private String phone;
    private LocalDate birthdate;
    private String companyIdentifier;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
