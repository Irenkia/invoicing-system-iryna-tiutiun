package pl.futurecollars.invoicing.service;

import java.util.List;
import java.util.Optional;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

public class InvoiceService {

  private final Database<Invoice> database;

  public InvoiceService(Database<Invoice> database) {
    this.database = database;
  }

  public int save(Invoice invoice) {
    return database.save(invoice);
  }

  public Optional<Invoice> getById(int id) {
    return database.getById(id);
  }

  public List<Invoice> getAll() {
    return database.getAll();
  }

  public int update(int id, Invoice updatedInvoice) {
    database.update(id, updatedInvoice);
    return id;
  }

  public void delete(int id) {
    database.delete(id);
  }
}
