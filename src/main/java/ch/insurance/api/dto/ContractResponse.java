package ch.insurance.api.dto;

import java.math.BigDecimal;
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
public class ContractResponse {

  private Long id;
  private Long clientId;
  private LocalDate startDate;
  private LocalDate endDate;
  private BigDecimal costAmount;
  private LocalDateTime createdAt;
}
