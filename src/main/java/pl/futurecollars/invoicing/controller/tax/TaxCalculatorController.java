package pl.futurecollars.invoicing.controller.tax;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.service.tax.TaxCalculatorResult;
import pl.futurecollars.invoicing.service.tax.TaxCalculatorService;

@RestController
@AllArgsConstructor
public class TaxCalculatorController implements TaxCalculatorApi {

  private final TaxCalculatorService taxService;

  @Override
  public ResponseEntity<TaxCalculatorResult> calculateTaxes(@RequestBody Company company) {
    return ResponseEntity.ok().body(taxService.calculateTaxes(company));
  }

}
