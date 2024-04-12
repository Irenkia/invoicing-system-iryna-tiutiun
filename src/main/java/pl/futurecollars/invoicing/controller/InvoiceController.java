package pl.futurecollars.invoicing.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.InvoiceService;

@RestController
@RequestMapping("/invoices")
@Api(tags = {"invoice-controller"})
public class InvoiceController {

  @Autowired
  private final InvoiceService service;

  @Autowired
  public InvoiceController(InvoiceService service) {
    this.service = service;
  }

  @ApiOperation(value = "Get list of all invoices")
  @GetMapping(produces = {"application/json;charset=UTF-8"})
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<List<Invoice>> getAll() {
    return ResponseEntity.ok().body(service.getAll());
  }

  @ApiOperation(value = "Add new invoice to system")
  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public int addInvoice(@RequestBody Invoice invoice) {
    return service.save(invoice);
  }

  @ApiOperation(value = "Get invoice by id")
  @GetMapping(value = "/{id}", produces = {"application/json;charset=UTF-8"})
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Optional<Invoice>> findById(@PathVariable("id") int id) {
    return Optional.ofNullable(service.getById(id))
        .map(invoice -> ResponseEntity.ok().body(invoice))
        .orElse(ResponseEntity.notFound().build());
  }

  @ApiOperation(value = "Update invoice with given id")
  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Optional<Invoice>> updateInvoice(@PathVariable("id") int id, @RequestBody Invoice invoice) {
    return Optional.of(service.update(id, invoice))
        .map(ResponseEntity.ok()::body)
        .orElse(ResponseEntity.notFound().build());
  }

  @ApiOperation(value = "Delete invoice with given id")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Optional<Invoice>> deleteInvoice(@PathVariable("id") int id) {
    return Optional.of(service.delete(id))
        .map(ResponseEntity.ok()::body)
        .orElse(ResponseEntity.notFound().build());
  }
}
