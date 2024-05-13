package pl.futurecollars.invoicing.controller.invoice;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.futurecollars.invoicing.model.Invoice;

@RequestMapping("/invoices")
@Api(tags = {"invoice-controller"})
public interface InvoiceApi {

  @ApiOperation(value = "Get list of all invoices")
  @GetMapping(produces = {"application/json;charset=UTF-8"})
  @ResponseStatus(HttpStatus.CREATED)
  ResponseEntity<List<Invoice>> getAll();

  @ApiOperation(value = "Add new invoice to system")
  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  Long addInvoice(@RequestBody Invoice invoice);

  @ApiOperation(value = "Get invoice by id")
  @GetMapping(value = "/{id}", produces = {"application/json;charset=UTF-8"})
  @ResponseStatus(HttpStatus.OK)
  ResponseEntity<Optional<Invoice>> findById(@PathVariable("id") Long id);

  @ApiOperation(value = "Update invoice with given id")
  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  ResponseEntity<Optional<Invoice>> updateInvoice(@PathVariable("id") Long id, @RequestBody Invoice invoice);

  @ApiOperation(value = "Delete invoice with given id")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  ResponseEntity<Optional<Invoice>> deleteInvoice(@PathVariable("id") Long id);
}
