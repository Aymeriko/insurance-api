package ch.insurance.api.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonRequest {

  @NotBlank(message = "First name is required")
  private String firstName;

  @NotBlank(message = "Last name is required")
  private String lastName;

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  private String email;

  @NotBlank(message = "Phone is required")
  @Pattern(regexp = "^\\+?[0-9\\s\\-()]{7,20}$", message = "Phone number must be valid")
  private String phone;

  @NotNull(message = "Birthdate is required")
  @Past(message = "Birthdate must be in the past")
  private LocalDate birthDate;

  @NotNull(message = "Client type is mandatory")
  @Pattern(regexp = "PERSON", message = "Client type must be 'PERSON' for person requests")
  private String clientType;
}
