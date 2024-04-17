package pl.futurecollars.invoicing.controller.tax;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.service.TaxCalculatorResult;
import pl.futurecollars.invoicing.service.TaxCalculatorService;

@RestController
@AllArgsConstructor
public class TaxCalculatorController implements TaxCalculatorApi {

  private final TaxCalculatorService taxService;

  @Override
  public TaxCalculatorResult calculateTaxes(@PathVariable @ApiParam(example = "552-168-66-00") String taxIdentificationNumber) {
    return taxService.calculateTaxes(taxIdentificationNumber);
  }
}
