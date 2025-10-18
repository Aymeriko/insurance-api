package ch.insurance.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractCostUpdateRequest {

    @NotNull(message = "Cost amount is required")
    @DecimalMin(value = "0.01", message = "Cost amount must be positive")
    private BigDecimal costAmount;
}
