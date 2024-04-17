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

  private BigDecimal incomingVat;
  private BigDecimal outgoingVat;
  private BigDecimal vatToPay;
}
