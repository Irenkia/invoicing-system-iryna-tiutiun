package pl.futurecollars.invoicing.service.tax;

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

  private BigDecimal incomeMinusCosts;
  private BigDecimal pensionInsurance;
  private BigDecimal incomeMinusCostsMinusPensionInsurance;
  private BigDecimal incomeMinusCostsMinusPensionInsuranceRounded;
  private BigDecimal incomeTax;

  private BigDecimal healthInsurancePaid;
  private BigDecimal healthInsuranceToSubtract;
  private BigDecimal incomeTaxMinusHealthInsurance;
  private BigDecimal finalIncomeTax;

  private BigDecimal collectedVat;
  private BigDecimal paidVat;
  private BigDecimal vatToReturn;
}
