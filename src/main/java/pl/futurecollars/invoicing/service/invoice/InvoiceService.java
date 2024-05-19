package pl.futurecollars.invoicing.service.invoice;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

@Service
public class InvoiceService {

  private final Database<Invoice> database;

  public InvoiceService(Database<Invoice> database) {
    this.database = database;
  }

  public Long save(Invoice invoice) {
    return database.save(invoice);
  }

  public Optional<Invoice> getById(Long id) {
    return database.getById(id);
  }

  public List<Invoice> getAll() {
    return database.getAll();
  }

  public Optional<Invoice> update(Long id, Invoice updatedInvoice) {
    return database.update(id, updatedInvoice);
  }

  public Optional<Invoice> delete(Long id) {

    return database.delete(id);
  }

}
