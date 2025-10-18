package ch.vaudoise.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("PERSON")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Person extends Client {

    @NotNull(message = "Birthdate is required for person")
    @Past(message = "Birthdate must be in the past")
    @Column(name = "birthdate", nullable = false, updatable = false)
    private LocalDate birthdate;

    @Override
    public String getClientType() {
        return "PERSON";
    }
}
