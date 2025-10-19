package ch.insurance.api.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "clients")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Client {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  @Column(nullable = false)
  protected String email;

  @NotBlank(message = "Phone is required")
  @Pattern(regexp = "^\\+?[0-9\\s\\-()]{7,20}$", message = "Phone number must be valid")
  @Column(nullable = false)
  protected String phone;

  @Column(name = "created_at", nullable = false, updatable = false)
  protected LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  protected LocalDateTime updatedAt;

  @Column(name = "client_type", nullable = false, updatable = false)
  @Enumerated(EnumType.STRING)
  protected ClientType clientType;

  @PrePersist
  protected void onPrePersist() {
    LocalDateTime now = LocalDateTime.now();
    createdAt = now;
    updatedAt = now;

    // Ensure clientType is set based on the concrete class
    if (this instanceof Person) {
      this.clientType = ClientType.PERSON;
    } else if (this instanceof Company) {
      this.clientType = ClientType.COMPANY;
    }
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public enum ClientType {
    PERSON,
    COMPANY
  }
}
