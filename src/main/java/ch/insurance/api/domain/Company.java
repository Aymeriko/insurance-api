package ch.insurance.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("COMPANY")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Company extends Client {

    @NotBlank(message = "Company identifier is required")
    @Pattern(regexp = "^[A-Z]{3}-[0-9]{3}$", message = "Company identifier must match format: XXX-123")
    @Column(name = "company_identifier", unique = true, updatable = false)
    private String companyIdentifier;

    @Override
    public String getClientType() {
        return "COMPANY";
    }
}
