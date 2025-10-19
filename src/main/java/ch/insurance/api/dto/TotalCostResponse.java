package ch.insurance.api.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalCostResponse {

  private Long clientId;
  private BigDecimal totalCost;
  private Long activeContractsCount;
}
