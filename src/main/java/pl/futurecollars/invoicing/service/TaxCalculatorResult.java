package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaxCalculatorResult {

  private BigDecimal income;
  private BigDecimal costs;
  private BigDecimal earnings;

  private BigDecimal collectedVat;
  private BigDecimal paidVat;
  private BigDecimal vatToReturn;
}
