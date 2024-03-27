package pl.futurecollars.invoicing.controller;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.db.file.FileBasedDatabase;
import pl.futurecollars.invoicing.db.file.IdService;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.InvoiceService;
import pl.futurecollars.invoicing.utils.FilesService;
import pl.futurecollars.invoicing.utils.JsonService;

@RestController
public class FileBasedController {

  private final Path idFilePath = Path.of("id.txt");
  private final Path databasePath = Path.of("invoices.txt");
  private final FilesService filesService = new FilesService();
  private final JsonService jsonService = new JsonService();
  private final IdService idService = new IdService(idFilePath, filesService);

  private final InvoiceService service = new InvoiceService(new FileBasedDatabase<Invoice>(databasePath, idService, filesService, jsonService));

  @GetMapping("/invoices")
  public ResponseEntity<List<Invoice>> getAll() {
    return ResponseEntity.ok().body(service.getAll());
  }

  @PostMapping("/invoices")
  public int addInvoice(@RequestBody Invoice invoice) {
    return service.save(invoice);
  }

  @GetMapping("/invoices/{id}")
  public ResponseEntity<Optional<Invoice>> findById(@PathVariable("id") int id) {
    Optional<Invoice> invoice = service.getById(id);
    try {
      return ResponseEntity.ok().body(invoice);
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("invoices/{id}")
  public ResponseEntity<?> updateInvoice(@PathVariable("id") int id, @RequestBody Invoice invoice) {
    Optional<Invoice> invoiceDetails = service.getById(id);
    service.update(invoiceDetails.get().getId(), invoice);
    try {
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("invoices/{id}")
  public ResponseEntity<?> deleteInvoice(@PathVariable("id") int id) {
    Optional<Invoice> invoice = service.getById(id);
    service.delete(invoice.get().getId());
    try {
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }
}
