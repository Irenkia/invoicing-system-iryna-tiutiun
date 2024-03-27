package pl.futurecollars.invoicing.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.InvoiceService;

import java.util.List;
import java.util.Optional;

//@RestController
public class MemoryController {

    private final InvoiceService service = new InvoiceService(new InMemoryDatabase<Invoice>());

    @GetMapping("/invoices")
    public ResponseEntity<List<Invoice>> getAll() {
        return  ResponseEntity.ok().body(service.getAll());
    }

    @PostMapping("/invoices")
    public int addInvoice(@RequestBody Invoice invoice) {
        return service.save(invoice);
    }

    @GetMapping("/invoices/{id}")
    public ResponseEntity<Optional<Invoice>> findById(@PathVariable("id") int id) {
        Optional<Invoice> invoice = service.getById(id);
        try{
            return ResponseEntity.ok().body(invoice);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("invoices/{id}")
    public ResponseEntity<?> updateInvoice(@PathVariable("id") int id, @RequestBody Invoice invoice) {
        Optional<Invoice> invoiceDetails = service.getById(id);
        service.update(invoiceDetails.get().getId(),invoice);
        try{
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("invoices/{id}")
    public ResponseEntity<?> deleteInvoice(@PathVariable("id") int id) {
        Optional<Invoice> invoice = service.getById(id);
        service.delete(invoice.get().getId());
        try{
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
