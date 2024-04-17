package pl.futurecollars.invoicing.controller;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.controller.InvoiceApi;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.InvoiceService;

@RestController
@AllArgsConstructor
public class InvoiceController implements InvoiceApi {

  private final InvoiceService service;

  @Override
  public ResponseEntity<List<Invoice>> getAll() {
    return ResponseEntity.ok().body(service.getAll());
  }

  @Override
  public int addInvoice(@RequestBody Invoice invoice) {
    return service.save(invoice);
  }

  @Override
  public ResponseEntity<Optional<Invoice>> findById(@PathVariable("id") int id) {
    return Optional.ofNullable(service.getById(id))
        .map(invoice -> ResponseEntity.ok().body(invoice))
        .orElse(ResponseEntity.notFound().build());
  }

  @Override
  public ResponseEntity<Optional<Invoice>> updateInvoice(@PathVariable("id") int id, @RequestBody Invoice invoice) {
    return Optional.of(service.update(id, invoice))
        .map(ResponseEntity.ok()::body)
        .orElse(ResponseEntity.notFound().build());
  }

  @Override
  public ResponseEntity<Optional<Invoice>> deleteInvoice(@PathVariable("id") int id) {
    return Optional.of(service.delete(id))
        .map(ResponseEntity.ok()::body)
        .orElse(ResponseEntity.notFound().build());
  }
}
