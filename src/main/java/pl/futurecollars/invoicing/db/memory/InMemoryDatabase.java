package pl.futurecollars.invoicing.db.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.NoArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

@NoArgsConstructor
public class InMemoryDatabase implements Database {

  private final Map<Long, Invoice> invoices = new HashMap<>();
  private Long nextId = 1L;

  @Override
  public Long save(Invoice invoice) {
    invoice.setId(nextId);
    invoices.put(nextId, invoice);
    return nextId++;
  }

  @Override
  public Optional<Invoice> getById(Long id) {
    return Optional.ofNullable(invoices.get(id));
  }

  @Override
  public List<Invoice> getAll() {
    return new ArrayList<>(invoices.values());
  }

  @Override
  public Optional<Invoice> update(Long id, Invoice updatedInvoice) {
    Optional<Invoice> invoice = Optional.ofNullable(invoices.get(id));
    if (invoices.containsKey(id)) {
      updatedInvoice.setId(id);
      invoices.put(id, updatedInvoice);
    }
    return invoice;
  }

  @Override
  public Optional<Invoice> delete(Long id) {
    return Optional.ofNullable(invoices.remove(id));
  }
}
