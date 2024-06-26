package pl.futurecollars.invoicing.controller.tax;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.service.tax.TaxCalculatorResult;

@CrossOrigin
@RequestMapping(value = "tax", produces = {"application/json;charset=UTF-8"})
@Api(tags = {"tax-controller"})
public interface TaxCalculatorApi {

  @ApiOperation(value = "Get incomes, costs, vat and taxes to pay")
  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  ResponseEntity<TaxCalculatorResult> calculateTaxes(@RequestBody Company company);

}
