package ch.insurance.api.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonFormat;

import ch.insurance.api.domain.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientUpdateRequest {

  private Client.ClientType clientType;

  @Email(message = "Email must be valid")
  private String email;

  @Pattern(regexp = "^\\+?[0-9\\s\\-()]{7,20}$", message = "Phone number must be valid")
  private String phone;

  // For Person
  private String firstName;
  private String lastName;

  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX]",
      timezone = "Europe/Zurich")
  private LocalDateTime createdAt;

  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX]",
      timezone = "Europe/Zurich")
  private LocalDateTime updatedAt;
}
