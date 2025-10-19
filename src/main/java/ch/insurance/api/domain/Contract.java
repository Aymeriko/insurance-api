package ch.insurance.api.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "contracts",
    indexes = {
      @Index(name = "idx_client_id", columnList = "client_id"),
      @Index(name = "idx_contract_id", columnList = "id")
    })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "client_id", nullable = false)
  private Client client;

  @NotNull(message = "Start date is required")
  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date")
  private LocalDate endDate;

  @NotNull(message = "Cost amount is required")
  @DecimalMin(value = "0.01", message = "Cost amount must be positive")
  @Column(name = "cost_amount", nullable = false, precision = 19, scale = 2)
  private BigDecimal costAmount;

  @Column(name = "last_modified_date", nullable = false)
  private LocalDateTime lastModifiedDate;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    if (startDate == null) {
      startDate = LocalDate.now();
    }
    createdAt = LocalDateTime.now();
    lastModifiedDate = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    lastModifiedDate = LocalDateTime.now();
  }

  public boolean isActive() {
    LocalDate today = LocalDate.now();
    return endDate == null || endDate.isAfter(today);
  }
}
