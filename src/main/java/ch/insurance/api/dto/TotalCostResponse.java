package ch.insurance.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalCostResponse {

    private Long clientId;
    private BigDecimal totalCost;
    private Long activeContractsCount;
}
