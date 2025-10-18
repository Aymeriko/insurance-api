package ch.insurance.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[0-9\\s\\-()]{7,20}$", message = "Phone number must be valid")
    private String phone;

    @NotBlank(message = "Company identifier is required")
    @Pattern(regexp = "^[A-Z]{3}-[0-9]{3}$", message = "Company identifier must match format: XXX-123")
    private String companyIdentifier;
}
