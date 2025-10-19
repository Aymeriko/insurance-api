package ch.insurance.api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "company_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Company extends Client{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Company identifier is required")
    @Column(name = "company_identifier", nullable = false)
    private String companyIdentifier;

    public String getName() {
        return companyIdentifier != null ? companyIdentifier : "";
    }

}
