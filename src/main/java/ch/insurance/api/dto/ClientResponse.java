package ch.insurance.api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponse {

  private Long id;
  private String clientType;
  private String email;
  private String phone;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // for person
  private String firstName;
  private String lastName;
  private LocalDate birthDate;

  // for company
  private String companyName;
  private String companyIdentifier;
}
